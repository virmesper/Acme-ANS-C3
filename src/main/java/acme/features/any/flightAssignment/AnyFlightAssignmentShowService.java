
package acme.features.any.flightAssignment;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S3.CurrentStatus;
import acme.entities.S3.Duty;
import acme.entities.S3.FlightAssignment;

@GuiService
public class AnyFlightAssignmentShowService extends AbstractGuiService<Any, FlightAssignment> {

	@Autowired
	private AnyFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findAssignmentById(id);

		boolean authorised = assignment != null && !assignment.isDraftMode() && !assignment.getLeg().isDraftMode();

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findAssignmentById(id);
		super.getBuffer().addData(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		SelectChoices choicesCrewRole = SelectChoices.from(Duty.class, assignment.getDuty());
		SelectChoices choicesAssignmentStatus = SelectChoices.from(CurrentStatus.class, assignment.getCurrentStatus());

		Dataset dataset = super.unbindObject(assignment, "duty", "moment", "currentStatus", "remarks", "leg.flightNumber", "leg.departureAirport.name", "leg.arrivalAirport.name", "leg.scheduledDeparture", "leg.scheduledArrival",
			"flightCrewMember.employeeCode");

		dataset.put("duties", choicesCrewRole);
		dataset.put("currentStatuses", choicesAssignmentStatus);
		dataset.put("masterId", assignment.getId());

		super.getResponse().addData(dataset);
	}

}
