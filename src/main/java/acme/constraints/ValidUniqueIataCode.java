
package acme.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = UniqueIataCodeValidator.class)
public @interface ValidUniqueIataCode {

	String message() default "El IataCode debe ser único para Aeropuertos y Aerolíneas.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
