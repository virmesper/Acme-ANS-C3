
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.S4.Indicator;
import acme.entities.S4.TrackingLog;
import acme.entities.S4.TrackingLogRepository;

@Validator
public class TrackingLogValidator extends AbstractValidator<ValidTrackingLog, TrackingLog> {

	@Autowired
	private TrackingLogRepository repository;


	@Override
	protected void initialise(final ValidTrackingLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final TrackingLog trackingLog, final ConstraintValidatorContext context) {
		boolean result;
		assert context != null;

		if (trackingLog == null)
			super.state(context, false, "TrackingLog", "acme.validation.trackinglog.no-trackinglogs");
		else {
			// Verificación del estado según el porcentaje de resolución
			if (trackingLog.getResolutionPercentage() == 100.0)
				super.state(context, !trackingLog.getIndicator().equals(Indicator.PENDING), "Status", "acme.validation.trackinglog.status.not-pending");
			else
				super.state(context, trackingLog.getIndicator().equals(Indicator.PENDING), "Status", "acme.validation.trackinglog.status.pending-required");

			// Verificación del campo de resolución
			if (trackingLog.getIndicator().equals(Indicator.PENDING))
				super.state(context, trackingLog.getResolution() == null, "Resolution", "acme.validation.trackinglog.resolution.incorrect");
			else
				super.state(context, trackingLog.getResolution() != null, "Resolution", "acme.validation.trackinglog.resolution.incorrect");
		}

		result = !super.hasErrors(context);
		return result;
	}
}
