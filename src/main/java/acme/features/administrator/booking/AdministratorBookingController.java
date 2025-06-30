
package acme.features.administrator.booking;

import javax.annotation.PostConstruct;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.student2.Booking;

@GuiController
public class AdministratorBookingController extends AbstractGuiController<Administrator, Booking> {

	// Internal state ---------------------------------------------------------

	private final AdministratorBookingListService	listService;
	private final AdministratorBookingShowService	showService;

	// Constructor ------------------------------------------------------------


	public AdministratorBookingController(final AdministratorBookingListService listService, final AdministratorBookingShowService showService) {
		this.listService = listService;
		this.showService = showService;
	}

	// Initialisation ---------------------------------------------------------

	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
	}

}
