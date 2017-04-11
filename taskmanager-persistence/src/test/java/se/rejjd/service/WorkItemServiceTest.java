package se.rejjd.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import se.rejjd.AbstractTest;
import se.rejjd.model.Issue;
import se.rejjd.model.Team;
import se.rejjd.model.User;
import se.rejjd.model.WorkItem;
import se.rejjd.model.WorkItem.Status;

public class WorkItemServiceTest extends AbstractTest {

	@Autowired
	public WorkItemService workItemService;
	@Autowired
	public IssueService issueService;
	@Autowired
	public UserService userService;
	@Autowired
	public TeamService teamService;
	@Autowired
	private EmbeddedDatabase database;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void canAddOrUpdateWorkItem() throws ServiceException {
		WorkItem workitem = workItemService.addOrUpdateWorkItem(new WorkItem("broken windows", "replace the windows"));
		WorkItem workItemFromDb = workItemService.getWorkItemById(workitem.getId());

		assertNotNull(workitem);
		assertThat(workitem, is(workItemFromDb));
	}

	@Test
	public void canAddWorkItemToUser() throws ServiceException {
		User user = userService.addOrUpdateUser(new User("username10", "firstname", "lastname", "userId"));
		WorkItem workItem = workItemService.addOrUpdateWorkItem(new WorkItem("title", "description"));

		workItemService.addUserToWorkItem(workItem, user);
		ArrayList<WorkItem> listOfUserWorkItems = (ArrayList<WorkItem>) workItemService.getAllWorkItemsByUser(user);

		assertThat(listOfUserWorkItems, contains(workItem));
	}

	@Test
	public void shouldThrowServiceExceptionWhenGivingUserTooManyWorkItems() throws ServiceException {
		expectedException.expect(ServiceException.class);
		expectedException.expectMessage("user must be active and cannot have more than 5 work items");
		User user = userService.addOrUpdateUser(new User("validUsername", "firstname", "lastname", "userId"));

		for (int i = 0; i < 6; i++) {
			workItemService.addUserToWorkItem(new WorkItem("title" + i, "description"), user);
		}
	}

	@Test
	public void shouldThrowExceptionWhenWorkItemAddedToInactiveUser() throws ServiceException {
		expectedException.expect(ServiceException.class);
		expectedException.expectMessage("user must be active and cannot have more than 5 work items");

		User inactiveUser = new User("usernam8888e", "firstname", "lastname", "userId00");
		inactiveUser.setActiveUser(false);
		userService.addOrUpdateUser(inactiveUser);

		WorkItem workItem = new WorkItem("title", "description");
		workItemService.addUserToWorkItem(workItem, inactiveUser);

	}

	@Test
	public void canUpdateWorkItemStatus() throws ServiceException {
		String statusDone = "DONE";
		String statusArchived = "ARCHIVED";
		WorkItem workitem = workItemService.addOrUpdateWorkItem(new WorkItem("buggy code", "fix the code"));
		WorkItem secondWorkitem = workItemService.addOrUpdateWorkItem(new WorkItem("more buggy code", "fix the code"));

		WorkItem workItemFromDb = workItemService.updateWorkItemStatus(workitem, Status.DONE);
		WorkItem secondWorkItemFromDb = workItemService.updateWorkItemStatus(secondWorkitem, Status.ARCHIVED);

		assertThat(workItemFromDb.getStatus().toString(), is(statusDone));
		assertThat(secondWorkItemFromDb.getStatus().toString(), is(statusArchived));
	}

	@Test
	public void canGetWorkItemsByStatus() throws ServiceException {
		WorkItem workitemOne = workItemService.addOrUpdateWorkItem(new WorkItem("workitem-1", "de"));
		WorkItem workitemTwo = workItemService.addOrUpdateWorkItem(new WorkItem("workitem-2", "desc"));
		workItemService.addOrUpdateWorkItem(new WorkItem("workitem-3", "descrip"));
		workItemService.addOrUpdateWorkItem(new WorkItem("workitem-4", "description"));

		workItemService.updateWorkItemStatus(workitemOne, Status.DONE);
		workItemService.updateWorkItemStatus(workitemTwo, Status.DONE);
		ArrayList<WorkItem> listOfWorkItems = (ArrayList<WorkItem>) workItemService.getWorkItemsByStatus(Status.DONE);

		assertThat(listOfWorkItems.size(), is(2));
		assertThat(listOfWorkItems, contains(workitemOne, workitemTwo));
	}

