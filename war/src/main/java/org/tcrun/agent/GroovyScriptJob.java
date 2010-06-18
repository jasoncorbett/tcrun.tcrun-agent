/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tcrun.agent;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.io.FilenameUtils;
import org.codehaus.groovy.control.CompilationFailedException;

/**
 *
 * @author jcorbett
 */
public class GroovyScriptJob implements Job
{
	private String m_scriptpath;
	private MultivaluedMap<String, String> m_parameters;
	private UUID m_id;
	private String m_outputpath;
	private OutputRedirect m_outputredirect;

	public GroovyScriptJob(String p_scriptpath, MultivaluedMap<String, String> p_parameters)
	{
		m_scriptpath = p_scriptpath;
		m_parameters = p_parameters;
		m_outputredirect = new OutputRedirect();
	}

	public void initialize(UUID p_id, String p_outputpath)
	{
		m_id = p_id;
		m_outputpath = p_outputpath;
	}

	public void run()
	{
		m_outputredirect.start();
		Binding vars = new Binding();
		vars.setProperty("parameters", m_parameters);
		vars.setProperty("output", m_outputpath);
		GroovyShell shell = new GroovyShell(vars);
		try
		{
			shell.evaluate(new File(m_scriptpath));
		} catch(CompilationFailedException ex)
		{
			System.err.println("ERROR: Bad Script '" + m_scriptpath + "': " + ex.getMessage());
			ex.printStackTrace();
		} catch(IOException ex)
		{
			System.err.println("ERROR: IO Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
		m_outputredirect.stop();
	}

	public String getDescription()
	{
		StringBuilder retval = new StringBuilder();
		retval.append("Groovy Script ");
		retval.append(FilenameUtils.getBaseName(m_scriptpath));
		retval.append("\r\n");
		retval.append("Parameters:");
		retval.append("\r\n");
		for(String key : m_parameters.keySet())
		{
			for(String value : m_parameters.get(key))
			{
				retval.append(key);
				retval.append(" = ");
				retval.append(value);
				retval.append("\r\n");
			}
		}
		retval.append("\r\n");
		retval.append("Output:\r\n");
		retval.append("-------\r\n");
		retval.append(m_outputredirect.getOutput());

		return retval.toString();
	}

	public String getOutput()
	{
		return m_outputredirect.getOutput();
	}

}
