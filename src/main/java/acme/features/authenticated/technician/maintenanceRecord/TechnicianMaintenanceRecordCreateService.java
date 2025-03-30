
package acme.features.authenticated.technician.maintenanceRecord;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S5.MaintenanceRecord;

@GuiService
public class TechnicianMaintenanceRecordCreateService extends AbstractGuiService<Authenticated, MaintenanceRecord> {

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		MaintenanceRecord record = new MaintenanceRecord();
		record.setMaintenanceMoment(new Date());
		record.setTechnician(this.repository.findTechnicianByUserAccountId(userAccountId));
		super.getBuffer().addData(record);
	}

	@Override
	public void bind(final MaintenanceRecord record) {
		super.bindObject(record, "status", "nextInspectionDate", "estimatedCost", "notes", "aircraft");
	}

	@Override
	public void validate(final MaintenanceRecord record) {
		// Puedes agregar validaciones personalizadas aquí si necesitas
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
