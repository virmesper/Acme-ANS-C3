
package acme.features.flightcrewmember.activitylog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.components.basis.AbstractRealm;
import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.entities.S3.ActivityLog;
import acme.realms.FlightCrewMember;

@Service
public class FlightCrewMemberActivityLogListService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private FlightCrewMemberActivityLogRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		AbstractRealm userId = super.getRequest().getPrincipal().getActiveRealm();
		Collection<ActivityLog> logs = this.repository.findActivityLogsByCrewMemberId(userId);
		super.getBuffer().addData(logs);
	}

	@Override
	public void unbind(final ActivityLog object) {
		Dataset dataset = super.unbindObject(object, "registrationMoment", "incidentType", "description", "severityLevel");
		super.getResponse().addData(dataset);
	}
}
