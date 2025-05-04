
package acme.features.flightCrewMember.flightAssignments;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S1.LegRepository;
import acme.entities.S3.CurrentStatus;
import acme.entities.S3.Duty;
import acme.entities.S3.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentPublishService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightAssignmentRepository	repositoryAssignment;

	@Autowired
	private LegRepository				repositoryLeg;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int flightAssignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment flightAssignment = this.repositoryAssignment.findFlightAssignmentById(flightAssignmentId);

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {
		super.bindObject(flightAssignment, "duty", "currentStatus", "remarks", "leg");
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {
		super.state(flightAssignment.getFlightCrewMember() != null, "flightCrewMember", "acme.validation.flightAssignment.flightcrewmember");
		super.state(flightAssignment.getLeg() != null, "leg", "acme.validation.flightAssignment.leg");

		if (flightAssignment.getDuty() != null && flightAssignment.getLeg() != null) {
			boolean isDutyAssigned = this.repositoryAssignment.hasDutyAssigned(flightAssignment.getLeg().getId(), flightAssignment.getDuty(), flightAssignment.getId());
			super.state(!isDutyAssigned, "duty", "acme.validation.flightAssignment.duty");
		}

		if (flightAssignment.getLeg() != null) {
			boolean isPastLeg = flightAssignment.getLeg().getScheduledDeparture().before(MomentHelper.getCurrentMoment());
			super.state(!isPastLeg, "leg", "acme.validation.flightAssignment.leg.scheduledDeparture");
		}

	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		flightAssignment.setDraftMode(false);
		this.repositoryAssignment.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset = super.unbindObject(flightAssignment, "duty", "moment", "currentStatus", "remarks", "draftMode", "flightCrewMember", "leg");

		SelectChoices dutyChoices = SelectChoices.from(Duty.class, flightAssignment.getDuty());
		dataset.put("duties", dutyChoices);

		SelectChoices statusChoices = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());
		dataset.put("statuses", statusChoices);

		Collection<FlightCrewMember> flightCrewMembers = this.repositoryAssignment.findAllflightCrewMemberFromAirline(flightAssignment.getFlightCrewMember().getAirline().getId());
		SelectChoices flightCrewMemberChoices = SelectChoices.from(flightCrewMembers, "identity.fullName", flightAssignment.getFlightCrewMember());
		dataset.put("members", flightCrewMemberChoices);
		dataset.put("readOnlyCrewMember", true);

		SelectChoices legChoices = SelectChoices.from(this.repositoryLeg.findAllLegs(), "flightNumber", flightAssignment.getLeg());
		dataset.put("legs", legChoices);

		super.getResponse().addData(dataset);
	}

}
