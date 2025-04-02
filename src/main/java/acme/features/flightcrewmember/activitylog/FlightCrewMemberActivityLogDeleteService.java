
package acme.features.flightcrewmember.activitylog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.services.AbstractGuiService;
import acme.entities.S3.ActivityLog;
import acme.realms.FlightCrewMember;

@Service
public class FlightCrewMemberActivityLogDeleteService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private FlightCrewMemberActivityLogRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		ActivityLog log = this.repository.findOneActivityLogById(id);
		boolean isOwner = log != null && super.getRequest().getPrincipal().hasRealm(log.getFlightCrewMember());
		boolean isDeletable = this.repository.findConfirmedAssignmentsByCrewMemberId(log.getFlightCrewMember().getId()).isEmpty();

		super.getResponse().setAuthorised(isOwner && isDeletable);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		ActivityLog log = this.repository.findOneActivityLogById(id);
		super.getBuffer().addData(log);
	}

	@Override
	public void bind(final ActivityLog object) {
		// No binding needed for delete
	}

	@Override
	public void validate(final ActivityLog object) {
		// No validation needed for delete
	}

	@Override
	public void perform(final ActivityLog object) {
		this.repository.delete(object);
	}

	@Override
	public void unbind(final ActivityLog object) {
		// No unbinding needed for delete
	}
}
