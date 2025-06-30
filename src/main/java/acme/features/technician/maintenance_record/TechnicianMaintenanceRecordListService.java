
package acme.features.technician.maintenance_record;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.student5.MaintenanceRecord;
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
		Collection<MaintenanceRecord> object;
		int technicianId;

		boolean mine;
		boolean showCreate = false;

		technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		mine = super.getRequest().hasData("mine", boolean.class);

		if (mine) {
			object = this.repository.findMaintenanceRecordsByTechnicianId(technicianId);
			showCreate = true;
		} else
			object = this.repository.findPublishedMaintenanceRecords();

		super.getResponse().addGlobal("showCreate", showCreate);
		super.getBuffer().addData(object);

	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		Dataset dataset;

		dataset = super.unbindObject(maintenanceRecord, "moment", "status", "nextInspectionDueTime");
		super.addPayload(dataset, maintenanceRecord, "estimatedCost", "notes", "draftMode", "aircraft.model", "aircraft.registrationNumber", "technician.identity.fullName", "technician.licenseNumber", "technician.phoneNumber");

		super.getResponse().addData(dataset);
	}
}
