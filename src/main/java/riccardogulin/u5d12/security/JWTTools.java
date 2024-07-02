package riccardogulin.u5d12.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import riccardogulin.u5d12.entities.User;
import riccardogulin.u5d12.exceptions.UnauthorizedException;

import java.util.Date;

@Component
public class JWTTools {

	@Value("${jwt.secret}")
	private String secret;

	public String createToken(User user){ // Dato l'utente generami un token per esso
		return Jwts.builder()
				.issuedAt(new Date(System.currentTimeMillis())) // <-- Data di emissione del token (IAT - Issued AT), va messa in millisecondi
				.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // <-- Data di scadenza del token (Expiration Date), anch'essa in millisecondi
				.subject(String.valueOf(user.getId())) // <-- Subject, ovvero a chi appartiene il token (ATTENZIONE A NON INSERIRE DATI SENSIBILI QUA DENTRO!!!!!!)
				.signWith(Keys.hmacShaKeyFor(secret.getBytes())) // <-- Con quest firmo il token, passandogli il SEGRETO
				.compact();
	}

	public void verifyToken(String token){ // Dato un token verificami se è valido
		try {
			Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes())).build().parse(token);
			// Il metodo .parse(token) mi lancerà varie eccezioni in caso di token scaduto o malformato o manipolato

		} catch (Exception ex){
			throw new UnauthorizedException("Problemi col token! Per favore effettua di nuovo il login!");
			// Non importa se l'eccezione lanciata da .parse() sia un'eccezione perché il token è scaduto o malformato o manipolato, a noi interessa solo che il risultato sia un 401
		}
	}

	public String extractIdFromToken(String token){
		return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes())).build()
				.parseSignedClaims(token).getPayload().getSubject(); // Il subject è l'id dell'utente
	}
}
