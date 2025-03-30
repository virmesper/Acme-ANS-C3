
package acme.features.flightcrewmember.activitylog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.entities.S3.ActivityLog;
import acme.entities.S3.FlightAssignment;
import acme.realms.FlightCrewMember;

@Service
public class FlightCrewMemberActivityLogPublishService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private FlightCrewMemberActivityLogRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		ActivityLog log = this.repository.findOneActivityLogById(id);
		boolean isOwner = log != null && super.getRequest().getPrincipal().hasRealm(log.getFlightCrewMember());

		boolean canPublish = !this.repository.findConfirmedAssignmentsByCrewMemberId(log.getFlightCrewMember().getId()).isEmpty();

		super.getResponse().setAuthorised(isOwner && canPublish);
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
	}

	@Override
	public void perform(final ActivityLog object) {
		// No cambio de estado porque no hay campo "published"
		// Pero podrías añadir un log, notificación, etc.
	}

	@Override
	public void unbind(final ActivityLog object) {
		Dataset dataset = super.unbindObject(object, "registrationMoment", "incidentType", "description", "severityLevel", "flightAssignment");

		FlightCrewMember crew = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();
		Collection<FlightAssignment> assignments = this.repository.findAssignmentsByCrewMemberId(crew.getId());

		SelectChoices choices = SelectChoices.from(assignments, "remarks", object.getFlightAssignment());
		dataset.put("flightAssignments", choices);

		super.getResponse().addData(dataset);
	}

}
