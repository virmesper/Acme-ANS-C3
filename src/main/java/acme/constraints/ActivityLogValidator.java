
package acme.constraints;

import java.util.Date;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.S1.Leg;
import acme.entities.S3.ActivityLog;
import acme.entities.S3.FlightAssignment;

@Validator
public class ActivityLogValidator extends AbstractValidator<ValidActivityLog, ActivityLog> {

	@Override
	protected void initialise(final ValidActivityLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final ActivityLog activityLog, final ConstraintValidatorContext context) {
		if (activityLog == null)
			return false;
		FlightAssignment flightAssignment = activityLog.getFlightAssignment();
		if (activityLog.getRegistrationMoment() == null || flightAssignment == null)
			return false;
		Leg leg = flightAssignment.getLeg();
		if (leg == null || leg.getScheduledArrival() == null)
			return false;
		Date activityLogMoment = activityLog.getRegistrationMoment();
		Date scheduledArrival = leg.getScheduledArrival();
		Boolean activityLogMomentIsAfterscheduledArrival = MomentHelper.isAfter(activityLogMoment, scheduledArrival);
		if (!activityLog.isDraftMode())
			super.state(context, activityLogMomentIsAfterscheduledArrival, "WrongActivityLogDate", "{acme.validation.activityLog.wrongMoment.message}");

		return !super.hasErrors(context);

	}

}
