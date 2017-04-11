package se.rejjd.resource;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public final class UserQueryNameParam {
	
	@DefaultValue("%") @QueryParam("firstname") private String firstname;
	@DefaultValue("%") @QueryParam("lastname") private String lastname;
	@DefaultValue("%") @QueryParam("username") private String username;
	
	public String getFirstname() {
		return firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public String getUsername() {
		return username;
	}
	
	

}
