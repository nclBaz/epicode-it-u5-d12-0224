package riccardogulin.u5d12.exceptions;

import lombok.Getter;
import org.springframework.validation.ObjectError;

import java.util.List;

@Getter
public class BadRequestException extends RuntimeException {
	private List<ObjectError> errorsList;

	public BadRequestException(String message) {
		super(message);
	}

	public BadRequestException(List<ObjectError> errorsList) {
		super("Ci sono stati errori di validazione del payload");
		this.errorsList = errorsList;
	}
}
