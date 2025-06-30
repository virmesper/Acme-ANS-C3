
package acme.features.administrator.passenger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.student2.Passenger;

@GuiController
public class AdministratorPassengerController extends AbstractGuiController<Administrator, Passenger> {

	static final String												MASTER_ID	= "bookingId";

	// Internal state ---------------------------------------------------------

	private final AdministratorPassengerListService					listService;
	private final AdministratorBannedPassengersListService			listBannedPassengerService;
	private final AdministratorBannedPassengersLastMonthListService	listBannedPassengerLastMonthService;
	private final AdministratorLiftedBanPassengersListService		listLiftedBanPassengerService;
	private final AdministratorPassengerShowService					showService;

	// Constructors -----------------------------------------------------------


	@Autowired
	public AdministratorPassengerController(final AdministratorPassengerListService listService, final AdministratorBannedPassengersListService listBannedPassengerService,
		final AdministratorBannedPassengersLastMonthListService listBannedPassengerLastMonthService, final AdministratorLiftedBanPassengersListService listLiftedBanPassengerService, final AdministratorPassengerShowService showService) {
		this.listService = listService;
		this.listBannedPassengerService = listBannedPassengerService;
		this.listBannedPassengerLastMonthService = listBannedPassengerLastMonthService;
		this.listLiftedBanPassengerService = listLiftedBanPassengerService;
		this.showService = showService;
	}

	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addCustomCommand("list-banned-passengers", "list", this.listBannedPassengerService);
		super.addCustomCommand("list-banned-passengers-last-month", "list", this.listBannedPassengerLastMonthService);
		super.addCustomCommand("list-lifted-ban-passengers", "list", this.listLiftedBanPassengerService);
	}

}
