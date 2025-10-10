
package acme.features.administrator.recommendation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student2.Recommendation;

@GuiService
public class AdministratorRecommendationPopulateService extends AbstractGuiService<Administrator, Recommendation> {

	@Autowired
	private AdministratorRecommendationRepository	repository;

	private final RestTemplate						http	= new RestTemplate();


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Administrator.class));
	}

	@Override
	public void load() {
		super.getBuffer().addData(new Recommendation()); // para el formulario
	}

	@Override
	public void bind(final Recommendation dummy) {
		super.bindObject(dummy, "city", "country", "category"); // parámetros del form
	}

	@Override
	public void validate(final Recommendation dummy) {
		// opcional: exigir city o country
	}

	@Override
	public void perform(final Recommendation dummy) {
		String city = super.getRequest().getData("city", String.class);
		String country = super.getRequest().getData("country", String.class);
		String category = super.getRequest().getData("category", String.class);
		Integer limit = super.getRequest().getData("limit", Integer.class);
		if (limit == null || limit < 1)
			limit = 20;

		// === EJEMPLO con OpenTripMap (pseudo) ===
		final String apiKey = "TU_API_KEY";      // llévatelo a application.properties
		final Date now = new Date();

		// 1) Obtener bbox/coords para city o country (consulta “geoname”)
		// 2) Obtener lugares por categoría alrededor (consulta “places”)
		// 3) Mapear a Recommendation
		// (A falta del mapeo concreto, te dejo un ejemplo de creación manual)

		List<Recommendation> batch = new ArrayList<>();
		// Ejemplo simulado:
		for (int i = 0; i < limit; i++) {
			Recommendation r = new Recommendation();
			r.setSource("opentripmap");
			r.setExternalId((city != null ? city : country) + "-" + category + "-" + i);
			r.setTitle("Sample " + i + " in " + (city != null ? city : country));
			r.setCategory(category);
			r.setCity(city);
			r.setCountry(country);
			r.setUrl("https://opentripmap.com/...");
			r.setLastUpdate(now);

			if (!this.repository.existsBySourceAndExternalId(r.getSource(), r.getExternalId()))
				this.repository.save(r);
		}
	}

	@Override
	public void unbind(final Recommendation dummy) {
		Dataset ds = super.unbindObject(dummy, "city", "country", "category");
		ds.put("limit", 20);
		super.getResponse().addData(ds);
	}
}
