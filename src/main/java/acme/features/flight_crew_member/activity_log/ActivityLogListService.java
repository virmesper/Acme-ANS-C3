
package acme.features.flight_crew_member.activity_log;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student3.ActivityLog;
import acme.entities.student3.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class ActivityLogListService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ActivityLogRepository	repository;

	// AbstractGuiService interface -------------------------------------------

	private static final String		MASTER_ID	= "masterId";


	@Override
	public void authorise() {
		boolean status = false;
		int masterId;
		FlightAssignment flightAssignment;

		masterId = super.getRequest().getData(ActivityLogListService.MASTER_ID, int.class);
		flightAssignment = this.repository.findFlightAssignmentById(masterId);
		if (flightAssignment != null) {

			int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
			boolean authorised = this.repository.existsFlightCrewMember(flightCrewMemberId);

			status = authorised && flightAssignment != null;
			boolean isHis = flightAssignment.getFlightCrewMember().getId() == flightCrewMemberId;
			status = status && isHis && this.repository.isFlightAssignmentCompleted(MomentHelper.getCurrentMoment(), masterId);
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {

		Collection<ActivityLog> activityLog;

		Integer masterId = super.getRequest().getData(ActivityLogListService.MASTER_ID, Integer.class);

		activityLog = this.repository.findActivityLogsByMasterId(masterId);
		super.getResponse().addGlobal(ActivityLogListService.MASTER_ID, masterId);
		super.getBuffer().addData(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;

		dataset = super.unbindObject(activityLog, "registrationMoment", "typeOfIncident", "description", "severityLevel", "draftMode");
		super.addPayload(dataset, activityLog, "registrationMoment", "typeOfIncident");

		boolean showCreate;

		Integer masterId = super.getRequest().getData(ActivityLogListService.MASTER_ID, Integer.class);

		showCreate = this.repository.flightAssignmentAssociatedWithCompletedLeg(masterId, MomentHelper.getCurrentMoment());

		super.getResponse().addGlobal(ActivityLogListService.MASTER_ID, masterId);
		super.getResponse().addGlobal("showCreate", showCreate);
		super.getResponse().addData(dataset);

	}

}
