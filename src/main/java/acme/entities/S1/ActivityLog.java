
package acme.entities.S1;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import acme.client.components.basis.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ActivityLog extends AbstractEntity {

	// Serialisation version
	private static final long serialVersionUID = 1L;

	// Registration moment (must be in the past)
	@Past
	@Column(nullable = false)
	private LocalDateTime registrationMoment;

	// Type of incident (up to 50 characters)
	@Size(max = 50)
	@Column(nullable = false)
	private String incidentType;

	// Description (up to 255 characters)
	@Size(max = 255)
	private String description;

	// Severity level (0 to 10)
	@Min(0)
	@Max(10)
	@Column(nullable = false)
	private Integer severityLevel;

	// Relationships
	@ManyToOne(optional = false)
	private FlightAssignment flightAssignment;
}
