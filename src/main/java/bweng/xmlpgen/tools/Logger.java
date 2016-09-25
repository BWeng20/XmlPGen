/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.tools;

public final class Logger 
{
	private long startTime;
	private java.text.MessageFormat logFormat;

	private static Logger logger = new Logger(); 

	private Logger()
	{
		startTime = System.currentTimeMillis();
		logFormat = new java.text.MessageFormat("{0,number,000}:{1,number,000} {2}");
	}

	
	public static Logger getLogger()
	{
		return logger;
	}
	
	public void log( Throwable t )
	{
		t.printStackTrace();
	}
	
	public void log( Object msg )
	{
		long t = System.currentTimeMillis() - startTime;
		System.out.println( logFormat.format( new Object[] { t/1000.0, t % 1000.0, msg } ));
	}


	
}
