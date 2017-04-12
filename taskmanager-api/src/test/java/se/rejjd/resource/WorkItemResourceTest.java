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
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.rejjd.model.Issue;
import se.rejjd.model.WorkItem;

public class WorkItemResourceTest {
	private final String key = "auth";
	private final String value = "dummy";
	private Client client = ClientBuilder.newClient();
	private WebTarget workitemResource = client.target("http://127.0.0.1:8080/workitems/");
	private Invocation.Builder workitemBuilder = workitemResource.request().header(key, value);
	private WorkItem workItem;

	@Before
	public void setUpclass() {
		workItem = new WorkItem("title", "description");

	}

	@Test
	public void canAddWorkItem() {
		String workitemLocation = workitemBuilder.post(Entity.entity(workItem, MediaType.APPLICATION_JSON))
				.getLocation().toString();
		WorkItem workItemFromDb = client.target(workitemLocation).request().header(key, value).get(WorkItem.class);
		assertThat(workItemFromDb, not(equalTo(null)));
	}

	@Test
	public void canAddIssueToWorktItem() {
		String workitemLocation = workitemBuilder.post(Entity.entity(workItem, MediaType.APPLICATION_JSON))
				.getLocation().toString();
		WorkItem workitemfromDb = client.target(workitemLocation).request().header(key, value).get(WorkItem.class);
		workitemfromDb.setStatus(WorkItem.Status.DONE);
		workitemResource.path("{id}")
		.resolveTemplate("id", workitemfromDb.getId()).request().header(key, value)
				.put(Entity.entity(workitemfromDb, MediaType.APPLICATION_JSON));
		String issueDescription = "issueDescription";
		int status = workitemResource.path("{id}/issues").resolveTemplate("id", workitemfromDb.getId()).request()
				.header(key, value).post(Entity.entity(issueDescription, MediaType.APPLICATION_JSON)).getStatus();
		assertThat(status, is(Status.CREATED.getStatusCode()));
	}

	@Test
	public void canUpdateWorkitem() {
		String workitemLocation = workitemBuilder.post(Entity.entity(workItem, MediaType.APPLICATION_JSON))
				.getLocation().toString();
		WorkItem workitemfromDb = client.target(workitemLocation).request()
				.header(key, value)
				.get(WorkItem.class);
		workitemfromDb.setStatus(WorkItem.Status.DONE);
		workitemResource.path("{id}")
		.resolveTemplate("id", workitemfromDb.getId()).request()
		.header(key, value)
		.put(Entity.entity(workitemfromDb, MediaType.APPLICATION_JSON)).getStatus();
		
		WorkItem updatedWorkitemfromDb = client.target(workitemLocation).request()
				.header(key, value)
				.get(WorkItem.class);
		assertThat(updatedWorkitemfromDb.getStatus(), is(WorkItem.Status.DONE));
		
	}
	
	//not sure how to do yet
	@Test
	public void canUpdateIssue(){
		String workitemLocation = workitemBuilder.post(Entity.entity(workItem, MediaType.APPLICATION_JSON))
				.getLocation().toString();
		WorkItem workitemfromDb = client.target(workitemLocation).request()
				.header(key, value)
				.get(WorkItem.class);
		workitemfromDb.setStatus(WorkItem.Status.DONE);
		workitemResource.path("{id}")
		.resolveTemplate("id", workitemfromDb.getId()).request()
		.header(key, value)
		.put(Entity.entity(workitemfromDb, MediaType.APPLICATION_JSON)).getStatus();
		String issueDescrition = "wrong way";
//		workitemResource.path("{id}/issues").request()
//		.header(key, value)
//		.post(Entity.entity(issueDescrition, MediaType.APPLICATION_JSON));		
	}
	
	@Test
	public void canGetWorkItemsWithIssues(){
		String workitemLocation = workitemBuilder.post(Entity.entity(workItem, MediaType.APPLICATION_JSON))
				.getLocation().toString();
		WorkItem workitemfromDb = client.target(workitemLocation).request()
				.header(key, value)
				.get(WorkItem.class);
		workitemfromDb.setStatus(WorkItem.Status.DONE);
		workitemResource.path("{id}")
		.resolveTemplate("id", workitemfromDb.getId()).request()
		.header(key, value)
		.put(Entity.entity(workitemfromDb, MediaType.APPLICATION_JSON));
		
		String issueDescrition = UUID.randomUUID().toString();		
		workitemResource.path("{id}/issues").resolveTemplate("id", workitemfromDb.getId()).request()
		.header(key, value)
		.post(Entity.entity(issueDescrition, MediaType.APPLICATION_JSON));
		
		Collection <WorkItem> worktiemsWithissues = workitemResource.path("/issues").request()
				.header(key, value).get(new GenericType<Collection<WorkItem>>(){});
		
		assertThat(worktiemsWithissues, hasItem(workitemfromDb));
	}
	@Test
	public void canGetWorkitemByQuery(){
		String workitemLocation = workitemBuilder.post(Entity.entity(workItem, MediaType.APPLICATION_JSON))
				.getLocation().toString();
		WorkItem workitemfromDb = client.target(workitemLocation).request()
				.header(key, value)
				.get(WorkItem.class);
		Collection<WorkItem> workitems = workitemResource.queryParam("description", workitemfromDb.getDescription())
				.request()
				.header(key, value)
				.get(new GenericType<Collection<WorkItem>>(){});
		assertThat(workitems, hasItem(workitemfromDb));
		
	}


}
