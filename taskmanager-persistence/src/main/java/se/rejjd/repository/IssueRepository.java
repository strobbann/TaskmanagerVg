package se.rejjd.repository;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;

import se.rejjd.model.Issue;

public interface IssueRepository extends CrudRepository<Issue, Long> {

	Collection<Issue> findByWorkItemId(Long id);
	
	Issue findOne(Long id);
}
