
package acme.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

import acme.client.components.basis.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "bookings")

public class Booking extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@NotBlank
	@Column(length = 8, unique = true, nullable = false)
	@Pattern(regexp = "^[A-Z0-9]{6,8}$", message = "Invalid locator code format")
	private String				locatorCode;

	@PastOrPresent
	@Column(nullable = false)
	private LocalDateTime		purchaseMoment;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TravelClass			travelClass;

	@Positive
	@Column(nullable = false)
	private Double				price;

	@Column(length = 4, nullable = true)
	private String				lastCardDigits;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------
	@ManyToOne
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer			customer;

}
