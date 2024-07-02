package riccardogulin.u5d12.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record NewUserDTO(
		@NotEmpty(message = "Il nome proprio è un dato obbligatorio!")
		@Size(min = 3, max = 40, message = "Il nome proprio deve essere compreso tra i 3 ed i 40 caratteri!")
		String name,
		@NotEmpty(message = "Il cognome è un dato obbligatorio!")
		@Size(min = 3, max = 40, message = "Il cognome deve essere compreso tra i 3 ed i 40 caratteri!")
		String surname,
		@NotEmpty(message = "L'email è un dato obbligatorio!")
		@Email(message = "L'email inserita non è valida!")
		String email,
		@NotEmpty(message = "La password è un dato obbligatorio!")
		@Size(min = 4, message = "La password deve avere almeno 4 caratteri!")
		String password) {
}
