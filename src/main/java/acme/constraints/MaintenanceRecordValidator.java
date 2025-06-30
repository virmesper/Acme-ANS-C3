
package acme.constraints;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.student5.MaintenanceRecord;

@Validator
public class MaintenanceRecordValidator extends AbstractValidator<ValidMaintenanceRecord, MaintenanceRecord> {

	// Internal state ---------------------------------------------------------

	// ConstraintValidator interface ------------------------------------------

	@Override
	protected void initialise(final ValidMaintenanceRecord maintenanceRecord) {
		assert maintenanceRecord != null;
	}

	@Override
	public boolean isValid(final MaintenanceRecord maintenanceRecord, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		Date minimumInspectionDueDate;
		boolean futureInspectionDueDate;
		Date currentMoment;
		boolean correctInspectionDueDate;

		if (maintenanceRecord.isDraftMode() && maintenanceRecord.getMoment() != null && maintenanceRecord.getNextInspectionDueTime() != null) {

			minimumInspectionDueDate = MomentHelper.deltaFromMoment(maintenanceRecord.getMoment(), 1, ChronoUnit.MINUTES);
			correctInspectionDueDate = MomentHelper.isAfterOrEqual(maintenanceRecord.getNextInspectionDueTime(), minimumInspectionDueDate);

			super.state(context, correctInspectionDueDate, "inspectionDueDate", "acme.validation.maintenance-record.inspection-due-date.message");
		}
		if (maintenanceRecord.isDraftMode() && maintenanceRecord.getNextInspectionDueTime() != null) {
			currentMoment = MomentHelper.deltaFromMoment(MomentHelper.getCurrentMoment(), 1, ChronoUnit.MINUTES);

			futureInspectionDueDate = MomentHelper.isAfterOrEqual(maintenanceRecord.getNextInspectionDueTime(), currentMoment);
			super.state(context, futureInspectionDueDate, "inspectionDueDate", "acme.validation.maintenance-record.future-inspection-due-date.message");
		}

		result = !super.hasErrors(context);

		return result;
	}

}
