
package acme.features.flightcrewmember.flightassignments;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.entities.S1.Leg;
import acme.entities.S3.AssignmentStatus;
import acme.entities.S3.DutyRole;
import acme.entities.S3.FlightAssignment;
import acme.realms.FlightCrewMember;

@Service
public class FlightCrewMemberFlightAssignmentsCreateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberFlightAssignmentsRepository repository;

	// AbstractService<AirlineManager, Flight> -------------------------------------


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
		FlightAssignment flightAssignment;
		FlightCrewMember flightCrewMember;
		int legId;
		Leg leg;
		Date moment;

		flightCrewMember = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();
		legId = super.getRequest().getData("legId", int.class);
		leg = this.repository.findLegById(legId);
		moment = MomentHelper.getCurrentMoment();

		flightAssignment = new FlightAssignment();
		flightAssignment.setFlightCrewDuty(DutyRole.PILOT);
		flightAssignment.setLastUpdate(moment);
		flightAssignment.setAssignmentStatus(AssignmentStatus.CONFIRMED);
		flightAssignment.setRemarks("");
		flightAssignment.setFlightCrewMember(flightCrewMember);
		flightAssignment.setLeg(leg);

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {
		super.bindObject(flightAssignment, "flightCrew", "currentStatus", "remarks");
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

		// 1. Validar estado AVAILABLE
		FlightCrewMember crewMember = flightAssignment.getFlightCrewMember();
		super.state(crewMember.getAvailabilityStatus().name().equals("AVAILABLE"), "flightCrew", "acme.validation.flightassignment.crew-unavailable");

		// 2. Validar que no esté asignado a otro leg
		Collection<FlightAssignment> existingAssignments = this.repository.findFlightAssignmentsByCrewMemberId(crewMember.getId());
		super.state(existingAssignments.isEmpty(), "flightCrew", "acme.validation.flightassignment.multiple-legs");
	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		Date moment;
		moment = MomentHelper.getCurrentMoment();
		flightAssignment.setLastUpdate(moment);
		this.repository.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		SelectChoices choices1;
		SelectChoices choices2;
		Dataset dataset;

		choices1 = SelectChoices.from(DutyRole.class, flightAssignment.getFlightCrewDuty());
		choices2 = SelectChoices.from(AssignmentStatus.class, flightAssignment.getAssignmentStatus());

		dataset = super.unbindObject(flightAssignment, "flightCrew", "currentStatus", "remarks");
		dataset.put("confirmation", false);
		dataset.put("readonly", false);
		dataset.put("flightCrews", choices1);
		dataset.put("currentStatuses", choices2);

		super.getResponse().addData(dataset);
	}
}