	@Test
	public void canGetWorkItemByteam() throws ServiceException {
		WorkItem workitemOne = workItemService.addOrUpdateWorkItem(new WorkItem("workitem-1", "de"));
		WorkItem workitemTwo = workItemService.addOrUpdateWorkItem(new WorkItem("workitem-2", "desc"));
		User user1 = new User("username123", "firstname", "lastname", "userId123");
		Team team1 = new Team("Awesome Team");

		userService.addOrUpdateUser(user1);
		teamService.addOrUpdateTeam(team1);
		teamService.addUserToTeam(user1, team1);

		workItemService.addOrUpdateWorkItem(new WorkItem("workitem-3", "descrip"));
		workItemService.addOrUpdateWorkItem(new WorkItem("workitem-4", "description"));

		workItemService.addUserToWorkItem(workitemOne, user1);
		workItemService.addUserToWorkItem(workitemTwo, user1);
		ArrayList<WorkItem> listOfWorkItems = (ArrayList<WorkItem>) workItemService
				.getAllWorkItemsByTeam(user1.getTeam().getId());

		assertThat(listOfWorkItems.size(), is(2));
		assertThat(listOfWorkItems, contains(workitemOne, workitemTwo));
	}

	@Test
	public void canGetAllWorkItemsByUser() throws ServiceException {
		WorkItem workitemOne = workItemService.addOrUpdateWorkItem(new WorkItem("workitem-1", "de"));
		WorkItem workitemTwo = workItemService.addOrUpdateWorkItem(new WorkItem("workitem-2", "desc"));
		User user1 = new User("username123", "firstname", "lastname", "userId123");

		userService.addOrUpdateUser(user1);

		workItemService.addOrUpdateWorkItem(new WorkItem("workitem-3", "descrip"));
		workItemService.addOrUpdateWorkItem(new WorkItem("workitem-4", "description"));

		workItemService.addUserToWorkItem(workitemOne, user1);
		workItemService.addUserToWorkItem(workitemTwo, user1);
		ArrayList<WorkItem> listOfWorkItems = (ArrayList<WorkItem>) workItemService.getAllWorkItemsByUser(user1);

		assertThat(listOfWorkItems.size(), is(2));
		assertThat(listOfWorkItems, contains(workitemOne, workitemTwo));

	}

	@Test
	public void canGetAllWorkItemsByDescription() throws ServiceException {
		String description = "Testing";
		WorkItem workitemOne = workItemService.addOrUpdateWorkItem(new WorkItem("workitem-1", description));
		WorkItem workitemTwo = workItemService.addOrUpdateWorkItem(new WorkItem("workitem-2", description));

		workItemService.addOrUpdateWorkItem(workitemOne);
		workItemService.addOrUpdateWorkItem(workitemTwo);

		ArrayList<WorkItem> listOfWorkItems = (ArrayList<WorkItem>) workItemService
				.getWorkItemByDescripton(description);

		assertThat(listOfWorkItems.size(), is(2));
		assertThat(listOfWorkItems, contains(workitemOne, workitemTwo));
	}
	
	@Test
	public void canGetAllWorkItemsWithAnIssue() throws ServiceException{
		WorkItem workItem1 = new WorkItem("Stuff to do", "Do things");
		WorkItem workItem2 = new WorkItem("Nothing", "Vacation");
		WorkItem workItem3 = new WorkItem("Work work", "No play");
		WorkItem updatedWorkItem1 = workItemService.updateWorkItemStatus(workItem1, Status.DONE);
		WorkItem updatedWorkItem2 = workItemService.updateWorkItemStatus(workItem2, Status.DONE);
		WorkItem updatedWorkItem3 = workItemService.updateWorkItemStatus(workItem3, Status.DONE);
		Issue issue1 = issueService.addIssue(updatedWorkItem1, "Not right");
		Issue issue2 = issueService.addIssue(updatedWorkItem3, "Johnny");
		
		ArrayList<WorkItem> workItemsWithIssues = (ArrayList<WorkItem>) workItemService.getAllWorkItemsWithIssues();
		assertThat(workItemsWithIssues.size(), is(2));
		assertThat(workItemsWithIssues,contains(updatedWorkItem1, updatedWorkItem3));
		
	}
}
