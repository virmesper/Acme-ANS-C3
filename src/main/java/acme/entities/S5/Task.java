
package acme.entities.S5;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tasks")
public class Task extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TaskType			type;

	@Mandatory
	@ValidString(max = 255)
	@Column(nullable = false, length = 255)
	private String				description;

	@Mandatory
	@Min(0)
	@Max(10)
	@Column(nullable = false)
	private Integer				priority;

	@Mandatory
	@Min(0)
	@Column(nullable = false)
	private Double				estimatedDuration; //duracion en horas

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private MaintenanceRecord	maintenanceRecord; //un registro de mantentimiento contiene muchas tareas

}
