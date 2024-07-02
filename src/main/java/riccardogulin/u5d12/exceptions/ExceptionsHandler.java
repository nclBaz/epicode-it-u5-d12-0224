package riccardogulin.u5d12.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import riccardogulin.u5d12.payloads.ErrorsDTO;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice // <-- OBBLIGATORIA
// Controller specifico per la GESTIONE DELLE ECCEZIONI
// Questo significa che non inseriremo dei metodi che rappresentano degli endpoint qua,
// bensì inseriremo dei metodi che gestiscono le varie eccezioni con opportuni status code.
// Per gestire le varie eccezioni i metodi useranno l'annotazione @ExceptionHandler
// Questa classe quindi ci serve per CENTRALIZZARE LA GESTIONE DELLE ECCEZIONI.
/// In parole povere, invece di gestire le eccezioni endpoint per endpoint, creiamo
// un unico punto all'interno di tutta l'applicazione in cui "catturare" le eccezioni.
// Le eccezioni possono provenire dai controllers, dai services, o anche da altre parti,
// ma non importa la provenienza, ci importa solo che arrivino qua.
// Questo ci consentirà di gestire ogni tipo di eccezione andando ad inviare una risposta
// adatta per tale problematica, impostando status code corretto ed un payload contenente un
// messaggio che spieghi quale sia stato il problema.

public class ExceptionsHandler {

	@ExceptionHandler(BadRequestException.class) // Nelle parentesi indico quale eccezione debba venir gestita da questo metodo
	// Questo metodo dovrà rispondere con 400
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorsDTO handleBadRequest(BadRequestException ex) {
		if (ex.getErrorsList() != null) {
			// Se c'è la lista degli errori allora nel payload metterò la lista di messaggi di errore concatenati
			String message = ex.getErrorsList().stream().map(objectError -> objectError.getDefaultMessage()).collect(Collectors.joining(". "));
			return new ErrorsDTO(message, LocalDateTime.now());

		} else {
			// Se la lista degli errori è null mandiamo un classico payload di errore con semplice messaggio
			return new ErrorsDTO(ex.getMessage(), LocalDateTime.now());
		}
	}

	@ExceptionHandler(UnauthorizedException.class)
	// Questo metodo dovrà rispondere con 401
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ErrorsDTO handleUnauthorized(UnauthorizedException ex) {
		return new ErrorsDTO(ex.getMessage(), LocalDateTime.now());
	}

	@ExceptionHandler(AuthorizationDeniedException.class)
	// Questo metodo dovrà rispondere con 403
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorsDTO handleForbidden(AuthorizationDeniedException ex) {
		return new ErrorsDTO("Non hai accesso a questa funzionalità", LocalDateTime.now());
	}

	@ExceptionHandler(NotFoundException.class)
	// Questo metodo dovrà rispondere con 404
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorsDTO handleNotFound(NotFoundException ex) {
		return new ErrorsDTO(ex.getMessage(), LocalDateTime.now());
	}

	@ExceptionHandler(Exception.class)
	// Questo metodo dovrà rispondere con 500
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorsDTO handleGenericErrors(Exception ex) {
		ex.printStackTrace(); // Non dimentichiamoci che è ESTREMAMENTE UTILE sapere dove è stato causato l'errore per poterlo fixare!
		return new ErrorsDTO("Problema lato server! Giuro che lo risolveremo presto!", LocalDateTime.now());
	}
}
