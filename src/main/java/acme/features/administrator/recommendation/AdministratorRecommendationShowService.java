
package acme.features.administrator.recommendation;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student2.Recommendation;
import acme.features.authenticated.customer.recommendation.CustomerRecommendationRepository;

@GuiService
public class AdministratorRecommendationShowService extends AbstractGuiService<Administrator, Recommendation> {

	// Internal state ---------------------------------------------------------

	private final CustomerRecommendationRepository repository;

	// Constructor ------------------------------------------------------------


	public AdministratorRecommendationShowService(final CustomerRecommendationRepository repository) {
		this.repository = repository;
	}

	// AbstractGuiService interface -------------------------------------------

	@Override
	public void authorise() {
		Recommendation rec;
		int recId;

		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);

		if (status && super.getRequest().hasData("id")) {
			recId = super.getRequest().getData("id", int.class);
			rec = this.repository.findRecommendationById(recId);
			status = rec != null;
		} else
			status = false;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Recommendation recommendation;
		int recommendationId;

		recommendationId = super.getRequest().getData("id", int.class);
		recommendation = this.repository.findRecommendationById(recommendationId);

		super.getBuffer().addData(recommendation);
	}

	@Override
	public void unbind(final Recommendation recommendation) {
		Dataset dataset;

		dataset = super.unbindObject(recommendation, "name", "city", "businessStatus", "formattedAddress", "rating", "userRatingsTotal", "openNow", "photoReference");

		super.getResponse().addData(dataset);
	}

}
