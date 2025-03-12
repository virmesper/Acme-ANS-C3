
package acme.entities.S5;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import acme.client.components.basis.AbstractEntity;
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
	@ManyToOne(optional = false)
	private MaintenanceRecord	maintenanceRecord;

	/**
	 * Relación Many-to-One obligatoria con Task.
	 * 
	 * Esta relación indica que cada instancia de InvolvedIn está asociada con una
	 * tarea específica. Permite saber qué tarea fue realizada como parte del
	 * mantenimiento referenciado.
	 */
	@ManyToOne(optional = false)
	private Task				task;
}
