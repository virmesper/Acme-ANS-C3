
package acme.features.administrator.recommendation;

import java.util.Collection;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student2.Recommendation;

@GuiService
public class AdministratorRecommendationListService extends AbstractGuiService<Administrator, Recommendation> {

	// Internal state ---------------------------------------------------------

	private final AdministratorRecommendationRepository repository;

	// Constructor ------------------------------------------------------------


	public AdministratorRecommendationListService(final AdministratorRecommendationRepository repository) {
		this.repository = repository;
	}

	// AbstractGuiService interface -------------------------------------------

	@Override
	public void authorise() {
		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Administrator.class));
	}

	@Override
	public void load() {
		Collection<Recommendation> recommendation;

		recommendation = this.repository.findAllRecommendation();

		super.getBuffer().addData(recommendation);
	}

	@Override
	public void unbind(final Recommendation recommendation) {
		Dataset dataset;

		dataset = super.unbindObject(recommendation, "city", "name", "rating", "photoReference");
		dataset.put("openNow", recommendation.getOpenNow() ? "✓" : "x");

		super.getResponse().addData(dataset);
	}

}
