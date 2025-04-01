
package acme.features.technician;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.Group.Aircraft;
import acme.entities.S5.MaintenanceRecord;
import acme.entities.S5.Task;
import acme.realms.Technician;

@Repository
public interface TechnicianMaintenanceRecordRepository extends AbstractRepository {

	// Obtener todos los registros de mantenimiento de un técnico
	@Query("select r from MaintenanceRecord r where r.technician.id = :technicianId")
	Collection<MaintenanceRecord> findManyByTechnicianId(@Param("technicianId") int technicianId);

	// Obtener un registro específico por ID
	@Query("select r from MaintenanceRecord r where r.id = :id")
	MaintenanceRecord findOneById(@Param("id") int id);

	// Contar tareas publicadas de un registro
	@Query("select count(i) from InvolvedIn i where i.maintenanceRecord.id = :recordId and i.task.published = true")
	int countPublishedTasksByRecordId(@Param("recordId") int recordId);

	// Contar todas las tareas de un registro
	@Query("select count(i) from InvolvedIn i where i.maintenanceRecord.id = :recordId")
	int countAllTasksByRecordId(@Param("recordId") int recordId);

	// Obtener técnico por cuenta de usuario
	@Query("select t from Technician t where t.userAccount.id = :userAccountId")
	Technician findTechnicianByUserAccountId(@Param("userAccountId") int userAccountId);

	// Obtener tareas asociadas a un registro de mantenimiento
	@Query("select i.task from InvolvedIn i where i.maintenanceRecord.id = :recordId")
	Collection<Task> findTasksByMaintenanceRecordId(@Param("recordId") int recordId);

	// Obtener registros de mantenimiento por ID de técnico
	@Query("select m from MaintenanceRecord m where m.technician.id = :id")
	Collection<MaintenanceRecord> findMaintenanceRecordsByTechnicianId(@Param("id") int id);

	// Obtener un registro de mantenimiento específico
	@Query("select m from MaintenanceRecord m where m.id = :id")
	MaintenanceRecord findMaintenanceRecordById(@Param("id") int id);

	// Obtener aeronave por número de registro (corrigiendo el nombre del parámetro)
	@Query("select a from Aircraft a where a.registrationnumber = :registrationnumber")
	Aircraft findAircraftByRegistrationNumber(@Param("registrationnumber") String registrationnumber);
}
