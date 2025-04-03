
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
		// Validación de que el objeto no sea nulo
		assert maintenanceRecord != null : "Maintenance record is null";

		// Validación del número de registro de la aeronave
		if (maintenanceRecord.getAircraft() == null || maintenanceRecord.getAircraft().getRegistrationnumber() == null || maintenanceRecord.getAircraft().getRegistrationnumber().trim().isEmpty())
			super.state(false, "aircraft", "El número de registro de la aeronave no puede estar vacío");

		// Validación del coste estimado
		if (maintenanceRecord.getEstimatedCost() == null || maintenanceRecord.getEstimatedCost().getAmount().compareTo(0.0) < 0)
			super.state(false, "estimatedCost", "El costo estimado no puede ser negativo o nulo");

		// Validación de la fecha de la próxima inspección (debe ser una fecha futura)
		if (maintenanceRecord.getNextInspectionDate() == null || !MomentHelper.isFuture(maintenanceRecord.getNextInspectionDate()))
			super.state(false, "nextInspectionDate", "La fecha de la próxima inspección debe ser en el futuro");

		// Si todas las validaciones pasan
		super.state(true, "valid", "El registro de mantenimiento es válido");
	}
	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
		try {
			// Log de información detallada
			System.out.println("Guardando el registro de mantenimiento:");
			System.out.println("Aircraft: " + maintenanceRecord.getAircraft().getRegistrationnumber());
			System.out.println("Next Inspection Date: " + maintenanceRecord.getNextInspectionDate());
			System.out.println("Estimated Cost: " + maintenanceRecord.getEstimatedCost());
			System.out.println("Notes: " + maintenanceRecord.getNotes());
			System.out.println("Draft Mode: " + maintenanceRecord.isDraftMode());

			// Guardar en la base de datos
			this.repository.save(maintenanceRecord);
			System.out.println("Registro guardado con ID: " + maintenanceRecord.getId());
			super.getResponse().setView("/technician/maintenance-record/list");
		} catch (Exception e) {
			super.getResponse().setOops(e);
			System.err.println("Error al guardar el registro: " + e.getMessage());
		}
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		Dataset dataset = super.unbindObject(maintenanceRecord, "nextInspectionDate", "estimatedCost", "notes", "draftMode");

		if (maintenanceRecord.getAircraft() != null && maintenanceRecord.getAircraft().getRegistrationnumber() != null)
			dataset.put("aircraft", maintenanceRecord.getAircraft().getRegistrationnumber());
		else
			dataset.put("aircraft", "N/A");

		super.getResponse().addData(dataset);
	}
}
