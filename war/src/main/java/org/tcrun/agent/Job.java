/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.agent;

import java.util.UUID;

/**
 *
 * @author jcorbett
 */
public interface Job extends Runnable
{
	public void initialize(UUID p_id, String p_outputpath);
	public String getDescription();
	public String getOutput();
}
