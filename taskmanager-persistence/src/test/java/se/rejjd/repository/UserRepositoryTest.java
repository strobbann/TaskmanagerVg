package se.rejjd.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import se.rejjd.AbstractTest;
import se.rejjd.model.Team;
import se.rejjd.model.User;
import se.rejjd.service.ServiceException;
import se.rejjd.service.UserService;

public final class UserRepositoryTest extends AbstractTest {

	@Autowired
	private UserService userService;

	// @MockBean
	private UserRepository userRepository;

	private String userName = "Robbe";
	private String firstName = "Robert";
	private String lastName = "Savela";
	private String userid = "15";
	private User user = new User(userName, firstName, lastName, userid);

	@Test
	public void canGetUser() throws ServiceException {
		when(userRepository.findUserByUserId("15")).thenReturn(user);
		User userfromDb = userRepository.findUserByUserId("15");
		System.out.println(user);
		assertThat(userfromDb, is(user));
	}

	@Test
	public void canGetUserByFirstName() {
		when(userRepository.getUser(firstName, "%", "%")).thenReturn(Stream.of(user).collect(Collectors.toList()));
		List<User> users = userRepository.getUser(firstName, "%", "%").stream().collect(Collectors.toList());
		User userFromDb = users.get(0);
		assertThat(user, is(userFromDb));

	}

	@Test
	public void canGetUserByLastName() {
		when(userRepository.getUser("%", lastName, "%")).thenReturn(Stream.of(user).collect(Collectors.toList()));
		List<User> users = userRepository.getUser("%", lastName, "%").stream().collect(Collectors.toList());
		User userFromDb = users.get(0);
		assertThat(userFromDb, is(user));
	}

	@Test
	public void canGetUserByUsername() {
		when(userRepository.getUser("%", "%", userName)).thenReturn(Stream.of(user).collect(Collectors.toList()));
		List<User> users = userRepository.getUser("%", "%", userName).stream().collect(Collectors.toList());
		User userFromDb = users.get(0);
		assertThat(userFromDb, is(user));
	}

	@Test
	public void canGetUserInTeam() {
		Team team = new Team("Team1");
		team.setUsers(Stream.of(user).collect(Collectors.toList()));
		when(userRepository.findByTeamId(team.getId())).thenReturn(Stream.of(user).collect(Collectors.toList()));
		List<User> users = userRepository.findByTeamId(team.getId()).stream().collect(Collectors.toList());
		User userFromDb = users.get(0);
		assertThat(userFromDb, is(user));

	}

	@Test
	public void CanCountUsersByTeamId() {
		Team team = new Team("Team1");
		team.setUsers(Stream.of(user).collect(Collectors.toList()));
		when(userRepository.countByTeamId(team.getId())).thenReturn(1L);
		Long size = userRepository.countByTeamId(team.getId());
		assertThat(size, is(1L));
	}

	@Test
	public void canUpdateUserName() throws ServiceException {
		String username = "Robbitrobbi";
		when(userRepository.save(user)).thenReturn(user);
		userService.updateUsername(user, username);
		assertThat(user.getUsername(), is(username));
	}

}