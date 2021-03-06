package se.rejjd.resource;

import java.net.URI;
import java.util.Collection;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.stereotype.Component;

import se.rejjd.model.Issue;
import se.rejjd.model.WorkItem;
import se.rejjd.service.IssueService;
import se.rejjd.service.ServiceException;
import se.rejjd.service.WorkItemService;

@Component
@Path("/workitems")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public final class WorkItemResource {

	@Context
	private UriInfo uriInfo;

	private final WorkItemService workItemService;
	private final IssueService issueService;

	public WorkItemResource(WorkItemService workItemService, IssueService issueService) {
		this.workItemService = workItemService;
		this.issueService = issueService;
	}

	@POST
	public Response addWorkItem(WorkItem workItem) throws ServiceException {
//commented away until solution for client
//		WorkItem workitemFromDb = workItemService.getWorkItemById(workItem.getId());
//		if (workitemFromDb != null) {
//			return Response.status(Status.FOUND).build();
//		}
		workItem = new WorkItem(workItem.getTitle(), workItem.getDescription());
		workItemService.addOrUpdateWorkItem(workItem);
		URI location = uriInfo.getAbsolutePathBuilder().path(workItem.getId().toString()).build();
		return Response.created(location).build();
	}

	@POST
	@Path("{id}/issues")
	public Response addIssueToWorkItem(@PathParam("id") Long id, String issueDescription) {
		WorkItem workItem = workItemService.getWorkItemById(id);
		Issue issue;
		if (workItem == null) {
			throw new ResourceException("Workitem Doesnt Exist", Status.BAD_REQUEST);
		}
		try {
			issue = issueService.addIssue(workItem, issueDescription);
		} catch (ServiceException e) {
			throw new ResourceException(e.getMessage(), Status.PRECONDITION_FAILED);
		}
		URI location = uriInfo.getAbsolutePathBuilder().path(issue.getId().toString()).build();
		return Response.created(location).build();
	}

	@GET
	@Path("{id}")
	public Response getWorkItem(@PathParam("id") Long id) {
		WorkItem workitem = workItemService.getWorkItemById(id);
		if (workitem == null) {
			throw new ResourceException("Workitem Doesnt Exist", Status.BAD_REQUEST);
		}
		return Response.ok(workitem).build();
	}

	@GET
	public Response getWorkItemByStatusOrDescription(@BeanParam WorkItemQueryParam param) {
		Collection<WorkItem> workitems = null;
		if (param.getStatus() != null) {
			workitems = workItemService.getWorkItemsByStatus(param.getStatus());
			if (workitems.isEmpty()) {
				throw new ResourceException("No workitems with status", Status.NO_CONTENT);
			}
			return Response.ok(workitems).build();
		} else {
			workitems = workItemService.getWorkItemByDescripton(param.getDescription());
			if (workitems.isEmpty()) {
				throw new ResourceException("No workitems with description", Status.NO_CONTENT);
			}
			return Response.ok(workitems).build();
		}
	}

	@GET
	@Path("/issues")
	public Response getWorkItemsWithIssues() {
		Collection<WorkItem> workItems = workItemService.getAllWorkItemsWithIssues();
		if (workItems.isEmpty()) {
			return Response.noContent().build();
		}
		return Response.ok(workItems).build();
	}

	@PUT
	@Path("{id}")
	public Response updateWorkItem(@PathParam("id") Long id, WorkItem workItem) throws ServiceException {
		if (workItem.getId() != id) {
			throw new ResourceException("conflicting ids",Status.BAD_REQUEST);
		}
		try {
			WorkItem workitemFromDb = workItemService.getWorkItemById(workItem.getId());
			workItemService.updateWorkItemStatus(workitemFromDb, workItem.getStatus());
		} catch (ServiceException e) {
			throw new ResourceException(e.getMessage(), Status.BAD_REQUEST);
		}

		return Response.ok().build();
	}

	@PUT
	@Path("{id}/issues/{issueId}")
	public Response updateIssue(@PathParam("id") Long id, @PathParam("issueId") Long issueId, Issue issue) {
		WorkItem workItem = workItemService.getWorkItemById(id);
		Issue issueFromDb = issueService.findIssueById(issue.getId());
		if (workItem == null || issueFromDb == null) {
			throw new ResourceException("workitem or issue doesnt exist", Status.NO_CONTENT);
		}
		try {
			issueService.updateIssue(issue);
		} catch (ServiceException e) {
			throw new ResourceException(e.getMessage(),Status.BAD_REQUEST);
		}
		return Response.ok().build();
	}
}
