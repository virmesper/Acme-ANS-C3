
package acme.features.technician;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S5.MaintenanceRecord;
import acme.realms.Technician;

@GuiService
public class TechnicianMaintenanceRecordDeleteService extends AbstractGuiService<Technician, MaintenanceRecord> {

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
			super.getResponse().setOops(new IllegalStateException("No se puede borrar un registro ya publicado."));
		} else if (!isOwner) {
			super.getResponse().setAuthorised(false);
			super.getResponse().setOops(new IllegalStateException("Solo el propietario puede borrar el registro."));
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
		// No hay campos adicionales que enlazar en eliminación
	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {
		// Validación adicional si es necesaria
	}

	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
		if (maintenanceRecord.isDraftMode())
			this.repository.delete(maintenanceRecord);
		else
			throw new IllegalStateException("Cannot delete a published record");
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		Dataset dataset = super.unbindObject(maintenanceRecord, "maintenanceMoment", "nextInspectionDate", "estimatedCost", "notes", "draftMode");
		super.getResponse().addData(dataset);
	}
}
