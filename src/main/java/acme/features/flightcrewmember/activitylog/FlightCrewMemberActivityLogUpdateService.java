
package acme.features.flightcrewmember.activitylog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.entities.S3.ActivityLog;
import acme.realms.FlightCrewMember;

@Service
public class FlightCrewMemberActivityLogUpdateService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private FlightCrewMemberActivityLogRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		ActivityLog log = this.repository.findOneActivityLogById(id);
		boolean isOwner = log != null && super.getRequest().getPrincipal().hasRealm(log.getFlightCrewMember());
		boolean isEditable = this.repository.findConfirmedAssignmentsByCrewMemberId(log.getFlightCrewMember().getId()).isEmpty();

		super.getResponse().setAuthorised(isOwner && isEditable);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		ActivityLog log = this.repository.findOneActivityLogById(id);
		super.getBuffer().addData(log);
	}

	@Override
	public void bind(final ActivityLog object) {
		super.bindObject(object, "registrationMoment", "incidentType", "description", "severityLevel");
	}

	@Override
	public void validate(final ActivityLog object) {
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
		super.state(MomentHelper.isPast(object.getRegistrationMoment()), "registrationMoment", "acme.validation.moment.past");
	}

	@Override
	public void perform(final ActivityLog object) {
		this.repository.save(object);
	}

	@Override
	public void unbind(final ActivityLog object) {
		Dataset dataset = super.unbindObject(object, "registrationMoment", "incidentType", "description", "severityLevel");
		dataset.put("confirmation", false);
		dataset.put("readonly", false);
		super.getResponse().addData(dataset);
	}
}
