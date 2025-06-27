
package acme.features.flightCrewMember.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S3.ActivityLog;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class ActivityLogDeleteService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ActivityLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		String method = super.getRequest().getMethod();
		if (method.equals("GET"))
			status = false;
		else {
			int activityLogId;

			ActivityLog activityLog;

			activityLogId = super.getRequest().getData("id", int.class);
			activityLog = this.repository.findActivityLogById(activityLogId);
			int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
			boolean authorised1 = this.repository.existsFlightCrewMember(flightCrewMemberId);
			boolean authorised = authorised1 && this.repository.thatActivityLogIsOf(activityLogId, flightCrewMemberId);

			status = authorised && activityLog != null && activityLog.isDraftMode();
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
		super.bindObject(activityLog, "typeOfIncident", "description", "severityLevel");
	}

	@Override
	public void validate(final ActivityLog activityLog) {
		;
	}

	@Override
	public void perform(final ActivityLog activityLog) {

		this.repository.delete(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;

		dataset = super.unbindObject(activityLog, "draftMode", "typeOfIncident", "description", "severityLevel");

		super.getResponse().addData(dataset);
	}

}
