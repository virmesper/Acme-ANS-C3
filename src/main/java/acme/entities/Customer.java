
package acme.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "customers")

public class Customer {

	@Id
	@Column(length = 10, unique = true, nullable = false)
	@Pattern(regexp = "^[A-Z]{2,3}\\d{6}$", message = "Invalid identifier format")
	private String	identifier;

	@Column(length = 15, nullable = false)
	@Pattern(regexp = "^\\+?\\d{6,15}$", message = "Invalid phone number format")
	private String	phoneNumber;

	@Column(length = 255, nullable = false)
	private String	address;

	@Column(length = 50, nullable = false)
	private String	city;

	@Column(length = 50, nullable = false)
	private String	country;

	@Column(nullable = true)
	@Min(0)
	@Max(500000)
	private int		earnedPoints;
}
