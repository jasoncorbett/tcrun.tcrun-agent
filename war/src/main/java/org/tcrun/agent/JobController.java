package org.tcrun.agent;

import java.util.UUID;

/**
 *
 * @author jcorbett
 */
public interface JobController
{
	public UUID add(Job p_job);
	public JobStatus getJobStatus(UUID p_job);
	public String getOutputPath(UUID p_job);
	public UUID getCurrentJobId();
	public Job getJob(UUID p_job);
	public void shutdown();
}
