package se.rejjd.resource;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isNotNull;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.hamcrest.core.IsNot;
import org.junit.Before;
import org.junit.Test;

import se.rejjd.model.User;

public final class UserResourceTest {
	private Client client = ClientBuilder.newClient();
	private final WebTarget userResource = client.target("http://127.0.0.1:8080/users/");
	private User usertodb;

	@Before
	public void setUp() throws Exception {
		String random = UUID.randomUUID().toString();
		usertodb = new User("SuperUserName" + random, "firstname", "lastname", "" + random);
	}

	@Test
	public void CanAddUser() {
		String location = userResource.request()
				.post(Entity.entity(usertodb, MediaType.APPLICATION_JSON)).getHeaderString("Location");
		User userfromdb = client.target(location).request(MediaType.APPLICATION_JSON).get(User.class);
		assertThat(userfromdb, not(equalTo(null)));
	}

	@Test
	public void canUpdateUser() {
		String location = userResource.request()
				.post(Entity.entity(usertodb, MediaType.APPLICATION_JSON))
				.getHeaderString("Location");
		User userfromdb = client.target(location).request(MediaType.APPLICATION_JSON).get(User.class);
		String updatedfirstname = "halabala";
		userfromdb.setFirstName(updatedfirstname);
		client.target(location).request(MediaType.APPLICATION_JSON)
				.put(Entity.entity(userfromdb, MediaType.APPLICATION_JSON));
		User updatedUserfromDb = client.target(location).request(MediaType.APPLICATION_JSON).get(User.class);
		assertThat(updatedUserfromDb.getFirstname(), is(updatedfirstname));

	}

	@Test
	public void canFindUser() {
		String location = userResource.request()
				.post(Entity.entity(usertodb, MediaType.APPLICATION_JSON)).getHeaderString("Location");
		User userfromdb = client.target(location).request(MediaType.APPLICATION_JSON).get(User.class);
		Collection<User> users = userResource.queryParam("firstname", usertodb.getFirstname()).request().get(new GenericType<Collection<User>>(){});
		assertThat(users, hasItem(userfromdb));
	}

}
