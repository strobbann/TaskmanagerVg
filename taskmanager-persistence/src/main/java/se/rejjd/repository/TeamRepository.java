package se.rejjd.repository;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;

import se.rejjd.model.Team;

public interface TeamRepository extends CrudRepository<Team, Long> {

	Collection<Team> findAll();
}
