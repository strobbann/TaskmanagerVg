package se.rejjd.resource;

import javax.ws.rs.ext.Provider;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import se.rejjd.model.ErrorMessage;

@Provider
public class NumberFormatExceptionMapper implements ExceptionMapper<NumberFormatException>{

	@Override
	public Response toResponse(NumberFormatException exception) {
		ErrorMessage message = new ErrorMessage("only numbers allowed");
		return Response.status(Status.BAD_REQUEST).entity(message).build();
	}
}
