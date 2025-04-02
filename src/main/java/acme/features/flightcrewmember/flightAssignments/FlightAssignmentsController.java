
package acme.features.flightcrewmember.flightAssignments;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.S3.FlightAssignment;
import acme.realms.FlightCrewMember;

@GuiController
public class FlightAssignmentsController extends AbstractGuiController<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightAssignmentListService					listService;

	@Autowired
	private FlightAssignmentListServiceUncompletedLegs	listServiceUL;

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
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);

		super.addCustomCommand("listUL", "list", this.listServiceUL);
		super.addCustomCommand("publish", "update", this.publishService);
	}
}
