
package acme.features.flightCrewMember.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S3.ActivityLog;
import acme.entities.S3.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class ActivityLogShowService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ActivityLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int activityLogId;

		ActivityLog activityLog;
		boolean authorised = false;
		boolean isHis = false;
		activityLogId = super.getRequest().getData("id", int.class);
		activityLog = this.repository.findActivityLogById(activityLogId);
		boolean authorised3 = this.repository.existsActivityLog(activityLogId);
		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentByActivityLogId(activityLogId);
		if (flightAssignment != null) {
			boolean authorised2 = this.repository.existsFlightAssignment(flightAssignment.getId());

			boolean authorised1 = authorised3 && authorised2 && this.repository.existsFlightCrewMember(flightCrewMemberId);
			authorised = authorised1 && this.repository.thatActivityLogIsOf(activityLogId, flightCrewMemberId);
			isHis = flightAssignment.getFlightCrewMember().getId() == flightCrewMemberId;
		}

		super.getResponse().setAuthorised(authorised && activityLog != null && isHis);
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
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentByActivityLogId(activityLog.getId());

		dataset = super.unbindObject(activityLog, "registrationMoment", "typeOfIncident", "description", "severityLevel", "draftMode");
		dataset.put("id", activityLog.getId());
		dataset.put("masterId", flightAssignment.getId());
		dataset.put("draftMode", activityLog.isDraftMode());
		dataset.put("masterDraftMode", flightAssignment.isDraftMode());
		dataset.put("readonly", false);

		super.getResponse().addData(dataset);
	}

}
