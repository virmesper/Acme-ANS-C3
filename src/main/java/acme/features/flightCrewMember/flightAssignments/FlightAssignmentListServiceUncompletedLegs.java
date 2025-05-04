
package acme.features.flightCrewMember.flightAssignments;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S3.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

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
		List<FlightAssignment> flightAssignments;
		List<FlightAssignment> myFlightAssignments;
		FlightCrewMember flightCrewMember = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();

		flightAssignments = this.repository.findUncompletedFlightAssignmentsThatArePublished(MomentHelper.getCurrentMoment());
		myFlightAssignments = this.repository.findUncompletedFlightAssignmentsByFlightCrewMember(MomentHelper.getCurrentMoment(), flightCrewMember.getId());
		flightAssignments.addAll(myFlightAssignments);
		super.getBuffer().addData(flightAssignments);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;

		dataset = super.unbindObject(flightAssignment, "duty", "moment", "currentStatus");
		super.getResponse().addData(dataset);
	}

}
