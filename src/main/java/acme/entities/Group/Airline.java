
package acme.entities.Group;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import acme.client.components.basis.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "airlines")
public class Airline extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@NotBlank
	@Size(max = 50)
	@Column(length = 50, unique = true, nullable = false)
	private String				name;

	@NotBlank
	@Pattern(regexp = "^[A-Z]{2}[A-ZX]$", message = "Invalid IATA code format")
	@Column(length = 3, unique = true, nullable = false)
	private String				iataCode;

	// Tipo de aerolínea: "LUXURY", "STANDARD", "LOW-COST" 
	@Column(length = 50, nullable = false)
	private AirlineType			type;

	@Past
	@Column(nullable = false)
	private LocalDate			foundationMoment;

	@Size(max = 255)
	@Column(length = 255, nullable = true)
	private String				website;

	@Pattern(regexp = "^[+]?\\d{6,15}$", message = "Invalid phone number format")
	@Column(length = 15, nullable = true)
	private String				phoneNumber;

	@Size(max = 255)
	@Column(length = 255, nullable = true)
	private String				email;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
