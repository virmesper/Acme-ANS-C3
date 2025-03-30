
package acme.features.authenticated.technician.maintenanceRecord;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S5.MaintenanceRecord;

@GuiService
public class TechnicianMaintenanceRecordUpdateService extends AbstractGuiService<Authenticated, MaintenanceRecord> {

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		MaintenanceRecord record = this.repository.findOneById(id);
		super.getBuffer().addData(record);
	}

	@Override
	public void bind(final MaintenanceRecord record) {
		super.bindObject(record, "status", "nextInspectionDate", "estimatedCost", "notes", "aircraft");
	}

	@Override
	public void validate(final MaintenanceRecord record) {
		// Ejemplo: Validar que no esté publicado (según lógica de tu proyecto)
	}

	@Override
	public void perform(final MaintenanceRecord record) {
		this.repository.save(record);
	}

	@Override
	public void unbind(final MaintenanceRecord record) {
		Dataset dataset = super.unbindObject(record, "maintenanceMoment", "status", "nextInspectionDate", "estimatedCost", "notes", "aircraft");
		super.getResponse().addData(dataset);
	}
}
