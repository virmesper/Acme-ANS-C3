
package acme.features.flightcrewmember.flightAssignments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S1.Leg;
import acme.entities.S3.AvailabilityStatus;
import acme.entities.S3.CurrentStatus;
import acme.entities.S3.Duty;
import acme.entities.S3.FlightAssignment;
import acme.realms.FlightCrewMember;

@GuiService
public class FlightAssignmentUpdateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightAssignmentRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int flightAssignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment flightAssignment = this.repository.findFa(flightAssignmentId);

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment fa) {

		int crewMemberId;
		int legId;
		crewMemberId = super.getRequest().getData("flightCrewMember", int.class);
		FlightCrewMember member = this.repository.findMemberById(crewMemberId);
		legId = super.getRequest().getData("leg", int.class);
		Leg legAssigned = this.repository.findLegById(legId);

		super.bindObject(fa, "moment", "duty", "currentStatus", "remarks");
		fa.setFlightCrewMember(member);
		fa.setLeg(legAssigned);

	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {
		boolean existSimultaneousLeg = false;
		boolean unproperPilotDuty = true;
		boolean unproperCopilotDuty = true;
		boolean alreadyAssignedToTheLeg = false;

		Leg legAnalized = flightAssignment.getLeg();
		Date legDeparture = flightAssignment.getLeg().getScheduledDeparture();
		Date legArrival = flightAssignment.getLeg().getScheduledArrival();
		List<Leg> simultaneousLegs = this.repository.findSimultaneousLegsByMember(legDeparture, legArrival, legAnalized.getId(), flightAssignment.getFlightCrewMember().getId());
		if (simultaneousLegs.isEmpty())
			existSimultaneousLeg = true;

		List<FlightAssignment> legCopilotAssignments = this.repository.findFlightAssignmentsByLegAndDuty(legAnalized, Duty.COPILOT);
		List<FlightAssignment> legPilotAssignments = this.repository.findFlightAssignmentsByLegAndDuty(legAnalized, Duty.PILOT);
		if (flightAssignment.getDuty() == Duty.COPILOT)
			if (legCopilotAssignments.size() + 1 >= 2)
				unproperCopilotDuty = false;
		if (flightAssignment.getDuty() == Duty.PILOT)
			if (legPilotAssignments.size() + 1 >= 2)
				unproperPilotDuty = false;

		List<Object[]> legFlightAssignment = this.repository.findLegsAndAssignmentsByMemberId(flightAssignment.getFlightCrewMember().getId());
		for (Object[] row : legFlightAssignment) {
			Leg leg = (Leg) row[0];
			FlightAssignment assignment = (FlightAssignment) row[1];
			if (leg.getId() == legAnalized.getId() && assignment.getId() == flightAssignment.getId() || leg.getId() != legAnalized.getId())
				alreadyAssignedToTheLeg = true;
		}

		super.state(alreadyAssignedToTheLeg, "flightCrewMember", "acme.validation.flight-assignment.memberAlreadyAssigned.message");
		super.state(existSimultaneousLeg, "leg", "acme.validation.flight-assignment.legCurrency.message");
		super.state(unproperCopilotDuty, "duty", "acme.validation.flight-assignment.dutyCopilot.message");
		super.state(unproperPilotDuty, "duty", "acme.validation.flight-assignment.dutyPilot.message");

	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		this.repository.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment fa) {
		SelectChoices statusChoices;
		SelectChoices dutyChoices;
		SelectChoices memberChoices;
		SelectChoices legChoices;
		Dataset dataset;
		List<FlightCrewMember> avaliableMembers;
		List<Leg> posibleLegs;

		statusChoices = SelectChoices.from(CurrentStatus.class, fa.getCurrentStatus());
		dutyChoices = SelectChoices.from(Duty.class, fa.getDuty());
		avaliableMembers = this.getAvailableMembers();
		posibleLegs = this.getPosibleLegs();
		legChoices = SelectChoices.from(posibleLegs, "flightNumber", fa.getLeg());
		memberChoices = SelectChoices.from(avaliableMembers, "userAccount.username", fa.getFlightCrewMember());
		dataset = super.unbindObject(fa, "moment", "duty", "currentStatus", "remarks", "draftMode");
		dataset.put("statuses", statusChoices);
		dataset.put("duties", dutyChoices);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("crewMembers", memberChoices.getSelected().getKey());
		dataset.put("members", memberChoices);

		super.getResponse().addData(dataset);
	}

	public List<FlightCrewMember> getAvailableMembers() {
		List<FlightCrewMember> avaliableMembers = this.repository.findMembersByStatus(AvailabilityStatus.AVAILABLE);
		if (avaliableMembers == null)
			avaliableMembers = new ArrayList<>();
		return avaliableMembers;
	}

	public List<Leg> getPosibleLegs() {
		Date currentDate = MomentHelper.getCurrentMoment();
		List<Leg> posibleLegs = this.repository.findUpcomingLegs(currentDate);
		if (posibleLegs == null)
			posibleLegs = new ArrayList<>();
		return posibleLegs;
	}

}
