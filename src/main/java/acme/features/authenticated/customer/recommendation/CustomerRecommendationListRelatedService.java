
package acme.features.authenticated.customer.recommendation;

import java.util.ArrayList; // Necesitas ArrayList para crear una lista mutable
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student2.Recommendation;
import acme.realms.Customer;

@GuiService
public class CustomerRecommendationListRelatedService extends AbstractGuiService<Customer, Recommendation> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerRecommendationRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Customer.class));
	}

	@Override
	public void load() {
		Collection<Recommendation> recommendations;

		Integer customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		List<String> cities = this.repository.findBookingsByCustomerId(customerId).stream().map(b -> b.getFlightId().getDestinationCity()).distinct().toList(); // Usa .toList() si estás en Java 16+, sino .collect(Collectors.toList())

		recommendations = this.repository.findRecommendationsByCities(cities);

		if (recommendations == null)
			recommendations = new ArrayList<>();

		List<Dataset> datasetsForView = new ArrayList<>();
		for (Recommendation r : recommendations) {
			Dataset dataset = super.unbindObject(r, "city", "name", "rating", "photoReference");
			dataset.put("openNow", r.getOpenNow() ? "✓" : "x");
			datasetsForView.add(dataset);
		}

		super.getBuffer().addData("recommendationsList", datasetsForView); // O solo datasetsForView si 'addData' detecta el tipo

	}

	@Override
	public void unbind(final Recommendation recommendation) {

		Dataset dataset;
		dataset = super.unbindObject(recommendation, "city", "name", "rating", "photoReference");
		dataset.put("openNow", recommendation.getOpenNow() ? "✓" : "x");
		super.getResponse().addData(dataset); // Esto añade un Dataset al Response, quizás para un solo objeto.
	}
}
