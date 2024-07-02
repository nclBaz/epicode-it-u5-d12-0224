package riccardogulin.u5d12.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
}
