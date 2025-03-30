
package acme.features.flightcrewmember.flightassignments;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.entities.S3.AssignmentStatus;
import acme.entities.S3.DutyRole;
import acme.entities.S3.FlightAssignment;
import acme.realms.FlightCrewMember;

@Service
public class FlightCrewMemberFlightAssignmentsPublishService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

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
		int id;

		id = super.getRequest().getData("id", int.class);
		flightAssignment = this.repository.findFlightAssignmentById(id);

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

		// 1. Validar que el leg no haya ocurrido
		boolean legFuture = !flightAssignment.getLeg().getScheduledArrival().before(MomentHelper.getCurrentMoment());
		super.state(legFuture, "leg", "acme.validation.flightassignment.leg-already-finished");

		// 2. Validar máximo 1 piloto y 1 copiloto
		int legId = flightAssignment.getLeg().getId();
		DutyRole role = flightAssignment.getFlightCrewDuty();

		if (role == DutyRole.PILOT) {
			int count = this.repository.countPilotsByFlightAssignmentId(flightAssignment.getId());
			super.state(count == 0, "flightCrewDuty", "acme.validation.flightassignment.pilot-already-assigned");
		}

		if (role == DutyRole.CO_PILOT) {
			int count = this.repository.countCopilotsByFlightAssignmentId(flightAssignment.getId());
			super.state(count == 0, "flightCrewDuty", "acme.validation.flightassignment.copilot-already-assigned");
		}
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
