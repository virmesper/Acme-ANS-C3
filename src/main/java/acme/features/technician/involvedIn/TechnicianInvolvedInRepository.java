
package acme.features.technician.involvedIn;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.S5.InvolvedIn;
import acme.entities.S5.MaintenanceRecord;
import acme.entities.S5.Task;

@Repository
public interface TechnicianInvolvedInRepository extends AbstractRepository {

	@Query("select i from InvolvedIn i where i.maintenanceRecord.id = :masterId")
	Collection<InvolvedIn> findInvolvesByMasterId(int masterId);

	@Query("select i from InvolvedIn i where i.id = :id")
	InvolvedIn findInvolvesById(int id);

	@Query("select m from MaintenanceRecord m where m.id = :id")
	MaintenanceRecord findMaintenanceRecordById(int id);

	@Query("select t from Task t")
	Collection<Task> findAllTasks();

	@Query("select i.maintenanceRecord from InvolvedIn i where i.id = :id")
	MaintenanceRecord findMaintenanceRecordByInvolvesId(int id);
}
