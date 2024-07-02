package riccardogulin.u5d12.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import riccardogulin.u5d12.entities.User;
import riccardogulin.u5d12.exceptions.BadRequestException;
import riccardogulin.u5d12.exceptions.NotFoundException;
import riccardogulin.u5d12.payloads.NewUserDTO;
import riccardogulin.u5d12.repositories.UsersRepository;

import java.util.UUID;

@Service
public class UsersService {
	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private PasswordEncoder bcrypt;

	public Page<User> getUsers(int pageNumber, int pageSize, String sortBy) {
		if (pageSize > 100) pageSize = 100;
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
		return usersRepository.findAll(pageable);
	}

	public User save(NewUserDTO body) {
		// 1. Verifico se l'email è già in uso
		this.usersRepository.findByEmail(body.email()).ifPresent(
				// 1.1 Se lo è triggero un errore
				user -> {
					throw new BadRequestException("L'email " + body.email() + " è già in uso!");
				}
		);

		// 2. Altrimenti creiamo un nuovo oggetto User e oltre a prendere i valori dal body, aggiungiamo l'avatarURL (ed eventuali altri campi server-generated)
		User newUser = new User(body.name(), body.surname(), body.email(), bcrypt.encode(body.password()));

		newUser.setAvatarURL("https://ui-avatars.com/api/?name=" + body.name() + "+" + body.surname());

		// 3. Poi salviamo lo user
		return usersRepository.save(newUser);
	}

	public User findById(UUID userId) {
		return this.usersRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
	}

	public User findByIdAndUpdate(UUID userId, User modifiedUser) {
		User found = this.findById(userId);
		found.setName(modifiedUser.getName());
		found.setSurname(modifiedUser.getSurname());
		found.setEmail(modifiedUser.getEmail());
		found.setPassword(modifiedUser.getPassword());
		found.setAvatarURL("https://ui-avatars.com/api/?name=" + modifiedUser.getName() + "+" + modifiedUser.getSurname());
		return this.usersRepository.save(found);
	}

	public void findByIdAndDelete(UUID userId) {
		User found = this.findById(userId);
		this.usersRepository.delete(found);
	}

	public User findByEmail(String email){
		return usersRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Utente con email " + email + " non trovato!"));
	}

}
