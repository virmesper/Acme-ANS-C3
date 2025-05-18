
package acme.features.flightCrewMember.flightAssignments;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S1.Leg;
import acme.entities.S3.CurrentStatus;
import acme.entities.S3.Duty;
import acme.entities.S3.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentPublishService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean status;
		String method = super.getRequest().getMethod();
		if (method.equals("GET"))
			status = false;
		else {
			int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
			int flightAssignmentId = super.getRequest().getData("id", int.class);
			boolean authorised = this.repository.thatFlightAssignmentIsOf(flightAssignmentId, flightCrewMemberId);
			FlightAssignment flightAssignment;
			boolean authorised1 = this.repository.existsFlightCrewMember(flightCrewMemberId);
			flightAssignment = this.repository.findFlightAssignmentById(flightAssignmentId);
			int legId = super.getRequest().getData("leg", int.class);
			boolean authorised3 = true;
			if (legId != 0)
				authorised3 = this.repository.existsLeg(legId);

			status = authorised3 && authorised1 && authorised && flightAssignment.isDraftMode() && MomentHelper.isFuture(flightAssignment.getLeg().getScheduledArrival());
			boolean isHis = flightAssignment.getFlightCrewMember().getId() == flightCrewMemberId;

			status = status && isHis;
		}
		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		FlightAssignment flightAssignment;
		flightAssignment = new FlightAssignment();

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {
		Integer legId;
		Leg leg;
		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember flightCrewMember = this.repository.findFlightCrewMemberById(flightCrewMemberId);

		int id = super.getRequest().getData("id", int.class);
		flightAssignment.setId(id);
		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findLegById(legId);

		super.bindObject(flightAssignment, "duty", "currentStatus", "remarks");
		FlightAssignment original = this.repository.findFlightAssignmentById(flightAssignment.getId());

		flightAssignment.setLeg(leg);
		flightAssignment.setFlightCrewMember(flightCrewMember);
		flightAssignment.setMoment(original.getMoment());
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {
		FlightAssignment original = this.repository.findFlightAssignmentById(flightAssignment.getId());
		Leg leg = flightAssignment.getLeg();
		boolean cambioDuty = !original.getDuty().equals(flightAssignment.getDuty());

		boolean cambioLeg = !original.getLeg().equals(flightAssignment.getLeg());

		if (leg != null && cambioLeg && !this.isLegCompatible(flightAssignment))
			super.state(false, "flightCrewMember", "acme.validation.FlightAssignment.FlightCrewMemberIncompatibleLegs.message");

		if (leg != null && (cambioDuty || cambioLeg))
			this.checkPilotAndCopilotAssignment(flightAssignment);

		boolean legCompleted = this.isLegCompleted(leg);
		if (legCompleted)
			super.state(false, "leg", "acme.validation.FlightAssignment.LegAlreadyCompleted.message");
	}

	private boolean isLegCompleted(final Leg leg) {
		return leg != null && leg.getScheduledArrival() != null && leg.getScheduledArrival().before(MomentHelper.getCurrentMoment());
	}

	private boolean isLegCompatible(final FlightAssignment flightAssignment) {
		Collection<Leg> legsByMember = this.repository.findLegsByFlightCrewMember(flightAssignment.getFlightCrewMember().getId());
		Leg newLeg = flightAssignment.getLeg();

		return legsByMember.stream().allMatch(existingLeg -> this.areLegsCompatible(newLeg, existingLeg));
	}

	private boolean areLegsCompatible(final Leg newLeg, final Leg oldLeg) {
		return !(MomentHelper.isInRange(newLeg.getScheduledDeparture(), oldLeg.getScheduledDeparture(), oldLeg.getScheduledArrival()) || MomentHelper.isInRange(newLeg.getScheduledArrival(), oldLeg.getScheduledDeparture(), oldLeg.getScheduledArrival()));
	}

	private void checkPilotAndCopilotAssignment(final FlightAssignment flightAssignment) {
		boolean havePilot = this.repository.existsFlightCrewMemberWithDutyInLeg(flightAssignment.getLeg().getId(), Duty.PILOT);
		boolean haveCopilot = this.repository.existsFlightCrewMemberWithDutyInLeg(flightAssignment.getLeg().getId(), Duty.COPILOT);

		if (Duty.PILOT.equals(flightAssignment.getDuty()))
			super.state(!havePilot, "duty", "acme.validation.FlightAssignment.havePilot.message");
		if (Duty.COPILOT.equals(flightAssignment.getDuty()))
			super.state(!haveCopilot, "duty", "acme.validation.FlightAssignment.haveCopilot.message");
	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		flightAssignment.setMoment(MomentHelper.getCurrentMoment());
		flightAssignment.setDraftMode(false);

		this.repository.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {

		SelectChoices currentStatus;
		SelectChoices duty;

		Collection<Leg> legs;
		SelectChoices legChoices;
		boolean isCompleted;
		int flightAssignmentId;

		flightAssignmentId = super.getRequest().getData("id", int.class);

		Date currentMoment;
		currentMoment = MomentHelper.getCurrentMoment();
		isCompleted = this.repository.areLegsCompletedByFlightAssignment(flightAssignmentId, currentMoment);
		Dataset dataset;
		FlightAssignment fa = this.repository.findFlightAssignmentById(flightAssignmentId);
		legs = this.repository.findAllLegs();

		legChoices = SelectChoices.from(legs, "flightNumber", flightAssignment.getLeg());

		currentStatus = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());
		duty = SelectChoices.from(Duty.class, flightAssignment.getDuty());

		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember flightCrewMember = this.repository.findFlightCrewMemberById(flightCrewMemberId);

		dataset = super.unbindObject(flightAssignment, "duty", "moment", "CurrentStatus", "remarks", "draftMode");
		dataset.put("readonly", false);
		dataset.put("moment", MomentHelper.getCurrentMoment());
		dataset.put("currentStatus", currentStatus);
		dataset.put("duty", duty);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("flightCrewMember", flightCrewMember.getEmployeeCode());
		dataset.put("isCompleted", isCompleted);
		dataset.put("draftMode", fa.isDraftMode());

		super.getResponse().addData(dataset);
	}

}
