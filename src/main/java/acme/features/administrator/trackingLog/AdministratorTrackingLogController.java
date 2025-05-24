
package acme.features.administrator.trackingLog;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.entities.S4.TrackingLog;

@Controller
public class AdministratorTrackingLogController extends AbstractGuiController<Administrator, TrackingLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorTrackingLogListService	listService;

	@Autowired
	private AdministratorTrackingLogShowService	showService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
	}

}
