package se.rejjd.resource;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import se.rejjd.model.ErrorMessage;

public final class RequestFilter implements ContainerRequestFilter{

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String header = requestContext.getHeaderString("auth");
		if(!"dummy".equals(header)){
			ErrorMessage message = new ErrorMessage("Invalid Token Value: " + header);
			requestContext.abortWith(Response.status(Status.UNAUTHORIZED).entity(message).build());
			
		}
			
	}


}
