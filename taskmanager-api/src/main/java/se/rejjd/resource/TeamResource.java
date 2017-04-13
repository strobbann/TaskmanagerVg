package se.rejjd.resource;

import java.net.URI;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.stereotype.Component;

import se.rejjd.data.TeamUserContainer;
import se.rejjd.model.ErrorMessage;
import se.rejjd.model.Team;
import se.rejjd.model.User;
import se.rejjd.model.WorkItem;
import se.rejjd.service.ServiceException;
import se.rejjd.service.TeamService;
import se.rejjd.service.UserService;
import se.rejjd.service.WorkItemService;

@Component
@Path("/teams")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public final class TeamResource {

	private final TeamService teamService;
	private final UserService userService;
	private final WorkItemService workItemService;

	@Context
	private UriInfo uriInfo;

	public TeamResource(TeamService teamService, UserService userService, WorkItemService workItemService) {
		this.teamService = teamService;
		this.userService = userService;
		this.workItemService = workItemService;
	}

	//commented away until solution
	@POST
	public Response addTeam(Team team) throws ServiceException {
//		Team newTeam = teamService.getTeamById(team.getId());
//		if (newTeam == null) {
			team = new Team(team.getTeamName());
			teamService.addOrUpdateTeam(team);
			URI location = uriInfo.getAbsolutePathBuilder().path(team.getId().toString()).build();
			return Response.created(location).build();

//		}
//		return Response.status(Status.BAD_REQUEST).build();
	}

	@GET
	public Response getAllTeams() {
		Collection<Team> teams = teamService.getAllTeams();
		if (teams.isEmpty()) {
			throw new ResourceException("No Teams", Status.NO_CONTENT);
		}
		return Response.ok(teams).build();
	}

	@GET
	@Path("{id}")
	public Response getTeamById(@PathParam("id") Long id) {
		Team team = teamService.getTeamById(id);
		if (team == null) {
			throw new  ResourceException("Could not find team with id", Status.NO_CONTENT);
		}
		return Response.ok(teamService.getTeamById(id)).build();
	}

	@GET
	@Path("{id}/users")
	public Response getUsersFromTeam(@PathParam("id") Long id) {
		Collection<User> users = userService.getUsersByTeamId(id);
		if (users.isEmpty()) {
			return Response.noContent().build();
		}
		return Response.ok(users).build();
	}

	@GET
	@Path("{id}/workitems")
	public Response getWorkItemsFromTeam(@PathParam("id") Long id) {
		Collection<WorkItem> workItems = workItemService.getAllWorkItemsByTeam(id);
		if (workItems.isEmpty()) {
			throw new ResourceException("No Workitems Connected to Team", Status.NO_CONTENT);
		}
		return Response.ok(workItems).build();
	}

	@PUT
	@Path("{id}")
	public Response updateTeam(Team team, @PathParam("id") Long id) throws ServiceException {
		if (team.getId() != id) {
			throw new ResourceException("Conflicting ids", Status.BAD_REQUEST);
		}
		Team teamFromDb = teamService.getTeamById(team.getId());
		if (teamFromDb != null) {
			teamService.addOrUpdateTeam(team);
			return Response.ok().build();
		}
		return Response.status(Status.NOT_FOUND).build();
	}

	@PUT
	@Path("{id}/users/{userId}")
	public Response addUserToTeam(@PathParam("id") Long id, @PathParam("userId") String userId,
			TeamUserContainer container) throws WebApplicationException {

		Team team = container.getTeam();
		User user = container.getUser();

		if (team.getId() != id && user.getUserId() != userId) {
			throw new ResourceException("Conflicting ids", Status.BAD_REQUEST);
		}
		try {
			teamService.addUserToTeam(user, team);
		} catch (ServiceException e) {
			throw new ResourceException(e.getMessage(), Status.PRECONDITION_FAILED);
		}
		return Response.ok().build();
	}

}
