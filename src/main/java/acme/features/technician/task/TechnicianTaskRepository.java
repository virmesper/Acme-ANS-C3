
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.student5.InvolvedIn;
import acme.entities.student5.MaintenanceRecord;
import acme.entities.student5.Task;

@Repository
public interface TechnicianTaskRepository extends AbstractRepository {

	@Query("select t from Task t where t.technician.id = :technicianId")
	Collection<Task> findTasksByTechnicianId(int technicianId);

	@Query("select t from Task t where t.id = :id")
	Task findTaskById(int id);

	@Query("select i from InvolvedIn i where i.task.id = :taskId")
	Collection<InvolvedIn> findInvolvesByTaskId(int taskId);

	//

	@Query("select mr from MaintenanceRecord mr where mr.id = :id")
	MaintenanceRecord findMaintenanceRecordById(int id);

	@Query("select inv.task from InvolvedIn inv where inv.maintenanceRecord.id = :masterId")
	Collection<Task> findTasksByMasterId(int masterId);

	@Query("select inv.maintenanceRecord from InvolvedIn inv where inv.task.id = :taskId")
	Collection<MaintenanceRecord> findMaintenanceRecordsByTaskId(int taskId);

	@Query("select t from Task t where t.draftMode = false")
	Collection<Task> findPublishedTasks();

}
