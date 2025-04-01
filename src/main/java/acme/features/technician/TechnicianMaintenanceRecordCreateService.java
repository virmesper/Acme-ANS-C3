
package acme.features.technician;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Group.Aircraft;
import acme.entities.S5.MaintenanceRecord;
import acme.entities.S5.MaintenanceRecordStatus;
import acme.realms.Technician;

@GuiService
public class TechnicianMaintenanceRecordCreateService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		MaintenanceRecord maintenanceRecord;
		Technician technician;

		technician = (Technician) super.getRequest().getPrincipal().getActiveRealm();

		maintenanceRecord = new MaintenanceRecord();
		maintenanceRecord.setDraftMode(true);
		maintenanceRecord.setTechnician(technician);

		super.getBuffer().addData(maintenanceRecord);
	}

	@Override
	public void bind(final MaintenanceRecord maintenanceRecord) {
		String aircraftRegistrationnumber;
		Aircraft aircraft;
		Date currentMoment;

		aircraftRegistrationnumber = super.getRequest().getData("aircraft", String.class);
		aircraft = this.repository.findAircraftByRegistrationNumber(aircraftRegistrationnumber);
		currentMoment = MomentHelper.getCurrentMoment();

		super.bindObject(maintenanceRecord, "nextInspectionDate", "estimatedCost", "notes");
		maintenanceRecord.setMaintenanceMoment(currentMoment);
		maintenanceRecord.setStatus(MaintenanceRecordStatus.PENDING);
		maintenanceRecord.setAircraft(aircraft);
	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {
		;
	}

	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
		this.repository.save(maintenanceRecord);
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		Dataset dataset;
		//	SelectChoices choices;

		//	choices = SelectChoices.from(MaintenanceRecordStatus.class, maintenanceRecord.getStatus());

		dataset = super.unbindObject(maintenanceRecord, "nextInspectionDate", "estimatedCost", "notes", "draftMode");
		dataset.put("aircraft", "");

		super.getResponse().addData(dataset);
	}
}
