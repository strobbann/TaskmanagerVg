package se.rejjd.model;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "users")
public class User extends AbstractEntity {

    @XmlElement
    @Column(nullable = false, unique = true)
    private String username;
    @XmlElement
    @Column(nullable = false)
    private String firstname;
    @XmlElement
    @Column(nullable = false)
    private String lastname;
    @XmlElement
    @Column(nullable = false, unique = true)
    private String userId;
    @XmlElement
    @Column(nullable = false)
    private boolean activeUser;
    @ManyToOne
    private Team team;
    @OneToMany(mappedBy = "user")
    private Collection<WorkItem> workitems;
    
    protected User() {
    	
    }
    
    public User(String username, String firstname, String lastname, String userId) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.activeUser = true;
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getFirstname() {
        return firstname;
    }
    
    public String getLastname() {
        return lastname;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setFirstName(String firstName) {
        this.firstname = firstName;
    }
    
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    
    public boolean isActiveUser() {
        return activeUser;
    }
    
    @XmlTransient
    public Team getTeam() {
        return team;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setActiveUser(boolean activeUser) {
        this.activeUser = activeUser;
    }
    
    public void setTeam(Team team) {
        this.team = team;
    }
    
    @Override
    public String toString() {
        return "User " + getId() + ", username: " + username + ", firstname: " + firstname + ", lastname: " + lastname
        + ", userId : " + userId + ", active: " + activeUser + ", teamId: " + team;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof User) {
            User userObj = (User) obj;
            return userObj.getId() == getId();
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
