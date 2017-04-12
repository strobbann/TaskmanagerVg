package se.rejjd.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import se.rejjd.resource.RequestFilter;
import se.rejjd.resource.TeamResource;
import se.rejjd.resource.UserResource;
import se.rejjd.resource.WorkItemResource;

@Component
public class JerseyConfig extends ResourceConfig{
	
	public JerseyConfig() {
		register(UserResource.class);
		register(TeamResource.class);
		register(WorkItemResource.class);
		register(RequestFilter.class);
	}

}
