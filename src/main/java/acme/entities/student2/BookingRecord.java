
package acme.entities.student2;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "booking_record", indexes = {
	@Index(name = "idx_bookingrecord_booking", columnList = "booking_id"), @Index(name = "idx_bookingrecord_passenger", columnList = "passenger_id")
}, uniqueConstraints = {
	@UniqueConstraint(name = "uq_booking_passenger", columnNames = {
		"booking_id", "passenger_id"
	})
})
public class BookingRecord extends AbstractEntity {
	// Serialisation version --------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------

	// Relationships -----------------------------------------------------

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Booking				booking;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Passenger			passenger;

}
