
package acme.features.technician.involvedIn;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S5.InvolvedIn;
import acme.entities.S5.MaintenanceRecord;
import acme.entities.S5.Task;
import acme.realms.Technician;

@GuiService
public class TechnicianInvolvedInDeleteService extends AbstractGuiService<Technician, InvolvedIn> {

	@Autowired
	private TechnicianInvolvedInRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int id;
		InvolvedIn involves;

		id = super.getRequest().getData("id", int.class);
		involves = this.repository.findInvolvesById(id);
		status = involves != null && super.getRequest().getPrincipal().hasRealm(involves.getMaintenanceRecord().getTechnician());

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		InvolvedIn involves;
		int id;

		id = super.getRequest().getData("id", int.class);
		involves = this.repository.findInvolvesById(id);

		super.getBuffer().addData(involves);
	}

	@Override
	public void bind(final InvolvedIn involves) {
		Task task;
		int id;
		MaintenanceRecord maintenanceRecord;

		id = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordByInvolvesId(id);
		task = super.getRequest().getData("task", Task.class);

		super.bindObject(involves);
		involves.setTask(task);
		involves.setMaintenanceRecord(maintenanceRecord);
	}

	@Override
	public void validate(final InvolvedIn involves) {
		;
	}

	@Override
	public void perform(final InvolvedIn involves) {
		this.repository.delete(involves);
	}

	@Override
	public void unbind(final InvolvedIn involves) {
		Dataset dataset;
		SelectChoices taskChoices;
		Collection<Task> tasks;
		final boolean draftRecord;

		tasks = this.repository.findAllTasks();
		taskChoices = SelectChoices.from(tasks, "ticker", involves.getTask());

		dataset = super.unbindObject(involves, "task");
		dataset.put("masterId", super.getRequest().getData("masterId", int.class));
		dataset.put("maintenanceRecord", involves.getMaintenanceRecord().getId());
		dataset.put("task", taskChoices.getSelected().getKey());
		dataset.put("tasks", taskChoices);
		dataset.put("taskTechnician", involves.getTask().getTechnician().getLicenseNumber());

		draftRecord = involves.getMaintenanceRecord().isDraftMode();
		super.getResponse().addGlobal("draftRecord", draftRecord);

		super.getResponse().addData(dataset);
	}
}
