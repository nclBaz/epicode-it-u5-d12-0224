package riccardogulin.u5d12.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import riccardogulin.u5d12.entities.User;
import riccardogulin.u5d12.services.UsersService;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UsersController {
	@Autowired
	private UsersService usersService;

	@GetMapping
	public Page<User> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sortBy) {
		return this.usersService.getUsers(page, size, sortBy);
	}

	@GetMapping("/me")
	public User getProfile(@AuthenticationPrincipal User currentAuthenticatedUser){
		return currentAuthenticatedUser;
	}

	@PutMapping("/me")
	public User updateProfile(@AuthenticationPrincipal User currentAuthenticatedUser, @RequestBody User body){
		return this.usersService.findByIdAndUpdate(currentAuthenticatedUser.getId(), body);
	}

	@DeleteMapping("/me")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteProfile(@AuthenticationPrincipal User currentAuthenticatedUser){
		this.usersService.findByIdAndDelete(currentAuthenticatedUser.getId());
	}

	@GetMapping("/{userId}")
	public User findById(@PathVariable UUID userId) {
		return this.usersService.findById(userId);
	}

	@PutMapping("/{userId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public User findByIdAndUpdate(@PathVariable UUID userId, @RequestBody User body) {
		return this.usersService.findByIdAndUpdate(userId, body);
	}

	@DeleteMapping("/{userId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void findByIdAndDelete(@PathVariable UUID userId) {
		this.usersService.findByIdAndDelete(userId);
	}

}
