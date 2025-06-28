
package acme.features.administrator.visas;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import acme.client.components.principals.Administrator;
import acme.client.controllers.GuiController;
import acme.client.helpers.Assert;
import acme.client.helpers.PrincipalHelper;
import acme.entities.S3.VisaRequirement;

@GuiController
public class AdministratorPopulateVisaController {

	@Autowired
	protected AdministratorVisaRequirementRepository	repository;

	// Config de la API
	private static final String							API_URL		= "https://visa-requirement.p.rapidapi.com/";
	private static final String							API_HOST	= "visa-requirement.p.rapidapi.com";

	// Inyecto la KEY desde application.properties
	@Value("${myapp.rapidapi.key}")
	private String										apiKey;

	// GET method -> /administrator/system/populate-visa


	@GetMapping("/administrator/system/populate-visa")
	public ModelAndView populateVisa() {
		// Verifico que sea administrador
		Assert.state(PrincipalHelper.get().hasRealmOfType(Administrator.class), "acme.default.error.not-authorised");

		ModelAndView result;

		try {
			// Llamada a la lógica de poblamiento
			int insertedCount = this.doPopulateVisaRequirements();

			// Si todo sale bien, volvemos a fragments/welcome
			result = new ModelAndView("fragments/welcome");
			result.addObject("_globalSuccessMessage", String.format("Populate done: %d VisaRequirements inserted", insertedCount));

		} catch (final Throwable oops) {
			// Si algo falla, panic
			result = new ModelAndView("master/panic");
			result.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			result.addObject("_oops", oops);
			result.addObject("_globalErrorMessage", "acme.default.global.message.error");
		}

		return result;
	}

	// doPopulateVisaRequirements() 
	// Hace ~25 llamadas a la API y retorna cuántos inserts hizo -> Limitado por plan gratuito

	protected int doPopulateVisaRequirements() throws Exception {
		// Lista limitada a ~25 combos para no agotar la cuota
		List<String[]> combos = List.of(new String[] {
			"US", "BH"
		}, new String[] {
			"US", "DE"
		});

		/*
		 * List<String[]> combos = List.of(
		 * // 25 combos de ejemplo (passport, destination)
		 * new String[]{"US", "BH"},
		 * new String[]{"US", "DE"},
		 * new String[]{"ES", "FR"},
		 * new String[]{"ES", "DE"},
		 * new String[]{"UK", "PL"},
		 * new String[]{"US", "IT"},
		 * new String[]{"US", "BE"},
		 * new String[]{"FR", "CN"},
		 * new String[]{"CN", "JP"},
		 * new String[]{"CZ", "SK"},
		 * new String[]{"PT", "BR"},
		 * new String[]{"ES", "AR"},
		 * new String[]{"ES", "MX"},
		 * new String[]{"US", "CA"},
		 * new String[]{"US", "ES"},
		 * new String[]{"GB", "IE"},
		 * new String[]{"GB", "PT"},
		 * new String[]{"GB", "AE"},
		 * new String[]{"GB", "SA"},
		 * new String[]{"GB", "NZ"},
		 * new String[]{"GB", "AU"},
		 * new String[]{"ES", "IT"},
		 * new String[]{"US", "BR"},
		 * new String[]{"US", "CH"},
		 * new String[]{"US", "EG"}
		 * );
		 */

		int insertedCount = 0;

		for (String[] combo : combos) {
			String passport = combo[0];
			String destination = combo[1];

			// 1) Llamar a la API -> si hay error lanza excepción
			VisaRequirement result = this.callVisaApi(passport, destination);

			// 2) Comprobamos si existe en BD
			VisaRequirement existing = this.repository.findOneByPassportAndDestination(result.getPassportCountry(), result.getDestinationCountry());
			if (existing == null) {
				this.repository.save(result);
				insertedCount++;
			}
		}

		return insertedCount;
	}

	// callVisaApi -> Envía la request y parsea la respuesta, si algo falla lanza excepción

	protected VisaRequirement callVisaApi(final String passport, final String destination) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();

		String formData = "passport=" + URLEncoder.encode(passport, StandardCharsets.UTF_8) + "&destination=" + URLEncoder.encode(destination, StandardCharsets.UTF_8);

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(AdministratorPopulateVisaController.API_URL)).header("Content-Type", "application/x-www-form-urlencoded").header("x-rapidapi-host", AdministratorPopulateVisaController.API_HOST)
			.header("x-rapidapi-key", this.apiKey).POST(BodyPublishers.ofString(formData)).build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		if (response.statusCode() == 200)
			// Parsear
			return this.parseApiResponse(response.body(), passport, destination);
		else
			// Si no es 200, lanzamos excepción
			throw new RuntimeException("API responded with status: " + response.statusCode());
	}

	// parseApiResponse -> con Jackson, si algo falla, se lanza excepción.

	protected VisaRequirement parseApiResponse(final String body, final String passport, final String destination) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(body);

		VisaRequirement vr = new VisaRequirement();
		vr.setPassportCountry(root.path("passport_of").asText(""));
		vr.setDestinationCountry(root.path("destination").asText(""));
		vr.setContinent(root.path("continent").asText(""));
		vr.setCapital(root.path("capital").asText(""));
		vr.setCurrency(root.path("currency").asText(""));
		vr.setPhoneCode(root.path("phone_code").asText(""));
		vr.setTimezone(root.path("timezone").asText(""));
		vr.setVisaType(root.path("visa").asText(""));
		vr.setStayDuration(root.path("stay_of").asText(""));
		vr.setPassportValidity(root.path("pass_valid").asText(""));
		vr.setAdditionalInfo(root.path("except_text").asText(""));
		vr.setOfficialLink(root.path("link").asText(""));

		return vr;
	}
}
