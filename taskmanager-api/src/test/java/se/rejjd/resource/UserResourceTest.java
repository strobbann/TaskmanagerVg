package se.rejjd.resource;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.UUID;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;

import se.rejjd.model.User;
import se.rejjd.model.WorkItem;

public final class UserResourceTest {
	private Client client = ClientBuilder.newClient();
	private final WebTarget userResource = client.target("http://127.0.0.1:8080/users/");
	private final WebTarget workitemresource = client.target("http://127.0.0.1:8080/workitems/");
	private User usertodb;

	@Before
	public void setUp() throws Exception {
		String random = UUID.randomUUID().toString();
		usertodb = new User("SuperUserName" + random, "firstname", "lastname", "" + random);
	}

	@Test
	public void CanAddUser() {
		String userLocation = userResource.request()
				.post(Entity.entity(usertodb, MediaType.APPLICATION_JSON))
				.getLocation().toString();
		User userfromdb = client.target(userLocation).request(MediaType.APPLICATION_JSON).get(User.class);
		assertThat(userfromdb, not(equalTo(null)));
	}

	@Test
	public void canUpdateUser() {
		String userLocation = userResource.request()
				.post(Entity.entity(usertodb, MediaType.APPLICATION_JSON))
				.getLocation().toString();
		User userfromdb = client.target(userLocation).request(MediaType.APPLICATION_JSON).get(User.class);
		String updatedfirstname = "halabala";
		userfromdb.setFirstName(updatedfirstname);
		client.target(userLocation).request(MediaType.APPLICATION_JSON)
				.put(Entity.entity(userfromdb, MediaType.APPLICATION_JSON));
		User updatedUserfromDb = client.target(userLocation).request(MediaType.APPLICATION_JSON).get(User.class);
		assertThat(updatedUserfromDb.getFirstname(), is(updatedfirstname));

	}

	@Test
	public void canFindUser() {
		String userLocation = userResource.request()
				.post(Entity.entity(usertodb, MediaType.APPLICATION_JSON)).getLocation().toString();
		User userfromdb = client.target(userLocation).request(MediaType.APPLICATION_JSON).get(User.class);
		Collection<User> users = userResource.queryParam("firstname", usertodb.getFirstname()).request().get(new GenericType<Collection<User>>(){});
		assertThat(users, hasItem(userfromdb));
	}
	@Test
	public void canAddWorktItemToUser(){
		WorkItem workItem = new WorkItem("title", "description");
		String workitemLocation = workitemresource.request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(workItem,MediaType.APPLICATION_JSON))
				.getLocation().toString();
		WorkItem workitemfromdb = client.target(workitemLocation)
				.request(MediaType.APPLICATION_JSON)
				.get(WorkItem.class);
		String userLocation = userResource.request()
				.post(Entity.entity(usertodb, MediaType.APPLICATION_JSON))
				.getLocation().toString();
		User userfromdb = client.target(userLocation)
				.request(MediaType.APPLICATION_JSON)
				.get(User.class);
		
		Entity<?> empty = Entity.entity("", MediaType.APPLICATION_JSON);
		userResource.path("{userId}/workitems/{id}")
				.resolveTemplate("userId", userfromdb.getUserId())
				.resolveTemplate("id", workitemfromdb.getId())
				.request(MediaType.APPLICATION_JSON)
				.put(empty);
		WorkItem workitemfromdbwithUser = client
				.target(workitemLocation)
				.request(MediaType.APPLICATION_JSON)
				.get(WorkItem.class);
		
		assertThat(workitemfromdbwithUser.getUser(), is(userfromdb));
	}
	@Test
	public void canGetWorkitemByUserId(){
		WorkItem workItem = new WorkItem("title", "description");
		String workitemLocation = workitemresource.request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(workItem,MediaType.APPLICATION_JSON))
				.getHeaderString("Location");
		WorkItem workitemfromdb = client.target(workitemLocation).request(MediaType.APPLICATION_JSON).get(WorkItem.class);
		String userLocation = userResource.request()
				.post(Entity.entity(usertodb, MediaType.APPLICATION_JSON))
				.getLocation().toString();
		User userfromdb = client.target(userLocation)
				.request(MediaType.APPLICATION_JSON)
				.get(User.class);
		Entity<?> empty = Entity.entity("", MediaType.APPLICATION_JSON);
		userResource.path("{userId}/workitems/{id}")
		.resolveTemplate("userId", userfromdb.getUserId())
		.resolveTemplate("id", workitemfromdb.getId())
		.request(MediaType.APPLICATION_JSON)
		.put(empty);
		WorkItem workitemfromdbwithUser = client.target(workitemLocation)
				.request(MediaType.APPLICATION_JSON)
				.get(WorkItem.class);
		Collection<WorkItem> workitems = userResource.path("{userId}/workitems")
				.resolveTemplate("userId", userfromdb.getUserId())
				.request().get(new GenericType<Collection<WorkItem>>(){});
		assertThat(workitems, hasItem(workitemfromdbwithUser));
		
	}

}
