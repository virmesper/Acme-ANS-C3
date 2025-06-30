
package acme.entities.student5;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import lombok.Getter;
import lombok.Setter;

/**
 * La entidad InvolvedIn representa una entidad intermedia que sirve de enlace entre una tarea y un registro de mantenimiento.
 * Es utilizada para rastrear qué tareas están asociadas con qué registros de mantenimiento.
 * 
 * Cada instancia de InvolvedIn indica que una tarea particular (Task) forma parte de
 * un mantenimiento específico (MaintenanceRecord).
 */

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "maintenance_record_id"),                        // 
	@Index(columnList = "task_id"),                                      // 
	@Index(columnList = "task_id, maintenance_record_id")             // 

})

public class InvolvedIn extends AbstractEntity {
	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	/**
	 * Relación Many-to-One obligatoria con MaintenanceRecord.
	 * 
	 * Esta relación indica que cada instancia de InvolvedIn está asociada con un único
	 * registro de mantenimiento. Permite rastrear en qué mantenimiento se incluye
	 * la tarea referenciada.
	 */
	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private MaintenanceRecord	maintenanceRecord;

	/**
	 * Relación Many-to-One obligatoria con Task.
	 * 
	 * Esta relación indica que cada instancia de InvolvedIn está asociada con una
	 * tarea específica. Permite saber qué tarea fue realizada como parte del
	 * mantenimiento referenciado.
	 */
	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Task				task;
}
