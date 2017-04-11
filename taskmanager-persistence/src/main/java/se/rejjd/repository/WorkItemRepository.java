package se.rejjd.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import se.rejjd.model.WorkItem;

public interface WorkItemRepository extends CrudRepository<WorkItem, Long>, PagingAndSortingRepository<WorkItem, Long> {

	Collection<WorkItem> findByStatus(WorkItem.Status status);

	@Query("select wi FROM #{#entityName} wi JOIN wi.user u JOIN u.team t WHERE t.id = :teamId and wi.status != 'ARCHIVED'")
	Collection<WorkItem> getWorkItemsByTeamId(@Param("teamId") Long teamId);

	@Query("Select wi from #{#entityName} wi where wi.user.id = :userId and wi.status != 'ARCHIVED'")
	Collection<WorkItem> findByUserId(@Param("userId") Long userId);

	@Query("Select wi from #{#entityName} wi where wi.description Like :desc and wi.status != 'ARCHIVED'")
	Collection<WorkItem> findByDescription(@Param("desc") String description);

	@Query("select workItem from Issue i WHERE i.openIssue=1 and i.workItem.status != 'ARCHIVED'")
	Collection<WorkItem> getAllWorkItemsWithIssues();

	@Query("Select count(wi) from #{#entityName} wi where wi.user.id = :userId and wi.status != 'ARCHIVED'")
	Long countByUserId(@Param("userId") Long userId);

	Collection<WorkItem> findByDateOfCompletionBetween(String startDate, String endDate);

}
