package riccardogulin.u5d12.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Se voglio poter dichiarare le regole di autorizzazione direttamente sui singoli endpoint, allora è OBBLIGATORIA l'annotazione @EnableMethodSecurity
public class Config {
	// Per poter configurare a piacimento la SecurityFilterChain devo creare un Bean di tipo SecurityFilterChain

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		// Possiamo ad es disabililtare dei comportamenti di default
		httpSecurity.formLogin(http -> http.disable()); // Non voglio il form di login (avremo React per quello)
		httpSecurity.csrf(http -> http.disable()); // Non voglio la protezione da CSRF (per le nostre app non è necessaria, anzi complicherebbe abbastanza tutta la faccenda, compreso il FE)
		httpSecurity.sessionManagement(http -> http.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // Non voglio le sessioni (perché con JWT non si utilizzano le sessioni)
		// Possiamo aggiungere anche dei filtri custom ad es
		// Possiamo ad es aggiungere/rimuovere determinate regole di protezione per gli endpoint
		// Possiamo decidere se debba essere necessaria un'autenticazione per accedere ai nostri endpoint
		httpSecurity.authorizeHttpRequests(http -> http.requestMatchers("/**").permitAll()); // <- questo evita di avere 401 per ogni richiesta
		return httpSecurity.build();
	}

	@Bean
	PasswordEncoder getBCrypt(){
		return new BCryptPasswordEncoder(11);
		// 11 è il numero di ROUNDS, ovvero quante volte viene eseguito l'algoritmo BCrypt, ciò ci è utile per determinare quale sarà la velocità di
		// esecuzione di BCrypt. Più è veloce meno sicure saranno le password e ovviamente viceversa. Bisogna comunque sempre tenere in considerazione
		// però anche la UX, quindi se lo rendessimo estremamente lento, la UX peggiorerebbe tantissimo, bisogna quindi trovare il giusto bilanciamento
		// tra sicurezza e UX.
		// 11 ad es significa che l'algoritmo verrà eseguito 2^11 volte, cioè 2048. Su un computer di prestazioni medie ciò significa all'incirca 100/200ms
	}
}
