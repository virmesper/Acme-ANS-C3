
package acme.features.authenticated.customer;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.client.components.principals.Authenticated;
import acme.client.controllers.AbstractGuiController;
import acme.entities.S2.Passenger;

@Controller
public class AuthenticatedPassengerController extends AbstractGuiController<Authenticated, Passenger> {

	@Autowired
	private AuthenticatedPassengerListService	listService;

	@Autowired
	private AuthenticatedPassengerShowService	showService;

	@Autowired
	private AuthenticatedPassengerCreateService	createService;

	@Autowired
	private AuthenticatedPassengerUpdateService	updateService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
	}
}
