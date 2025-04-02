
package acme.features.flightcrewmember.activitylog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.entities.S3.ActivityLog;
import acme.entities.S3.FlightAssignment;
import acme.realms.FlightCrewMember;

@Service
public class FlightCrewMemberActivityLogCreateService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private FlightCrewMemberActivityLogRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		ActivityLog log = new ActivityLog();
		FlightCrewMember crew = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();
		log.setFlightCrewMember(crew);
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
		Dataset dataset = super.unbindObject(object, "registrationMoment", "incidentType", "description", "severityLevel", "flightAssignment");

		FlightCrewMember crew = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();
		Collection<FlightAssignment> assignments = this.repository.findAssignmentsByCrewMemberId(crew.getId());

		SelectChoices choices = SelectChoices.from(assignments, "remarks", object.getFlightAssignment());
		dataset.put("flightAssignments", choices);

		super.getResponse().addData(dataset);
	}

}
