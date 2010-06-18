package org.tcrun.agent;

import java.io.File;
import java.util.UUID;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author jcorbett
 */
@Path("/run")
public class RunService
{
	private String m_basepath;
	private JobController m_control;

	@Context public void setServletContext(ServletContext context)
	{
		String jarfile = context.getInitParameter("base-jar");
		m_basepath = FilenameUtils.getFullPath(jarfile);
		m_control = JobControllerFactory.getJobController(m_basepath);
	}

	@GET
	@Path("/groovy/{scriptname}")
	@Produces("text/plain")
	public String runGroovyScript(@PathParam("scriptname") String scriptname, @Context UriInfo uriInfo)
	{
		String scriptpath = FilenameUtils.concat(m_basepath, scriptname + ".groovy");
		File script = new File(scriptpath);
		if(!script.exists())
			throw new WebApplicationException(Status.NOT_FOUND);
		UUID id = m_control.add(new GroovyScriptJob(scriptpath, uriInfo.getQueryParameters()));
		return id.toString();
	}
}
