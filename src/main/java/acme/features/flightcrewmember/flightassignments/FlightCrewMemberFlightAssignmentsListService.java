
package acme.features.flightcrewmember.flightassignments;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S3.DutyRole;
import acme.entities.S3.FlightAssignment;
import acme.realms.FlightCrewMember;

@GuiService
public class FlightCrewMemberFlightAssignmentsListService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	//Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberFlightAssignmentsRepository repository;

	//AbstractGuiService interface ---------------------------------------------------------


	@Override
	public void authorise() {
		boolean isLeadAttendant;
		FlightAssignment flightAssignment;

		flightAssignment = this.repository.findFlightAssignmentById(super.getRequest().getPrincipal().getAccountId());
		isLeadAttendant = flightAssignment != null && flightAssignment.getFlightCrewDuty() == DutyRole.LEAD_ATTENDANT;

		super.getResponse().setAuthorised(isLeadAttendant);
	}

	@Override
	public void load() {
		Collection<FlightAssignment> flightAssignment;
		int id;

		id = super.getRequest().getPrincipal().getAccountId();
		flightAssignment = this.repository.findFlightAssignmentsByCrewMemberId(id);

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;

		dataset = super.unbindObject(flightAssignment, "flightCrew", "moment", "currentStatus", "remarks", "crewMember", "leg");
		super.addPayload(dataset, flightAssignment, "text", "moreInfo");

		super.getResponse().addData(dataset);
	}
}
