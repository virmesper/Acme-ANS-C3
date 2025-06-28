
package acme.features.any.flightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S3.FlightAssignment;

@GuiService
public class AnyFlightAssignmentListService extends AbstractGuiService<Any, FlightAssignment> {

	@Autowired
	protected AnyFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<FlightAssignment> assignments;

		assignments = this.repository.findAllPublishedAssignments();
		super.getBuffer().addData(assignments);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Dataset dataset;

		dataset = super.unbindObject(assignment, "leg.flightNumber", "duty", "leg.departureAirport.name", "leg.arrivalAirport.name");

		super.getResponse().addData(dataset);
	}
}
