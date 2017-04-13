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

import se.rejjd.model.User;
import se.rejjd.model.WorkItem;
import se.rejjd.service.ServiceException;
import se.rejjd.service.UserService;
import se.rejjd.service.WorkItemService;

@Component
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public final class UserResource {

	private final UserService userService;
	private final WorkItemService workItemService;
	@Context
	private UriInfo uriInfo;

	public UserResource(UserService userService, WorkItemService workItemService) {
		this.userService = userService;
		this.workItemService = workItemService;
	}

	@POST
	public Response addUser(User user) throws ServiceException {
//		int userId = Integer.parseInt(user.getUserId());
		User fromDb = userService.getUserByUserId(user.getUserId());
		if (fromDb != null) {
			throw new ResourceException("user already exist with userid", Status.FOUND);
		}
		user = new User(user.getUsername(), user.getFirstname(), user.getLastname(), user.getUserId());
		userService.addOrUpdateUser(user);
		URI location = uriInfo.getAbsolutePathBuilder().path(user.getUserId()).build();
		return Response.created(location).build();
	}

	@GET
	@Path("{id}")
	public Response getUserById(@PathParam("id") String id) {
		User user = userService.getUserByUserId(id);

		if (user == null) {
			throw new ResourceException("no user with id",Status.NO_CONTENT);
		}
		return Response.ok(user).build();

	}

	@GET
	public Response getUserByName(@BeanParam UserQueryNameParam param) {
		Collection<User> users = userService.getUsers(param.getFirstname(), param.getLastname(), param.getUsername());
		if (users.isEmpty()) {
			throw new ResourceException("no user with name",Status.NO_CONTENT);
		}
		return Response.ok(users).build();
	}

	@GET
	@Path("{userId}/workitems")
	public Response getWorkItemsByUser(@PathParam("userId") String userId) {
		User user = userService.getUserByUserId(userId);
		Collection<WorkItem> workItems = workItemService.getAllWorkItemsByUser(user);
		if (workItems.isEmpty()) {
			throw new ResourceException("user has no workitems",Status.NO_CONTENT);
		}
		return Response.ok(workItems).build();
	}

	@PUT
	@Path("{userId}")
	public Response updateUser(@PathParam("userId") String userId, User user) throws ServiceException {
		if (!userId.equals(user.getUserId())) {
			throw new ResourceException("not the same userid as in db",Status.BAD_REQUEST);
		}
		User userfromDb = userService.getUserByUserId(userId);
		if (userfromDb == null) {
			throw new ResourceException("no user with id",Status.NO_CONTENT);
		}
		userService.addOrUpdateUser(user);
		return Response.ok().build();
	}

	@PUT
	@Path("{userId}/workitems/{id}")
	public Response addUserToWorkItem(@PathParam("userId") String userId, @PathParam("id") Long id)
			throws ServiceException {
		WorkItem workItem = workItemService.getWorkItemById(id);
		User user = userService.getUserByUserId(userId);
		workItemService.addUserToWorkItem(workItem, user);
		return Response.ok().build();
	}
}
