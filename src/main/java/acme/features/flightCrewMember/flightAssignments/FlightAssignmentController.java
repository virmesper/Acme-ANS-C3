
package acme.features.flightCrewMember.flightAssignments;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.S3.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiController
public class FlightAssignmentController extends AbstractGuiController<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightAssignmentListService					listService;

	@Autowired
	private FlightAssignmentListServiceUncompletedLegs	listServiceUncompletedLeg;

	@Autowired
	private FlightAssignmentShowService					showService;

	@Autowired
	private FlightAssignmentCreateService				createService;

	@Autowired
	private FlightAssignmentUpdateService				updateService;

	@Autowired
	private FlightAssignmentDeleteService				deleteService;

	@Autowired
	private FlightAssignmentPublishService				publishService;


	@PostConstruct
	protected void initialise() {
		super.addCustomCommand("list-completed", "list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);

		super.addCustomCommand("list-uncompleted", "list", this.listServiceUncompletedLeg);
		super.addCustomCommand("publish", "update", this.publishService);
	}
}
