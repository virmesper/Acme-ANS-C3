
package acme.features.authenticated.customer;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.client.components.principals.Authenticated;
import acme.client.controllers.AbstractGuiController;
import acme.entities.S2.Booking;

@Controller
public class AuthenticatedBookingController extends AbstractGuiController<Authenticated, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	protected AuthenticatedBookingListService	listService;

	@Autowired
	protected AutehnticatedBookingShowService	showService;

	@Autowired
	protected AuthenticatedBookingCreateService	createService;

	@Autowired
	protected AuthenticatedBookingUpdateService	updateService;

	@Autowired
	protected AuthenticatedBookingPublishedService	publishService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addCustomCommand("publish", "update", this.publishService);
	}
}
