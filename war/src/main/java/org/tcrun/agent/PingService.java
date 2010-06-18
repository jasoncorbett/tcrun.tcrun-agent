package org.tcrun.agent;

import java.util.List;
import java.util.Map.Entry;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author jcorbett
 */
@Path("/ping")
public class PingService
{
	@GET
	@Produces("text/plain")
	public String pingPong()
	{
		return "pong";
	}

	@GET
	@Produces("text/plain")
	@Path("/sayhello")
	public String sayHello(@QueryParam("to") String toWho)
	{
		return "Hello " + toWho;
	}

	@GET
	@Produces("text/plain")
	@Path("/echo")
	public String echo(@Context UriInfo uriInfo)
	{
		StringBuilder response = new StringBuilder();
		MultivaluedMap<String, String> parameters = uriInfo.getQueryParameters();
		for(Entry<String, List<String>> entry : parameters.entrySet())
		{
			for(String value : entry.getValue())
			{
				response.append(entry.getKey());
				response.append(" = ");
				response.append(value);
				response.append("\r\n");
			}
		}
		return response.toString();
	}
}
