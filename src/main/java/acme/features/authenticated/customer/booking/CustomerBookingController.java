
package acme.features.authenticated.customer.booking;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.student2.Booking;
import acme.realms.Customer;

@GuiController
public class CustomerBookingController extends AbstractGuiController<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	protected CustomerBookingListService		listService;

	@Autowired
	protected CustomerBookingShowService		showService;

	@Autowired
	protected CustomerBookingCreateService		createService;

	@Autowired
	protected CustomerBookingUpdateService		updateService;

	@Autowired
	protected CustomerBookingPublishedService	publishService;

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
