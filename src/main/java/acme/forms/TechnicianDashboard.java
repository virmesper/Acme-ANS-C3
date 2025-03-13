
package acme.forms;

import java.util.List;
import java.util.Map;

import acme.client.components.basis.AbstractForm;
import acme.client.components.datatypes.Money;
import acme.entities.S5.MaintenanceRecord;
import acme.entities.S5.StatusMR;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TechnicianDashboard extends AbstractForm {

	private static final long		serialVersionUID	= 1L;

	// Número de registros de mantenimiento agrupados por estado
	private Map<StatusMR, Integer>	maintenanceRecordsByStatus;

	// Mantenimiento con la inspección más cercana (si participa en tareas dentro de ese mantenimiento)
	// (si hay más de uno, devuelve una lista)
	private List<MaintenanceRecord>	nearestMaintenanceRecords;

	// Top 5 aeronaves con mayor número de tareas en registros de mantenimiento
	private List<String>			topFiveAircraftWithMostTasks;

	// Indicadores de costo estimado de registros de mantenimiento en el último año
	private Money					averageEstimatedMaintenanceCost;
	private Money					minimumEstimatedMaintenanceCost;
	private Money					maximumEstimatedMaintenanceCost;
	private Money					standardDeviationEstimatedMaintenanceCost;
	private Integer					countMaintenanceRecordsLastYear;

	// Indicadores de duración estimada de tareas en las que participa el técnico
	private Double					averageEstimatedTaskDuration;
	private Double					minimumEstimatedTaskDuration;
	private Double					maximumEstimatedTaskDuration;
	private Double					standardDeviationEstimatedTaskDuration;
	private Integer					countInvolvedTasks;
}
