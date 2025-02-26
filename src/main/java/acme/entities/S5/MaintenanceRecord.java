
package acme.entities.S5;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "maintenance_records")
public class MaintenanceRecord extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------	

	@Mandatory
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date				maintenanceDate;

	@Mandatory
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status				status;

	@Mandatory
	@Temporal(TemporalType.DATE)
	@Column(nullable = false)
	private Date				nextInspectionDate;

	@Mandatory
	@ValidMoney
	@Min(0)
	@Column(nullable = false)
	private Money				estimatedCost;

	@Optional
	@ValidString(max = 255)
	@Column(length = 255)
	private String				notes;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Technician			technician; //un tecnico puede realizar varios registros de mantenimiento

}
