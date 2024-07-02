package riccardogulin.u5d12.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import riccardogulin.u5d12.entities.User;
import riccardogulin.u5d12.exceptions.UnauthorizedException;
import riccardogulin.u5d12.services.UsersService;

import java.io.IOException;
import java.util.UUID;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {

	@Autowired
	private JWTTools jwtTools;
	@Autowired
	private UsersService usersService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		// Il codice di questo metodo verrà eseguito ad ogni richiesta che richieda di essere autenticati

		// Cose da fare:
		// 1. Controlliamo se nella richiesta corrente ci sia un Authorization Header, se non c'è --> 401
		String authHeader = request.getHeader("Authorization"); // "Authorization": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MTk4MjkwNjksImV4cCI6MTcyMDQzMzg2OSwic3ViIjoiOTFkMTg2MGItZjE2Yy00MTYwLWIyYTYtODU2NWY0MzY5MTBiIn0.1hEDloV0FbYnw5U8mwg0CsIWLVos6qZSYPOCJUyhG8o"

		if(authHeader == null || !authHeader.startsWith("Bearer ")) throw new UnauthorizedException("Per favore inserisci correttamente il token nell'header");

		// 2. Se c'è estraiamo il token dall'header
		String accessToken = authHeader.substring(7); // "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MTk4MjkwNjksImV4cCI6MTcyMDQzMzg2OSwic3ViIjoiOTFkMTg2MGItZjE2Yy00MTYwLWIyYTYtODU2NWY0MzY5MTBiIn0.1hEDloV0FbYnw5U8mwg0CsIWLVos6qZSYPOCJUyhG8o"

		// 3. Verifichiamo se il token è stato manipolato (verifica della signature) e se non è scaduto (verifica dell'Expiration Date)
		jwtTools.verifyToken(accessToken);

		// 4. Se tutto è OK, proseguiamo con il prossimo elemento della Filter Chain, prima o poi arriveremo all'endpoint
		// Se vogliamo abilitare l'autorizzazione dobbiamo 'informare' Spring Security di chi sia l'utente che sta effettuando la richiesta

		// 4.1 Cerco l'utente tramite id (l'id sta nel token..)
		String userId = jwtTools.extractIdFromToken(accessToken);
		User currentUser = usersService.findById(UUID.fromString(userId));

		// 4.2 Trovato l'utente posso associarlo al Security Context, praticamente questo equivale ad 'associare' l'utente autenticato alla richiesta corrente
		Authentication authentication = new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
		// Il terzo parametro è OBBLIGATORIO se si vuol poter usare i vari @PreAuthorize perché esso contiene la lista dei ruoli dell'utente
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// 4.3 Vado al prossimo elemento della catena, passandogli la richiesta corrente e l'oggetto response
		filterChain.doFilter(request, response);

		// 5. Se il token non è OK --> 401
	}

	// Facendo l'override del seguente metodo, posso disabilitare l'esecuzione del filtro per determinati endpoint
	// Nel nostro caso ci interessa che il filtro non venga azionato per le richieste di tipo Login o Register (esse ovviamente non devono richiedere un token!)
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		// Uso questo metodo per specificare in quali situazioni NON FILTRARE
		// Posso ad esempio escludere dal controllo del filtro tutti gli endpoint dentro il controller /auth
		return new AntPathMatcher().match("/auth/**", request.getServletPath());
	}
}
