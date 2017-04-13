package se.rejjd.resource;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.UUID;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import se.rejjd.model.Team;
import se.rejjd.model.User;

public class TeamResourceTest {
	private final String key = "auth";
	private final String value = "dummy";
	private final Client client = ClientBuilder.newClient();
	private WebTarget teamResource = client.target("http://127.0.0.1:8080/teams");
	private WebTarget userresource = client.target("http://127.0.0.1:8080/users");
	private Invocation.Builder teamBuilder = teamResource.request().header(key, value);
	private Invocation.Builder userBuilder = userresource.request().header(key, value); 
	private Team team;
	private User user;
	private Team teamFromDb;
	private String teamLocation;

	@Before
	public void setUp() throws Exception {
		String number = UUID.randomUUID().toString();
		team = new Team("team" + number);
		user = new User("username" + number, "firstname", "lastname", "userid" + number);
		teamLocation = teamBuilder.post(Entity.entity(team, MediaType.APPLICATION_JSON)).getLocation().toString();
		teamFromDb = client.target(teamLocation).request()
				.header(key, value).get(Team.class);
	}

	@Test
	public void canAddTeam() {
		assertThat(teamFromDb, not(equalTo(null)));
	}
	
	//solution not done
//	@Test
//	public void canAdduserToTeam(){
//		String userLocation = userBuilder
//				.post(Entity.entity(user, MediaType.APPLICATION_JSON))
//				.getLocation().toString();
//		User userfromDb = client.target(userLocation).request()
//				.header(key,value).get(User.class);
//		TeamUserContainer container = new TeamUserContainer(teamFromDb, userfromDb);
//		
//		String status = teamResource.path("{id}/users/{userId}")
//		.resolveTemplate("id", teamFromDb.getId())
//		.resolveTemplate("userId", userfromDb.getUserId()).request()
//		.header(key, value).put(Entity.entity(container, MediaType.APPLICATION_JSON)).getStatusInfo().getReasonPhrase();
//		User userWithTeamId = client.target(userLocation).request()
//				.header(key,value).get(User.class);
//	}
	
	@Test
	public void canUpdateTeam(){
		teamFromDb.setActiveTeam(false);
		teamResource.path("{id}").resolveTemplate("id", teamFromDb.getId()).request()
				.header(key, value)
				.put(Entity.entity(teamFromDb, MediaType.APPLICATION_JSON)).getStatusInfo().toString();
		Team updatedTeam = client.target(teamLocation).request()
				.header(key, value)
				.get(Team.class);
		assertThat(updatedTeam.isActiveTeam(), is(false));
	}
	@Test
	public void canGetWorkitemByTeamId(){
		
	}
	@Test
	public void canGetAllTeams(){
		Collection<Team> teams = teamResource.request().header(key, value).get(new GenericType<Collection<Team>>(){});
		assertThat(teams, hasItem(teamFromDb));
	}
}
