package se.rejjd.model;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "teams")
public class Team extends AbstractEntity {

	@XmlElement
	@Column(nullable = false, unique = true)
	private String teamName;
	@XmlElement
	@Column(nullable = false)
	private boolean activeTeam;
	@OneToMany(mappedBy = "team")
	private Collection<User> users;

	protected Team() {
	}

	public Team(String teamName) {
		this.teamName = teamName;
		this.activeTeam = true;
	}

	public void setUsers(Collection<User> users) {
		this.users = users;
	}

	public void setActiveTeam(boolean activeTeam) {
		this.activeTeam = activeTeam;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	@XmlTransient
	public Collection<User> getUsers() {
		return users;
	}

	public boolean isActiveTeam() {
		return activeTeam;
	}

	@Override
	public String toString() {
		return "Team id: " + getId() + ", team name: " + teamName + ", active: " + activeTeam;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Team) {
			Team teamObj = (Team) obj;
			return teamObj.getId() == getId();
		}
		return false;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result += 7 * getId();
		return result;
	}
}
