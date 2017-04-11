package se.rejjd.service;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import se.rejjd.AbstractTest;
import se.rejjd.model.Issue;
import se.rejjd.model.WorkItem;
import se.rejjd.model.WorkItem.Status;

public class IssueServiceTest extends AbstractTest {
	@Autowired
	private IssueService issueService;
	@Autowired
	private WorkItemService workItemService;
	
	private WorkItem workItem;
	
	@Before
	public void setup(){
		workItem = new WorkItem("title", "description");
	}
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void canAddIssue() throws ServiceException{
		WorkItem updatedWorkItem = workItemService.updateWorkItemStatus(workItem, Status.DONE); 
		Issue issue = issueService.addIssue(updatedWorkItem, "description");
		Collection <Issue> issues = issueService.getAllIssues();
		assertThat(issues, hasItems(issue));
	}
	
	@Test
	public void canUpdateIssueDescription() throws ServiceException{
		String updatedDescription = "updated";
		WorkItem updateWorkitem = workItemService.updateWorkItemStatus(workItem, Status.DONE);
		Issue issue = issueService.addIssue(updateWorkitem, "description");
		issue.setDescription(updatedDescription);
		issueService.updateIssue(issue);
		Issue issueFromDb = issueService.findIssueById(issue.getId());
		assertThat(issueFromDb.getDescription(), is(updatedDescription));
		
	}
	
	@Test
	public void canUpdateIssueStatus() throws ServiceException{
		WorkItem updatedWorkItem = workItemService.updateWorkItemStatus(workItem, Status.DONE);
		Issue issue = issueService.addIssue(updatedWorkItem, "description");
		issue.setOpenIssue(false);
		issueService.updateIssue(issue);
		Issue issueFromDb = issueService.findIssueById(issue.getId());
		assertThat(issueFromDb.isOpenIssue(), is(false));
	}
	@Test
	public void shouldThrowExceptionWhenAddIssueToInvalidWorkitemStatus() throws ServiceException{
		expectedException.expect(ServiceException.class);
		expectedException.expectMessage("Invalid work item status");
		Issue failedIssue = issueService.addIssue(workItem, "description");
	}
	
}
