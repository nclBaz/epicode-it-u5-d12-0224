package riccardogulin.u5d12.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import riccardogulin.u5d12.exceptions.BadRequestException;
import riccardogulin.u5d12.payloads.NewUserDTO;
import riccardogulin.u5d12.payloads.NewUserResponseDTO;
import riccardogulin.u5d12.payloads.UserLoginDTO;
import riccardogulin.u5d12.payloads.UserLoginResponseDTO;
import riccardogulin.u5d12.services.AuthService;
import riccardogulin.u5d12.services.UsersService;

@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	private AuthService authService;
	@Autowired
	private UsersService usersService;

	@PostMapping("/login")
	public UserLoginResponseDTO login(@RequestBody UserLoginDTO payload){
		return new UserLoginResponseDTO(authService.authenticateUserAndGenerateToken(payload));
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public NewUserResponseDTO saveUser(@RequestBody @Validated NewUserDTO body, BindingResult validationResult) {
		if (validationResult.hasErrors()) {
			throw new BadRequestException(validationResult.getAllErrors());
		}
		return new NewUserResponseDTO(this.usersService.save(body).getId());
	}
}
