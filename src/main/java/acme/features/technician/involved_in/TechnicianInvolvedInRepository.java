
package acme.features.technician.involved_in;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.student5.InvolvedIn;
import acme.entities.student5.MaintenanceRecord;
import acme.entities.student5.Task;
import acme.realms.Technician;

@Repository
public interface TechnicianInvolvedInRepository extends AbstractRepository {

	//	@Query("select i from InvolvedIn i where i.maintenanceRecord.id = :masterId")
	//	Collection<InvolvedIn> findInvolvesByMasterId(int masterId);
	//
	//	@Query("select i from InvolvedIn i where i.id = :id")
	//	InvolvedIn findInvolvesById(int id);

	//	@Query("select m from MaintenanceRecord m where m.id = :id")
	//	MaintenanceRecord findMaintenanceRecordById(int id);

	//	@Query("select t from Task t")
	//	Collection<Task> findAllTasks();
	//
	//	@Query("select i.maintenanceRecord from InvolvedIn i where i.id = :id")
	//	MaintenanceRecord findMaintenanceRecordByInvolvesId(int id);

	@Query("select m from MaintenanceRecord m where m.id = :maintenanceRecordId")
	MaintenanceRecord findMaintenanceRecordById(int maintenanceRecordId);

	@Query("select t from Task t where t.id = :taskId")
	Task findTaskById(int taskId);

	@Query("select t from Task t where t not in (select i.task from InvolvedIn i where i.maintenanceRecord = :maintenanceRecord) and (t.draftMode = false or t.technician = :technician)")
	Collection<Task> findValidTasksToLink(MaintenanceRecord maintenanceRecord, Technician technician);

	@Query("select t from Task t where t in (select i.task from InvolvedIn i where i.maintenanceRecord = :maintenanceRecord)")
	Collection<Task> findValidTasksToUnlink(MaintenanceRecord maintenanceRecord);

	@Query("select i.task from InvolvedIn i where i.maintenanceRecord = :maintenanceRecord")
	Collection<InvolvedIn> findInvolvedInByMaintenanceRecord(MaintenanceRecord maintenanceRecord);

	@Query("select i from InvolvedIn i where i.maintenanceRecord = :maintenanceRecord and i.task = :task")
	InvolvedIn findInvolvedInByMaintenanceRecordAndTask(MaintenanceRecord maintenanceRecord, Task task);

}
