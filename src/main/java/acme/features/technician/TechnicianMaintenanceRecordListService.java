
package acme.features.technician;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S5.MaintenanceRecord;
import acme.realms.Technician;

@GuiService
public class TechnicianMaintenanceRecordListService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<MaintenanceRecord> object;
		int technicianId;

		technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		object = this.repository.findMaintenanceRecordsByTechnicianId(technicianId);

		super.getBuffer().addData(object);
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		Dataset dataset;

		// Usamos el método estándar para obtener el dataset base
		dataset = super.unbindObject(maintenanceRecord, "maintenanceMoment", "status", "nextInspectionDate");

		// Reemplazar el valor del status para asegurar que sea el correcto
		if (maintenanceRecord.getStatus() != null)
			dataset.put("status", maintenanceRecord.getStatus().name());
		else
			dataset.put("status", "UNKNOWN");

		// Formatear correctamente el valor de la fecha de mantenimiento
		if (maintenanceRecord.getMaintenanceMoment() != null)
			dataset.put("maintenanceMoment", maintenanceRecord.getMaintenanceMoment().toString());
		else
			dataset.put("maintenanceMoment", "N/A");

		// Formatear correctamente la fecha de la próxima inspección
		if (maintenanceRecord.getNextInspectionDate() != null)
			dataset.put("nextInspectionDate", maintenanceRecord.getNextInspectionDate().toString());
		else
			dataset.put("nextInspectionDate", "N/A");

		// Verificar si el objeto Aircraft no es nulo antes de acceder al número de registro
		if (maintenanceRecord.getAircraft() != null && maintenanceRecord.getAircraft().getRegistrationnumber() != null) {
			String registrationNumber = maintenanceRecord.getAircraft().getRegistrationnumber();
			dataset.put("aircraft", registrationNumber);
		} else
			dataset.put("aircraft", "N/A");

		// Añadir el dataset al payload y a la respuesta
		super.addPayload(dataset, maintenanceRecord);
		super.getResponse().addData(dataset);
	}

}
