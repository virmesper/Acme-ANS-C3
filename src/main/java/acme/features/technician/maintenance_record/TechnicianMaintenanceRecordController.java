
package acme.features.technician.maintenance_record;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.services.GuiService;
import acme.entities.student5.MaintenanceRecord;
import acme.realms.Technician;

@GuiService
public class TechnicianMaintenanceRecordController extends AbstractGuiController<Technician, MaintenanceRecord> {

	@Autowired
	TechnicianMaintenanceRecordListService		listService;

	@Autowired
	TechnicianMaintenanceRecordShowService		showService;

	@Autowired
	TechnicianMaintenanceRecordUpdateService	updateService;

	@Autowired
	TechnicianMaintenanceRecordCreateService	createService;

	@Autowired
	TechnicianMaintenanceRecordDeleteService	deleteService;

	@Autowired
	TechnicianMaintenanceRecordPublishService	publishService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("delete", this.deleteService);

		// El comando "publish" se registra correctamente como "update"
		super.addCustomCommand("publish", "update", this.publishService);
	}
}
