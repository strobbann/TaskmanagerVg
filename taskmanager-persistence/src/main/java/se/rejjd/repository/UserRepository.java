package se.rejjd.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import se.rejjd.model.User;

public interface UserRepository extends CrudRepository<User, Long> {

	User findUserByUserId(String userId);

	@Query("select u from #{#entityName} u where u.firstname like :fName and u.lastname like :lName and u.username like :uName")
	Collection<User> getUser(@Param("fName") String firstname, @Param("lName") String lastname,
			@Param("uName") String username);

	Collection<User> findByTeamId(Long teamId);

	Long countByTeamId(Long teamId);

}
