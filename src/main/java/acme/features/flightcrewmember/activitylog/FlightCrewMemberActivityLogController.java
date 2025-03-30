
package acme.features.flightcrewmember.activitylog;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.S3.ActivityLog;
import acme.realms.FlightCrewMember;

@GuiController
@RequestMapping("/flightcrewmember/activity-log/")
public class FlightCrewMemberActivityLogController extends AbstractGuiController<FlightCrewMember, ActivityLog> {

	@Autowired
	protected FlightCrewMemberActivityLogListService	listService;

	@Autowired
	protected FlightCrewMemberActivityLogShowService	showService;

	@Autowired
	protected FlightCrewMemberActivityLogCreateService	createService;

	@Autowired
	protected FlightCrewMemberActivityLogUpdateService	updateService;

	@Autowired
	protected FlightCrewMemberActivityLogDeleteService	deleteService;

	@Autowired
	protected FlightCrewMemberActivityLogPublishService	publishService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);
		super.addCustomCommand("publish", "update", this.publishService);
	}
}
