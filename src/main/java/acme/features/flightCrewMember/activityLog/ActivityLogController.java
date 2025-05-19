
package acme.features.flightCrewMember.activityLog;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.S3.ActivityLog;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiController
public class ActivityLogController extends AbstractGuiController<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ActivityLogListService		listService;

	@Autowired
	private ActivityLogShowService		showService;

	@Autowired
	private ActivityLogCreateService	createService;

	@Autowired
	private ActivityLogDeleteService	deleteService;

	@Autowired
	private ActivityLogPublishService	publishService;

	@Autowired
	private ActivityLogUpdateService	updateService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("delete", this.deleteService);
		super.addBasicCommand("update", this.updateService);

		super.addCustomCommand("publish", "update", this.publishService);

	}

}
