
package acme.entities.S2;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import acme.client.components.basis.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "customers")

public class Customer extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@NotBlank
	@Column(length = 10, unique = true, nullable = false)
	@Pattern(regexp = "^[A-Z]{2,3}\\d{6}$", message = "Invalid identifier format")
	private String				identifier;

	@Column(length = 15, nullable = false)
	@Pattern(regexp = "^\\+?\\d{6,15}$", message = "Invalid phone number format")
	private String				phoneNumber;

	@Column(length = 255, nullable = false)
	private String				address;

	@Column(length = 50, nullable = false)
	private String				city;

	@Column(length = 50, nullable = false)
	private String				country;

	@Column(nullable = true)
	@Min(0)
	@Max(500000)
	private int					earnedPoints;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------
}
