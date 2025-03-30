
package acme.features.authenticated.technician.maintenanceRecord;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.S5.MaintenanceRecord;

@GuiService
public class TechnicianMaintenanceRecordListService extends AbstractGuiService<Authenticated, MaintenanceRecord> {

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		int technicianId = this.repository.findTechnicianByUserAccountId(userAccountId).getId();
		Collection<MaintenanceRecord> records = this.repository.findManyByTechnicianId(technicianId);
		super.getBuffer().addData(records);
	}

	@Override
	public void unbind(final MaintenanceRecord record) {
		Dataset dataset = super.unbindObject(record, "maintenanceMoment", "status", "nextInspectionDate", "estimatedCost");
		super.getResponse().addData(dataset);
	}

	@PostConstruct
	public void debug() {
		System.out.println(">>> TechnicianMaintenanceRecordListService loaded");
	}

}
