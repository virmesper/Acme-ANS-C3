
package acme.features.flight_crew_member.flight_assignments;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student1.LegStatus;
import acme.entities.student3.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentListServiceUncompletedLegs extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightAssignmentRepository repository;


	@Override
	public void authorise() {

		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		boolean authorised = this.repository.existsFlightCrewMember(flightCrewMemberId);
		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		Collection<FlightAssignment> flightAssignments;
		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		flightAssignments = this.repository.findAllFlightAssignmentByPlannedLeg(LegStatus.LANDED, flightCrewMemberId);

		super.getBuffer().addData(flightAssignments);
	}
	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset = super.unbindObject(flightAssignment, "duty", "moment", "currentStatus", "remarks", "draftMode");

		super.getResponse().addData(dataset);
	}
}
