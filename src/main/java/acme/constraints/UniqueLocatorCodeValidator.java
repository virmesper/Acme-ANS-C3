
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.student2.BookingRepository;

@Validator
public class UniqueLocatorCodeValidator extends AbstractValidator<ValidUniqueLocatorCode, String> {

	@Autowired
	BookingRepository bookingRepository;


	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		if (value == null || value.trim().isEmpty())
			return false;

		Boolean existsBooking = this.bookingRepository.existsBookingWithLocatorCode(value);

		return !existsBooking;
	}

}
