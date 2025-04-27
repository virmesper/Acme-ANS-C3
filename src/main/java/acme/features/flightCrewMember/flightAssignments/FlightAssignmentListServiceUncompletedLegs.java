
package acme.features.flightCrewMember.flightAssignments;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S3.FlightAssignment;
import acme.realms.FlightCrewMember;

@GuiService
public class FlightAssignmentListServiceUncompletedLegs extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightAssignmentRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		FlightCrewMember flightCrewMember = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();

		Collection<FlightAssignment> uncompletedFlightAssignments = this.repository.findAllPlannedFlightAssignments(MomentHelper.getCurrentMoment());

		super.getBuffer().addData(uncompletedFlightAssignments);
	}

	@Override
	public void unbind(final FlightAssignment uncompletedFlightAssignments) {

		Dataset dataset = super.unbindObject(uncompletedFlightAssignments, "duty", "moment", "currentStatus", "remarks", "leg");

		dataset.put("leg", uncompletedFlightAssignments.getLeg().getFlightNumber());

		super.addPayload(dataset, uncompletedFlightAssignments, "duty", "moment", "currentStatus", "remarks", "leg");
		super.getResponse().addData(dataset);

	}

}
