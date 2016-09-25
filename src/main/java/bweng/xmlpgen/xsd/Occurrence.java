/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.xsd;


public final class Occurrence
{
	public static final Occurrence UNBOUND = new Occurrence(0,-1);
	
	public Occurrence( int min, int max )
	{
		this.min = min;
		this.max = max;
	}

   public boolean isOptional()
   {
      return min<=0;
   }
   
	public int getMax()
	{
		return max;
	}
	
	public int getMin()
	{
		return min;
	}
	
	int min;
	int max;
	
	public String toString()
	{
		if ( min == 0 && max == -1)
			return "";
		else
			return "["+min+" - "+(max==-1 ? "unbound" : max)+"]";
	}
}
