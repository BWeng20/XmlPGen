/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.xsd;

public class UnsupportedException extends RuntimeException 
{
	public UnsupportedException( String msg )
	{
		super(msg);
	}

	private static final long serialVersionUID = 42L;

}
