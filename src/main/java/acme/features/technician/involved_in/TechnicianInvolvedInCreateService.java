
package acme.features.technician.involved_in;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student5.InvolvedIn;
import acme.entities.student5.MaintenanceRecord;
import acme.entities.student5.Task;
import acme.realms.Technician;

@GuiService
public class TechnicianInvolvedInCreateService extends AbstractGuiService<Technician, InvolvedIn> {

	@Autowired
	private TechnicianInvolvedInRepository repository;


	@Override
	public void authorise() {

		boolean statusTask = true;
		boolean status = false;
		int taskId;
		Task task;
		int maintenanceRecordId;
		MaintenanceRecord maintenanceRecord;
		Technician technician;
		Collection<Task> tasks;

		technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();
		maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);

		tasks = this.repository.findValidTasksToLink(maintenanceRecord, technician);

		if (super.getRequest().hasData("task", int.class)) {
			taskId = super.getRequest().getData("task", int.class);
			task = this.repository.findTaskById(taskId);

			if (!tasks.contains(task) && taskId != 0)
				statusTask = false;
		}

		status = maintenanceRecord != null && maintenanceRecord.isDraftMode() && super.getRequest().getPrincipal().hasRealm(maintenanceRecord.getTechnician());

		super.getResponse().setAuthorised(status && statusTask);
	}

	@Override
	public void load() {
		InvolvedIn object;
		Integer maintenanceRecordId;
		MaintenanceRecord maintenanceRecord;

		maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);

		object = new InvolvedIn();
		object.setMaintenanceRecord(maintenanceRecord);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final InvolvedIn involves) {
		super.bindObject(involves, "task");
	}

	@Override
	public void validate(final InvolvedIn involves) {
		;
	}

	@Override
	public void perform(final InvolvedIn involves) {
		this.repository.save(involves);
	}

	@Override
	public void unbind(final InvolvedIn involves) {
		Dataset dataset;
		SelectChoices taskChoices;
		Collection<Task> tasks;
		int maintenanceRecordId;
		MaintenanceRecord maintenanceRecord;
		Technician technician;

		technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();
		maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);

		tasks = this.repository.findValidTasksToLink(maintenanceRecord, technician);
		taskChoices = SelectChoices.from(tasks, "description", involves.getTask());

		dataset = super.unbindObject(involves, "maintenanceRecord");
		dataset.put("maintenanceRecordId", involves.getMaintenanceRecord().getId());
		dataset.put("task", taskChoices.getSelected().getKey());
		dataset.put("tasks", taskChoices);
		dataset.put("aircraftRegistrationNumber", involves.getMaintenanceRecord().getAircraft().getRegistrationNumber());

		super.getResponse().addData(dataset);
	}
}
