
package acme.features.flightcrewmember.activitylog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.entities.S3.ActivityLog;
import acme.realms.FlightCrewMember;

@Service
public class FlightCrewMemberActivityLogShowService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private FlightCrewMemberActivityLogRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		ActivityLog log = this.repository.findOneActivityLogById(id);
		boolean isOwner = log != null && super.getRequest().getPrincipal().hasRealm(log.getFlightCrewMember());

		super.getResponse().setAuthorised(isOwner);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		ActivityLog log = this.repository.findOneActivityLogById(id);
		super.getBuffer().addData(log);
	}

	@Override
	public void unbind(final ActivityLog log) {
		Dataset dataset = super.unbindObject(log, "registrationMoment", "incidentType", "description", "severityLevel");
		dataset.put("readonly", true);
		super.getResponse().addData(dataset);
	}
}
