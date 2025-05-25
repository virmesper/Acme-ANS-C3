
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.S3.AvailabilityStatus;
import acme.entities.S3.FlightAssignment;

@Validator
public class FlightAssignmentValidator extends AbstractValidator<ValidFlightAssignment, FlightAssignment> {

	@Override
	protected void initialise(final ValidFlightAssignment annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final FlightAssignment flightAssignment, final ConstraintValidatorContext context) {
		if (flightAssignment == null)
			return false;
		if (flightAssignment.getFlightCrewMember() == null)
			return false;

		boolean flightCrewMemberAvailable;
		flightCrewMemberAvailable = AvailabilityStatus.AVAILABLE.equals(flightAssignment.getFlightCrewMember().getAvailabilityStatus());
		super.state(context, flightCrewMemberAvailable, "flightCrewMember", "{acme.validation.FlightAssignment.flightCrewMemberNotAvailable.message}");

		return !super.hasErrors(context);
	}

}
