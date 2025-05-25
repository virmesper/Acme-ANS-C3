
package acme.features.flightCrewMember.flightAssignments;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S1.Leg;
import acme.entities.S1.LegStatus;
import acme.entities.S3.CurrentStatus;
import acme.entities.S3.Duty;
import acme.entities.S3.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentShowService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean authorised = false;
		boolean isHis = false;
		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		int flightAssignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(flightAssignmentId);
		if (flightAssignment != null) {
			boolean authorised2 = this.repository.existsFlightAssignment(flightAssignmentId);
			boolean authorised1 = this.repository.existsFlightCrewMember(flightCrewMemberId);
			authorised = authorised2 && authorised1 && this.repository.thatFlightAssignmentIsOf(flightAssignmentId, flightCrewMemberId);
			isHis = flightAssignment.getFlightCrewMember().getId() == flightCrewMemberId;
		}
		super.getResponse().setAuthorised(authorised && isHis);
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
	public void unbind(final FlightAssignment flightAssignment) {
		Collection<Leg> legs;
		SelectChoices legChoices;

		Dataset dataset;

		SelectChoices currentStatus;
		int flightAssignmentId;

		flightAssignmentId = super.getRequest().getData("id", int.class);
		SelectChoices duty;
		boolean isCompleted;
		legs = this.repository.findAllLegs();

		currentStatus = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());
		duty = SelectChoices.from(Duty.class, flightAssignment.getDuty());

		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember flightCrewMember = this.repository.findFlightCrewMemberById(flightCrewMemberId);

		legChoices = SelectChoices.from(legs, "flightNumber", flightAssignment.getLeg());
		isCompleted = this.repository.areLegsCompletedByFlightAssignment(flightAssignmentId, LegStatus.LANDED);
		dataset = super.unbindObject(flightAssignment, "duty", "moment", "currentStatus", "remarks", "draftMode");
		dataset.put("currentStatus", currentStatus);
		dataset.put("duty", duty);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("flightCrewMember", flightCrewMember.getEmployeeCode());
		dataset.put("isCompleted", isCompleted);
		super.getResponse().addData(dataset);
	}

}
