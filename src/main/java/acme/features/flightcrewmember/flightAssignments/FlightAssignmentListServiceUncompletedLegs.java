
package acme.features.flightcrewmember.flightAssignments;

import java.util.Collection;
import java.util.Date;

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
		Collection<FlightAssignment> fs;
		int flightCrewMemberid;
		Date currentDate = MomentHelper.getCurrentMoment();
		flightCrewMemberid = super.getRequest().getPrincipal().getActiveRealm().getId();
		fs = this.repository.findAssignmentsByMemberIdUnCompletedLegs(currentDate, flightCrewMemberid);
		super.getBuffer().addData(fs);

	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;

		dataset = super.unbindObject(flightAssignment, "duty", "moment", "currentStatus", "leg.scheduledArrival");
		super.addPayload(dataset, flightAssignment, "remarks");

		super.getResponse().addData(dataset);
	}
}
