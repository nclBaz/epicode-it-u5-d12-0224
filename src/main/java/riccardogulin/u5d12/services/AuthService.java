package riccardogulin.u5d12.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import riccardogulin.u5d12.entities.User;
import riccardogulin.u5d12.exceptions.UnauthorizedException;
import riccardogulin.u5d12.payloads.UserLoginDTO;
import riccardogulin.u5d12.security.JWTTools;

@Service
public class AuthService {
	@Autowired
	private UsersService usersService;

	@Autowired
	private PasswordEncoder bcrypt;

	@Autowired
	private JWTTools jwtTools;

	public String authenticateUserAndGenerateToken(UserLoginDTO payload){

		User user = this.usersService.findByEmail(payload.email());
		// 1.2 Verifico se la password combacia con quella ricevuta nel payload
		if(bcrypt.matches(payload.password(), user.getPassword())){
			// 2. Se tutto è OK --> Genero un token per tale utente e lo torno
			return jwtTools.createToken(user);
		} else {
			// 3. Se le credenziali sono errate --> 401 (Unauthorized)
			throw new UnauthorizedException("Credenziali non corrette!");
		}
	}
}
