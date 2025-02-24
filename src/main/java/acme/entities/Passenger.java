
package acme.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "passengers")

public class Passenger {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long		id;

	@Size(max = 255)
	@Column(length = 255, nullable = false)
	private String		fullName;

	@Email
	@Column(nullable = false)
	private String		email;

	@Column(length = 9, unique = true, nullable = false)
	@Pattern(regexp = "^[A-Z0-9]{6,9}$", message = "Invalid passport number format")
	private String		passportNumber;

	@Past
	@Column(nullable = false)
	private LocalDate	dateOfBirth;

	@Size(max = 50)
	@Column(length = 50, nullable = true)
	private String		specialNeeds;

	@ManyToOne
	@JoinColumn(name = "booking_id", nullable = false)
	private Booking		booking;
}
