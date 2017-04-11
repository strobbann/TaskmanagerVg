package se.rejjd.resource;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import se.rejjd.model.WorkItem.Status;

public class WorkItemQueryParam {
	@QueryParam("status") private Status status;
	@DefaultValue("%")@QueryParam("description") private String description;
	
	public String getDescription() {
		return description;
	}
	public Status getStatus() {
		return status;
	}

}
