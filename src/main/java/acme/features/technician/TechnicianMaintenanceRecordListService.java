
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
		Collection<MaintenanceRecord> objects;
		int technicianId;

		technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		// Obtener todos los registros, tanto borradores como publicados
		objects = this.repository.findManyByTechnicianId(technicianId);

		super.getBuffer().addData(objects);
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		Dataset dataset;

		dataset = super.unbindObject(maintenanceRecord, "maintenanceMoment", "status", "nextInspectionDate", "draftMode");

		// Estado del mantenimiento
		if (maintenanceRecord.getStatus() != null)
			dataset.put("status", maintenanceRecord.getStatus().name());
		else
			dataset.put("status", "UNKNOWN");

		// Manejo del momento del mantenimiento
		if (maintenanceRecord.getMaintenanceMoment() != null)
			dataset.put("maintenanceMoment", maintenanceRecord.getMaintenanceMoment().toString());
		else
			dataset.put("maintenanceMoment", "N/A");

		// Manejo de la próxima inspección
		if (maintenanceRecord.getNextInspectionDate() != null)
			dataset.put("nextInspectionDate", maintenanceRecord.getNextInspectionDate().toString());
		else
			dataset.put("nextInspectionDate", "N/A");

		// Verificar si el objeto Aircraft no es nulo antes de acceder al número de registro
		if (maintenanceRecord.getAircraft() != null && maintenanceRecord.getAircraft().getRegistrationnumber() != null)
			dataset.put("aircraft", maintenanceRecord.getAircraft().getRegistrationnumber());
		else
			dataset.put("aircraft", "N/A");

		super.addPayload(dataset, maintenanceRecord);
		super.getResponse().addData(dataset);
	}

}
