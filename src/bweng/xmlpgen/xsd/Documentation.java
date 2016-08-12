/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.xsd;

import java.util.ArrayList;
import java.util.List;

public class Documentation 
{
	ArrayList<String> lines = new ArrayList<String>(); 
	
	public Documentation()
	{
	}

	public void addLine( String line )
	{
		lines.add( line );
	}
	
	public List<String> getLines()
	{
		return lines;
	}

	public String getFirstLine() 
	{
		return (lines.size()>0) ? lines.get(0) : "";
	}
}
