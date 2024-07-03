package riccardogulin.u5d12.tools;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import riccardogulin.u5d12.entities.User;

@Component
public class MailgunSender {

	private String apiKey;
	private String domainName;

	public MailgunSender(@Value("${mailgun.apikey}")String apiKey, @Value("${mailgun.domainname}")String domainName) {
		this.apiKey = apiKey;
		this.domainName = domainName;
	}

	public void sendRegistrationEmail(User recipient){
		HttpResponse<JsonNode> response = Unirest.post("https://api.mailgun.net/v3/"+ this.domainName + "/messages")
				.basicAuth("api", this.apiKey)
				.queryString("from", "riccardo.gulin@gmail.com")
				.queryString("to", recipient.getEmail())
				.queryString("subject", "Registrazione completata con successo!")
				.queryString("text", "Complimenti " + recipient.getName() + " per esserti registrato con successo")
				.asJson();

		System.out.println(response.getBody());
	}
}
