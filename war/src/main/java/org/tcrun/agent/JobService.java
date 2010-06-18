package org.tcrun.agent;

import java.util.UUID;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author jcorbett
 */
@Path("/job")
public class JobService
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
	@Path("{id}/status")
	@Produces("text/plain")
	public String getJobStatus(@PathParam("id") String id)
	{
		UUID jobid = null;
		try
		{
			jobid = UUID.fromString(id);
		} catch(IllegalArgumentException ex)
		{
			throw new WebApplicationException(Status.NOT_ACCEPTABLE);
		}
		JobStatus status = m_control.getJobStatus(jobid);
		if(status == null)
			throw new WebApplicationException(Status.NOT_FOUND);
		return status.toString();
	}

	@GET
	@Path("current")
	@Produces("text/plain")
	public String getCurrentJob()
	{
		UUID retval = m_control.getCurrentJobId();
		if(retval == null)
			return "None";
		else
			return retval.toString();
	}

	@GET
	@Path("{id}/description")
	@Produces("text/plain")
	public String getJobDescription(@PathParam("id") String p_jobId)
	{
		UUID jobid = null;
		try
		{
			jobid = UUID.fromString(p_jobId);
		} catch(IllegalArgumentException ex)
		{
			throw new WebApplicationException(Status.NOT_ACCEPTABLE);
		}

		Job job = m_control.getJob(jobid);
		if(job == null)
			throw new WebApplicationException(Status.NOT_FOUND);
		return job.getDescription();
	}

	@GET
	@Path("{id}/output")
	@Produces("text/plain")
	public String getJobOutput(@PathParam("id") String p_jobId)
	{
		UUID jobid = null;
		try
		{
			jobid = UUID.fromString(p_jobId);
		} catch(IllegalArgumentException ex)
		{
			throw new WebApplicationException(Status.NOT_ACCEPTABLE);
		}

		Job job = m_control.getJob(jobid);
		if(job == null)
			throw new WebApplicationException(Status.NOT_FOUND);
		return job.getOutput();
	}

}
