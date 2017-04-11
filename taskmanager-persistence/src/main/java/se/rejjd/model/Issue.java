package se.rejjd.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

@Entity
@Table(name = "issues")
public class Issue extends AbstractEntity {

	@XmlElement
	@Column(nullable = false)
	private String description;
	@XmlElement
	@Column(nullable = false)
	private boolean openIssue;
	@XmlElement
	@ManyToOne
	private WorkItem workItem;

	protected Issue() {
	}

	public Issue(WorkItem workitem, String description) {
		this.description = description;
		this.openIssue = true;
		this.workItem = workitem;
	}

	public String getDescription() {
		return description;
	}

	public boolean isOpenIssue() {
		return openIssue;
	}

	public WorkItem getWorkitem() {
		return workItem;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setOpenIssue(boolean openIssue) {
		this.openIssue = openIssue;
	}

	@Override
	public String toString() {
		return "Issue " + getId() + ", description: " + description + ", is open: " + openIssue + ", workitem id: "
				+ workItem;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Issue) {
			Issue issueObj = (Issue) obj;
			return issueObj.getId() == getId();
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
