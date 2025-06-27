
package acme.features.flightCrewMember.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S3.ActivityLog;
import acme.entities.S3.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class ActivityLogPublishService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ActivityLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = false;
		String method = super.getRequest().getMethod();
		if (method.equals("GET"))
			status = false;
		else {
			int activityLogId;
			ActivityLog activityLog;

			activityLogId = super.getRequest().getData("id", int.class);
			activityLog = this.repository.findActivityLogById(activityLogId);
			if (activityLog != null) {
				int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
				boolean authorised = this.repository.thatActivityLogIsOf(activityLogId, flightCrewMemberId);
				boolean authorised1 = this.repository.existsFlightCrewMember(flightCrewMemberId) && authorised;
				status = authorised1 && activityLog != null && activityLog.isDraftMode();
			}
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		ActivityLog activityLog;
		int id;

		id = super.getRequest().getData("id", int.class);
		activityLog = this.repository.findActivityLogById(id);
		super.getBuffer().addData(activityLog);
	}

	@Override
	public void bind(final ActivityLog activityLog) {
		int activityLogId;
		FlightAssignment flightAssignment;
		activityLogId = super.getRequest().getData("id", int.class);
		flightAssignment = this.repository.findFlightAssignmentByActivityLogId(activityLogId);
		activityLog.setFlightAssignment(flightAssignment);
		activityLog.setRegistrationMoment(MomentHelper.getCurrentMoment());
		super.bindObject(activityLog, "typeOfIncident", "description", "severityLevel");
	}

	@Override
	public void validate(final ActivityLog activityLog) {
		int activityLogId = super.getRequest().getData("id", int.class);
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentByActivityLogId(activityLogId);
		boolean legHasArrive = MomentHelper.isAfter(MomentHelper.getCurrentMoment(), flightAssignment.getLeg().getScheduledArrival());
		super.state(legHasArrive, "*", "flight-crew-member.activity-log.validation.create");
	}

	@Override
	public void perform(final ActivityLog activityLog) {
		activityLog.setDraftMode(false);
		this.repository.save(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;
		dataset = super.unbindObject(activityLog, "draftMode", "typeOfIncident", "description", "severityLevel");
		super.getResponse().addData(dataset);
	}

}
