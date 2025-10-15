
package acme.features.authenticated.customer.recommendation;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.student2.Recommendation;
import acme.realms.Customer;

@GuiController
public class CustomerRecommendationController extends AbstractGuiController<Customer, Recommendation> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerRecommendationListService			listService;

	@Autowired
	private CustomerRecommendationListRelatedService	listRelatedService;

	@Autowired
	private CustomerRecommendationShowService			showService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addCustomCommand("list-related", "list", this.listRelatedService);
		super.addBasicCommand("show", this.showService);
	}
}
