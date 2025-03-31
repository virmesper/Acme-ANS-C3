
package acme.constraints;

import java.util.List;

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

			// Verificación de monotonía creciente del porcentaje
			List<TrackingLog> trackingLogs = this.repository.findOrderTrackingLog(trackingLog.getClaim().getId()).get();
			Integer pos = trackingLogs.indexOf(trackingLog);

			if (pos > 0) { // Verificar solo si hay un log anterior
				TrackingLog previousLog = trackingLogs.get(pos - 1);
				if (previousLog.getResolutionPercentage() > trackingLog.getResolutionPercentage())
					super.state(context, false, "ResolutionPercentage", "acme.validation.trackinglog.resolution.monotony");
			}
		}

		result = !super.hasErrors(context);
		return result;
	}
}
