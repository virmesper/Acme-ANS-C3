
package acme.features.flightcrewmember.flightassignments;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.S3.FlightAssignment;
import acme.realms.FlightCrewMember;

@GuiController
@RequestMapping("/flightcrewmember/flight-assignment/")
public class FlightCrewMemberFlightAssignmentsController extends AbstractGuiController<FlightCrewMember, FlightAssignment> {

	@Autowired
	protected FlightCrewMemberFlightAssignmentsListService		listService;

	@Autowired
	protected FlightCrewMemberFlightAssignmentsPublishService	publishService;

	@Autowired
	protected FlightCrewMemberFlightAssignmentsCreateService	createService;

	@Autowired
	protected FlightCrewMemberFlightAssignmentsUpdateService	updateService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addCustomCommand("publish", "update", this.publishService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
	}

}
