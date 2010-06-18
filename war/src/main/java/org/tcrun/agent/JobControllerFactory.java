/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.agent;

/**
 *
 * @author jcorbett
 */
public class JobControllerFactory
{
	private static JobController s_controller;

	public static JobController getJobController(String p_basepath)
	{
		synchronized(JobControllerFactory.class)
		{
			if(s_controller == null)
				s_controller = new DefaultJobController(p_basepath);
		}

		return s_controller;
	}

	public static synchronized void setJobController(JobController p_controller)
	{
		s_controller = p_controller;
	}
}
