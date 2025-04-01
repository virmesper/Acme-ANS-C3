
package acme.features.technician;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Authenticated;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S5.MaintenanceRecord;
import acme.entities.S5.MaintenanceRecordStatus;

@GuiService
public class TechnicianMaintenanceRecordPublishService extends AbstractGuiService<Authenticated, MaintenanceRecord> {

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
		// No hay campos que enlazar para publicar
	}

	@Override
	public void validate(final MaintenanceRecord record) {
		int total = this.repository.countAllTasksByRecordId(record.getId());
		int published = this.repository.countPublishedTasksByRecordId(record.getId());

		// Validación: Al menos una tarea publicada
		if (published == 0) {
			super.state(false, "*", "acme.validation.maintenanceRecord.must-have-one-task-published");
			return;
		}

		// Validación: No debe haber tareas no publicadas
		if (published < total) {
			super.state(false, "*", "acme.validation.maintenanceRecord.cannot-have-unpublished-tasks");
			return;
		}
	}

	@Override
	public void perform(final MaintenanceRecord record) {
		record.setStatus(MaintenanceRecordStatus.COMPLETED); // Ajusta según tus estados
		this.repository.save(record);
	}

	@Override
	public void unbind(final MaintenanceRecord record) {
		// No necesitas mostrar datos nuevos
	}
}
