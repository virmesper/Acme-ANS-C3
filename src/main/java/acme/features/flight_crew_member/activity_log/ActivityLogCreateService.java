
package acme.features.flight_crew_member.activity_log;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student3.ActivityLog;
import acme.entities.student3.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class ActivityLogCreateService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ActivityLogRepository	repository;

	// AbstractGuiService interface -------------------------------------------

	private static final String		MASTER_ID	= "master_id";


	@Override
	public void authorise() {
		boolean status = false;
		FlightAssignment flightAssignment;

		Integer masterId = super.getRequest().getData(ActivityLogCreateService.MASTER_ID, Integer.class);
		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		boolean authorised = this.repository.existsFlightCrewMember(flightCrewMemberId);

		flightAssignment = this.repository.findFlightAssignmentById(masterId);
		boolean authorised2 = false;
		if (flightAssignment != null) {
			authorised2 = this.repository.existsFlightAssignment(masterId);
			status = authorised && authorised2;
			boolean isHis = flightAssignment.getFlightCrewMember().getId() == flightCrewMemberId;

			status = status && isHis && this.repository.isFlightAssignmentCompleted(MomentHelper.getCurrentMoment(), masterId);
		}
		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		ActivityLog activityLog;
		FlightAssignment flightAssignment;

		Integer masterId = super.getRequest().getData(ActivityLogCreateService.MASTER_ID, Integer.class);
		flightAssignment = this.repository.findFlightAssignmentById(masterId);

		activityLog = new ActivityLog();
		activityLog.setFlightAssignment(flightAssignment);
		activityLog.setDraftMode(true);
		activityLog.setDescription("");
		activityLog.setRegistrationMoment(MomentHelper.getCurrentMoment());
		activityLog.setSeverityLevel(0);
		activityLog.setTypeOfIncident("");

		super.getBuffer().addData(activityLog);

	}

	@Override
	public void bind(final ActivityLog activityLog) {
		super.bindObject(activityLog, "typeOfIncident", "description", "severityLevel");
	}

	@Override
	public void validate(final ActivityLog activityLog) {
		// No se requiere validación específica al eliminar registros de actividad.
	}

	@Override
	public void perform(final ActivityLog activityLog) {

		this.repository.save(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;

		Integer masterId = super.getRequest().getData(ActivityLogCreateService.MASTER_ID, Integer.class);

		dataset = super.unbindObject(activityLog, "registrationMoment", "typeOfIncident", "description", "severityLevel", "draftMode");
		dataset.put(ActivityLogCreateService.MASTER_ID, masterId);
		dataset.put("draftMode", activityLog.isDraftMode());
		dataset.put("readonly", false);
		dataset.put("masterDraftMode", !this.repository.isFlightAssignmentAlreadyPublishedById(masterId));
		super.getResponse().addData(dataset);

	}

}
