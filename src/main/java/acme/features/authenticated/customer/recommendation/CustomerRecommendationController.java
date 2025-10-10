// CUSTOMER

package acme.features.authenticated.customer.recommendation;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.student2.Recommendation;
import acme.realms.Customer;

@GuiController
public class CustomerRecommendationController extends AbstractGuiController<Customer, Recommendation> {

	@Autowired
	private CustomerRecommendationListRelatedService listRelatedService;


	@PostConstruct
	protected void initialise() {
		super.addCustomCommand("list-related", "list", this.listRelatedService);
	}
}
