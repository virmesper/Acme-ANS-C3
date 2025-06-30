
package acme.features.administrator.maintenance_record;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.group.Aircraft;
import acme.entities.student5.InvolvedIn;
import acme.entities.student5.MaintenanceRecord;
import acme.entities.student5.Task;

@Repository
public interface AdministratorMaintenanceRecordRepository extends AbstractRepository {

	@Query("select mr from MaintenanceRecord mr where mr.technician.id = :technicianId")
	Collection<MaintenanceRecord> findMaintenanceRecordsByTechnicianId(int technicianId);

	@Query("select mr from MaintenanceRecord mr where mr.id = :id")
	MaintenanceRecord findMaintenanceRecordById(int id);

	@Query("select mr from MaintenanceRecord mr where mr.draftMode = false")
	Collection<MaintenanceRecord> findPublishedMaintenanceRecords();

	@Query("select a from Aircraft a")
	Collection<Aircraft> findAircrafts();

	@Query("select i.task from InvolvedIn i where i.maintenanceRecord.id = :maintenanceRecordId")
	Collection<Task> findTasksByMaintenanceRecordId(int maintenanceRecordId);

	@Query("select i from InvolvedIn i where i.maintenanceRecord.id = :maintenanceRecordId")
	Collection<InvolvedIn> findInvolvesByMaintenanceRecordId(int maintenanceRecordId);

	@Query("select a from Aircraft a where a.id = :id")
	Aircraft findAircraftById(int id);
}
