package se.rejjd.service;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import se.rejjd.AbstractTest;
import se.rejjd.model.Team;
import se.rejjd.model.User;

public class TeamServiceTest extends AbstractTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	@Autowired
	TeamService teamService;
	@Autowired
	UserService userService;

	User user = new User("Robertasdasdasd", "roberts", "Hello", "world");
	Team team = new Team("Team");
	Team teamDB;

	@Before
	public void setup() {
		teamDB = null;
	}

	@Test
	public void canCreateTeam() throws ServiceException {
		teamService.addOrUpdateTeam(team);
		teamDB = teamService.getTeamById(team.getId());
		assertThat(team, is(teamDB));
	}

	@Test
	public void canAddUserToTeam() throws ServiceException {
		team = teamService.addOrUpdateTeam(team);
		user = userService.addOrUpdateUser(user);
		teamService.addUserToTeam(user, team);

		teamDB = teamService.getTeamById(team.getId());

		Collection<User> users = userService.getUsersByTeamId(team.getId());
		boolean exist = users.contains(user);
		assertTrue(exist);
	}

	@Test
	public void canGetAllTeam() throws ServiceException {
		Team team1 = new Team("team1");
		Team team2 = new Team("team2");

		teamService.addOrUpdateTeam(team1);
		teamService.addOrUpdateTeam(team2);

		Collection<Team> teams = teamService.getAllTeams();

		assertThat(teams, hasItems(team1, team2));
	}

	@Test
	public void shouldThrowExceptionWhenTeamIsFull() throws ServiceException {
		expectedException.expect(ServiceException.class);
		expectedException.expectMessage("Team is full!");

		teamService.addOrUpdateTeam(team);
		for (int i = 1; i < 12; i++) {
			User user = new User("username000" + i, "firstname", "lastname", "000" + i);
			userService.addOrUpdateUser(user);
			teamService.addUserToTeam(user, team);
		}
	}

	@Test
	public void canUpdateTeam() throws ServiceException {
		String newTeamName = "Minecraft";
		team.setTeamName(newTeamName);
		teamDB = teamService.addOrUpdateTeam(team);

		assertThat(team.getTeamName(), is(teamDB.getTeamName()));
	}
	
	public void canUpdateTeamStatus() throws ServiceException{
		team = teamService.addOrUpdateTeam(team);
		Team updatedTeam = teamService.updateTeamStatus(team, false);
		teamDB = teamService.getTeamById(updatedTeam.getId());
		assertThat(teamDB.isActiveTeam(), is(false));
		
	}

}
