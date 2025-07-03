
package acme.features.administrator.tracking_log;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.student4.TrackingLog;

@GuiController
public class AdministratorTrackingLogController extends AbstractGuiController<Administrator, TrackingLog> {

	// Internal state ---------------------------------------------------------

	private final AdministratorTrackingLogListService	listService;
	private final AdministratorTrackingLogShowService	showService;


	@Autowired
	public AdministratorTrackingLogController(final AdministratorTrackingLogListService listService, final AdministratorTrackingLogShowService showService) {
		this.listService = listService;
		this.showService = showService;
	}

	// Constructors -----------------------------------------------------------

	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
	}

}
