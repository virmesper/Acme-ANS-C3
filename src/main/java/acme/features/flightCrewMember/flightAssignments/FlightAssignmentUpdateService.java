/*
 * FlightAssignmentUpdateService.java
 *
 * Copyright (C) 2012-2025 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.features.flightCrewMember.flightAssignments;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S3.CurrentStatus;
import acme.entities.S3.Duty;
import acme.entities.S3.FlightAssignment;
import acme.realms.FlightCrewMember;

@GuiService
public class FlightAssignmentUpdateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightAssignmentRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int flightAssignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(flightAssignmentId);

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {
		super.bindObject(flightAssignment, "duty", "currentStatus", "remarks", "leg");
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {
		boolean flightAssignmentNotNull;
		flightAssignmentNotNull = flightAssignment.getFlightCrewMember() == null ? false : true;
		super.state(flightAssignmentNotNull, "flightCrewMember", "acme.validation.flight-assignment.faNull.message");

	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		this.repository.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset = super.unbindObject(flightAssignment, "duty", "moment", "currentStatus", "remarks", "draftMode", "flightCrewMember", "leg");

		SelectChoices dutyChoices = SelectChoices.from(Duty.class, flightAssignment.getDuty());
		dataset.put("duties", dutyChoices);

		SelectChoices statusChoices = SelectChoices.from(CurrentStatus.class, flightAssignment.getCurrentStatus());
		dataset.put("statuses", statusChoices);

		Collection<FlightCrewMember> flightCrewMembers = this.repository.findAllflightCrewMemberFromAirline(flightAssignment.getFlightCrewMember().getAirline().getId());
		SelectChoices flightCrewMemberChoices = SelectChoices.from(flightCrewMembers, "identity.fullName", flightAssignment.getFlightCrewMember());
		dataset.put("members", flightCrewMemberChoices);
		dataset.put("readOnlyCrewMember", true);

		SelectChoices legChoices = SelectChoices.from(this.repository.findAllLegs(), "flightNumber", flightAssignment.getLeg());
		dataset.put("legs", legChoices);

		super.getResponse().addData(dataset);
	}
}
