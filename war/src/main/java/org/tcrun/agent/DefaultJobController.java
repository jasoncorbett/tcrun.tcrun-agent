package org.tcrun.agent;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author jcorbett
 */
public class DefaultJobController implements JobController
{
	private ConcurrentHashMap<UUID, Job> m_jobs;
	private String m_outputpath;
	private UUID m_current;
	private ConcurrentLinkedQueue<UUID> m_queue;
	private WorkerThread m_executor;

	public DefaultJobController(String p_basepath)
	{
		m_jobs = new ConcurrentHashMap<UUID, Job>();
		m_queue = new ConcurrentLinkedQueue<UUID>();
		m_current = null;
		m_outputpath = FilenameUtils.concat(p_basepath, "output");
		try
		{
			FileUtils.forceMkdir(new File(m_outputpath));
		} catch(IOException ex)
		{
			//TODO: logging the exception...
		}
		m_executor = new WorkerThread();
		m_executor.start();
	}

	public synchronized UUID getCurrentJobId()
	{
		return m_current;
	}

	public synchronized void setCurrentJobId(UUID p_jobid)
	{
		m_current = p_jobid;
	}

	public UUID add(Job p_job)
	{
		UUID job_uuid = UUID.randomUUID();
		String output_path = getOutputPath(job_uuid);
		try
		{
			FileUtils.forceMkdir(new File(output_path));
		} catch(IOException ex)
		{
			//TODO: logging the exception...
		}
		p_job.initialize(job_uuid, output_path);
		m_jobs.put(job_uuid, p_job);
		m_queue.add(job_uuid);
		return job_uuid;
	}

	public JobStatus getJobStatus(UUID p_jobId)
	{
		// fetch the currentJob
		UUID currentJob = getCurrentJobId();
		if(!m_jobs.containsKey(p_jobId))
			return null;
		if(m_queue.contains(p_jobId))
			return JobStatus.QUEUED;
		else if(currentJob != null && currentJob.compareTo(p_jobId) == 0)
			return JobStatus.RUNNING;
		else
			return JobStatus.DONE;
	}

	public Job getJob(UUID p_jobId)
	{
		if(!m_jobs.containsKey(p_jobId))
			return null;
		return m_jobs.get(p_jobId);
	}

	public String getOutputPath(UUID p_job)
	{
		return FilenameUtils.concat(m_outputpath, p_job.toString());
	}

	public void setBaseOutputPath(String p_path)
	{
		m_outputpath = p_path;
	}

	public void shutdown()
	{

		// clean up temporary directory
	}

	public class WorkerThread extends Thread
	{
		public WorkerThread()
		{
			this.setDaemon(true);
		}

		@Override
		public void run()
		{
			while(true)
			{
				UUID jobid = m_queue.poll();
				if(jobid == null)
				{
					try
					{
						Thread.sleep(250);
					} catch(InterruptedException ex)
					{
					}
					continue;
				}
				setCurrentJobId(jobid);
				Job job = m_jobs.get(jobid);
				job.run();
				setCurrentJobId(null);
			}
		}
	}
}
