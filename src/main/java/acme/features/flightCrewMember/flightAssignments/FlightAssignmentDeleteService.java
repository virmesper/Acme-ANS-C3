
package acme.features.flightCrewMember.flightAssignments;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S3.ActivityLog;
import acme.entities.S3.CurrentStatus;
import acme.entities.S3.Duty;
import acme.entities.S3.FlightAssignment;
import acme.features.flightCrewMember.activityLog.ActivityLogRepository;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentDeleteService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightAssignmentRepository	repository;

	@Autowired
	private ActivityLogRepository		repositoryActivityLog;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		FlightAssignment flightAssignment = new FlightAssignment();

		flightAssignment.setMoment(MomentHelper.getCurrentMoment());
		flightAssignment.setDraftMode(true);

		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		FlightCrewMember flightCrewMember = this.repository.findMemberById(userAccountId);
		flightAssignment.setFlightCrewMember(flightCrewMember);

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {

		super.bindObject(flightAssignment, "duty", "currentStatus", "remarks", "leg");
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {
		boolean confirmation;

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		Collection<ActivityLog> activityLogs;

		activityLogs = this.repositoryActivityLog.findAllActivityLogs(flightAssignment.getId());
		this.repository.deleteAll(activityLogs);
		this.repository.delete(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {

		Dataset dataset = super.unbindObject(flightAssignment, "duty", "moment", "currentStatus", "draftMode", "remarks", "flightCrewMember", "leg");
		FlightCrewMember flightCrewMember = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();
		dataset.put("flightCrewMember", flightCrewMember.getIdentity().getFullName());

		SelectChoices dutyChoices = SelectChoices.from(Duty.class, flightAssignment.getDuty());
		dataset.put("dutyChoices", dutyChoices);
		dataset.put("duty", dutyChoices.getSelected().getKey());

		SelectChoices statusChoices = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());
		dataset.put("statusChoices", statusChoices);
		dataset.put("status", statusChoices.getSelected().getKey());

		SelectChoices legChoices = SelectChoices.from(this.repository.findAllLegsFromAirline(flightCrewMember.getAirline().getId()), "flightNumber", flightAssignment.getLeg());
		dataset.put("legChoices", legChoices);
		dataset.put("leg", legChoices.getSelected().getKey());

		super.getResponse().addData(dataset);
	}

}
