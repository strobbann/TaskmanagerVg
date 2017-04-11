package se.rejjd.service;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertFalse;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import se.rejjd.AbstractTest;
import se.rejjd.model.User;
import se.rejjd.model.WorkItem;
import se.rejjd.model.WorkItem.Status;
import se.rejjd.repository.UserRepository;

public class UserServiceTest extends AbstractTest {

	private User user;
	private Collection<User> users;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Autowired
	UserService userService;
	@Autowired
	WorkItemService workItemService;
	
	@Before
	public void setup() {
		user = new User("username123","firstname","lastname","userId");
		users = new ArrayList<User>();
	}
	
	@Test
	public void shouldThrowExceptionWhenUsernameTooShort() throws ServiceException {
		expectedException.expect(ServiceException.class);
		expectedException.expectMessage("Username is too short!");
		User failedUser = new User("username", "firstname", "lastname", "userId");
		userService.addOrUpdateUser(failedUser);
	}
	
	@Test
	public void canAddUser() throws ServiceException {
		User newUser = userService.addOrUpdateUser(user);
		User userfromDB = userService.getUserByUserId(user.getUserId());
		
		assertThat(newUser, is(userfromDB));
	}
	
	@Test
	public void canUpdateUser() throws ServiceException {
		String updatedUsername = "rolfmed10bokstäver";
		user.setUsername(updatedUsername);
		User updatedUser = userService.addOrUpdateUser(user);
		
		assertThat(updatedUser.getUsername(), is(updatedUsername));
	}
	
	@Test
	public void shouldChangeWorkItemStatusWhenUserIsInactivated() throws ServiceException {
		User userintest =  userService.addOrUpdateUser(user);
		WorkItem workItem = workItemService.addOrUpdateWorkItem(new WorkItem("title","description"));
		workItemService.addUserToWorkItem(workItem, userintest);
		WorkItem workItemstatus = workItemService.updateWorkItemStatus(workItem, Status.STARTED);
		assertThat(workItemstatus.getStatus(), is(Status.STARTED));
		userintest.setActiveUser(false);
		userService.addOrUpdateUser(userintest);
		WorkItem workItemfromDb = workItemService.getWorkItemById(workItemstatus.getId());
		assertThat(workItemfromDb.getStatus() , is(Status.UNSTARTED));
		assertThat(userintest.isActiveUser(), is(false));
	}
	
	@Test
	public void canUpdateUserStatus() throws ServiceException {
		user.setActiveUser(false);
		User updatedUser = userService.addOrUpdateUser(user);
		User userfromdb = userService.getUserByUserId(updatedUser.getUserId());
		assertFalse(userfromdb.isActiveUser());
	}
	
	@Test
	public void canGetUsers() throws ServiceException {
		userService.addOrUpdateUser(user);
		users = userService.getUsers("firstname", "lastname", "username123");
		assertThat(users, hasItems(user));
	}
	
	@Test
	public void canGetUserByFirstName() throws ServiceException {
		userService.addOrUpdateUser(user);
		users = userService.getUserByFirstname("firstname");
		assertThat(users, hasItems(user));
	}
	
	@Test
	public void canGetUserByLastName() throws ServiceException {
		userService.addOrUpdateUser(user);
		users = userService.getUserByLastname("lastname");
		assertThat(users, hasItems(user));
	}
	
	@Test
	public void canGetUserByUsername() throws ServiceException {
		userService.addOrUpdateUser(user);
		users = userService.getUserByUsername("username123");
		assertThat(users, hasItems(user));
	}
	
	@Test
	public void canGetAllUsers() throws ServiceException {
		userService.addOrUpdateUser(user);
		users = userService.getAllUsers();
		assertThat(users, hasItems(user));
	}
}
