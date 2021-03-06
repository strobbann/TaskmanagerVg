package se.rejjd.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import se.rejjd.model.Team;
import se.rejjd.model.User;

@XmlRootElement
public final class TeamUserContainer {

	@XmlElement
	private Team team;
	@XmlElement
	private User user;
	
	public TeamUserContainer(Team team , User user){
		this.user = user;
		this.team = team;
	}

	public Team getTeam() {
		return team;
	}

	public User getUser() {
		return user;
	}
}
