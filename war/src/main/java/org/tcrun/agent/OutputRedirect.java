/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tcrun.agent;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 *
 * @author jcorbett
 */
public class OutputRedirect
{
	private ByteArrayOutputStream output;
	private PrintStream saved_out;
	private PrintStream saved_err;

	public OutputRedirect()
	{
		output = new ByteArrayOutputStream();
		saved_out = null;
		saved_err = null;
	}

	public void start()
	{
		saved_out = System.out;
		saved_err = System.err;
		System.setOut(new PrintStream(output));
		System.setErr(new PrintStream(output));
	}

	public void stop()
	{
		if(saved_out != null)
		{
			System.setOut(saved_out);
			System.setErr(saved_err);
			saved_out = null;
			saved_err = null;
		}
	}

	public String getOutput()
	{
		return output.toString();
	}
}
