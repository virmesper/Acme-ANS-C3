
package acme.features.authenticated.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S5.MaintenanceRecord;
import acme.entities.S5.Task;

@GuiService
public class TechnicianMaintenanceRecordShowService extends AbstractGuiService<Authenticated, MaintenanceRecord> {

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

		Collection<Task> tasks = this.repository.findTasksByMaintenanceRecordId(id);
		super.getResponse().addGlobal("tasks", tasks); // <-- pasa las tareas a la vista
	}

	@Override
	public void unbind(final MaintenanceRecord record) {
		Dataset dataset = super.unbindObject(record, "maintenanceMoment", "status", "nextInspectionDate", "estimatedCost", "notes");
		super.getResponse().addData(dataset);
	}
}
