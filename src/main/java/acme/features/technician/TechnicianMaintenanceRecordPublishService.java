
package acme.features.technician;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S5.MaintenanceRecord;
import acme.entities.S5.MaintenanceRecordStatus;
import acme.realms.Technician;

@GuiService
public class TechnicianMaintenanceRecordPublishService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		int masterId;
		MaintenanceRecord maintenanceRecord;

		masterId = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findOneById(masterId);

		boolean isDraft = maintenanceRecord != null && maintenanceRecord.isDraftMode();
		boolean isOwner = maintenanceRecord != null && super.getRequest().getPrincipal().hasRealm(maintenanceRecord.getTechnician());

		if (!isDraft) {
			super.getResponse().setAuthorised(false);
			super.getResponse().setOops(new IllegalStateException("No se puede modificar, borrar o publicar un registro ya publicado."));
		} else if (!isOwner) {
			super.getResponse().setAuthorised(false);
			super.getResponse().setOops(new IllegalStateException("Solo el propietario puede modificar o borrar el registro."));
		} else
			super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		MaintenanceRecord maintenanceRecord;
		int id;

		id = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findOneById(id);

		super.getBuffer().addData(maintenanceRecord);
	}

	@Override
	public void bind(final MaintenanceRecord maintenanceRecord) {
		// No hay campos adicionales que enlazar en publicación
	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {
		int total = this.repository.countAllTasksByRecordId(maintenanceRecord.getId());
		int published = this.repository.countPublishedTasksByRecordId(maintenanceRecord.getId());

		// Validación: Al menos una tarea publicada
		if (published == 0) {
			super.state(false, "*", "Debe haber al menos una tarea publicada para poder publicar el registro.");
			return;
		}

		// Validación: No debe haber tareas no publicadas
		if (published < total) {
			super.state(false, "*", "No debe haber tareas no publicadas para publicar el registro.");
			return;
		}
	}

	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
		maintenanceRecord.setDraftMode(false);
		maintenanceRecord.setStatus(MaintenanceRecordStatus.COMPLETED); // Ajusta según tus estados
		this.repository.save(maintenanceRecord);
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		SelectChoices choices;
		Dataset dataset;

		choices = SelectChoices.from(MaintenanceRecordStatus.class, maintenanceRecord.getStatus());

		dataset = super.unbindObject(maintenanceRecord, "maintenanceMoment", "nextInspectionDate", "estimatedCost", "notes", "draftMode");
		dataset.put("status", choices.getSelected().getKey());
		dataset.put("statuses", choices);
		dataset.put("aircraft", maintenanceRecord.getAircraft().getRegistrationnumber());

		super.getResponse().addData(dataset);
	}
}
