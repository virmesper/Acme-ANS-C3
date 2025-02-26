
package acme.entities.S5;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Min;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "technicians")
public class Technician extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(pattern = "^[A-Z]{2,3}\\d{6}$")
	@Column(unique = true, nullable = false)
	private String				licenseNumber;

	@Mandatory
	@ValidString(pattern = "^\\+?\\d{6,15}$")
	@Column(nullable = false)
	private String				phoneNumber;

	@Mandatory
	@ValidString(max = 50)
	@Column(nullable = false, length = 50)
	private String				specialisation;

	@Mandatory
	@Column(nullable = false)
	private boolean				healthTestPassed;

	@Mandatory
	@Min(0)
	@Column(nullable = false)
	private Integer				yearsOfExperience;

	@Optional
	@ValidString(max = 255)
	@Column(length = 255)
	private String				certifications;
}
