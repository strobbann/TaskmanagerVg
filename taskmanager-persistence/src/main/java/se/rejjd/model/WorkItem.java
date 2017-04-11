package se.rejjd.model;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "workitems")
public class WorkItem extends AbstractEntity {

	@XmlElement
	@Column(nullable = false)
	private String title;
	@XmlElement
	@Column(nullable = false)
	private String description;
	@XmlElement
	@Enumerated(EnumType.STRING)
	private Status status;
	@XmlElement
	@ManyToOne
	private User user;
	@XmlElement
	private String dateOfCompletion;
	@OneToMany(mappedBy = "workItem")
	private Collection<Issue> issues;

	protected WorkItem() {
	}

	public WorkItem(String title, String description) {
		this.title = title;
		this.description = description;
		this.status = Status.UNSTARTED;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Status getStatus() {
		return status;
	}

	public User getUser() {
		return user;
	}

	public String getDateOfCompletion() {
		return dateOfCompletion;
	}
	@XmlTransient
	public Collection<Issue> getIssues() {
		return issues;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setIssues(Collection<Issue> issues) {
		this.issues = issues;
	}

	public void setDateOfCompletion(String dateOfCompletion) {
		this.dateOfCompletion = dateOfCompletion;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public enum Status {
		DONE, UNSTARTED, STARTED, ARCHIVED
	}

	@Override
	public String toString() {
		return "Workitem " + getId() + ", title: " + title + ", description: " + description + ", status: " + status
				+ ", assignedUserId: " + user + ", dateOfCompletion: " + dateOfCompletion;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof WorkItem) {
			WorkItem WorkItemObj = (WorkItem) obj;
			return WorkItemObj.getId() == getId();
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
