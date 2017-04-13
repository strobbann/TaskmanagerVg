package se.rejjd.resource;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

public class ResourceException extends WebApplicationException {

	private static final long serialVersionUID = -8166397135423326377L;

	public ResourceException(String message, Status status) {
		super(message,status);
	}
}
