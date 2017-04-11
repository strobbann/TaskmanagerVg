package se.rejjd.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.rejjd.model.Team;
import se.rejjd.model.User;
import se.rejjd.repository.TeamRepository;
import se.rejjd.repository.UserRepository;

@Component
public final class TeamService {

	private final TeamRepository teamRepository;
	private final UserRepository userRepository;
	private final UserService userService;
	private final ServiceTransaction transaction;

	@Autowired
	public TeamService(TeamRepository teamRepository, UserRepository userRepository, UserService userService,
			ServiceTransaction transaction) {
		this.teamRepository = teamRepository;
		this.userRepository = userRepository;
		this.userService = userService;
		this.transaction = transaction;

	}

	public Team addOrUpdateTeam(Team team) throws ServiceException {
		return transaction.executeAction(() -> {
			return teamRepository.save(team);
		});
	}

	public Team updateTeamStatus(Team team, boolean status) throws ServiceException {
		team.setActiveTeam(status);
		return addOrUpdateTeam(team);
	}

	public Collection<Team> getAllTeams() {
		return teamRepository.findAll();
	}

	public void addUserToTeam(User user, Team team) throws ServiceException {
		teamExists(team);
		userService.userExists(user);
		if (isValidTeamSize(team)) {
			user = userService.getUserByUserId(user.getUserId());
			team = teamRepository.findOne(team.getId());
			user.setTeam(team);
			userService.addOrUpdateUser(user);
			teamRepository.save(team);

		} else {
			throw new ServiceException("Team is full!");
		}
	}

	public Team getTeamById(Long id) {
		return teamRepository.findOne(id);
	}

	public void teamExists(Team team) throws ServiceException {
		if (teamRepository.findOne(team.getId()) == null) {
			throw new ServiceException("Team not found");
		}
	}

	private boolean isValidTeamSize(Team team) {
		return userRepository.countByTeamId(team.getId()) < 10;
	}
}
