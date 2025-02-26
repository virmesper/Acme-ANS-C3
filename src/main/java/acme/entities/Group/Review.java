
package acme.entities.Group;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Review extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------
	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	// Nombre o alias de la persona que lo publica
	@Mandatory
	@ValidString(max = 50)
	@Column(nullable = false, length = 50)
	private String				author;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date				postedMoment;

	@Mandatory
	@ValidString(max = 50)
	@Column(nullable = false, length = 50)
	private String				subject;

	@Mandatory
	@ValidString(max = 255)
	@Column(nullable = false, length = 255)
	private String				text;

	@Optional
	@Min(0)
	@Max(10)
	@Column
	private Double				score;

	// Indica si la experiencia es recomendada o no
	@Optional
	@Column
	private Boolean				recommended;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
