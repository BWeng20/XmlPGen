/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.adapter;

import bweng.xmlpgen.tools.Configuration;
import bweng.xmlpgen.xsd.Model;


public abstract class XsdAdapter 
{
	public abstract void loadModel( Model model, String uri );
	public abstract String dumpModel( String uri );
	
	private int symbolGenNameCounter = 0;
	protected final Configuration config;
	
	XsdAdapter( Configuration cfg )
	{
		config = cfg;		
	}
	
	/**
	 * Generate some meaningful name from Namespace specification.
	 * E.g. "http://www.mydomain.com/TEST/1/1" -> "TEST"
	 * @param ns
	 * @return
	 */
	public final String normalizeNamespace( String ns )
	{
		String subS = config.getNamespaceAlias( ns );
		if ( subS == null )
		{
			subS = "";
		
			int lastIdx = ns.length()-1;
			do
			{
				int nextIdx = ns.lastIndexOf( "/", lastIdx);
				if ( nextIdx < 0 )
					break;			
				subS = ns.substring(nextIdx+1, lastIdx+1 );
				if ( subS.isEmpty() )
				{
					lastIdx = nextIdx-1;
				}
				else
				{
					try
					{
						Integer.decode(subS);
						lastIdx = nextIdx-1;
					}
					catch (NumberFormatException e) 
					{
						break;
					}
				}
			} while ( true );
		}
		if ( subS.isEmpty() )
			subS = "Unnamed"+String.valueOf(++symbolGenNameCounter);
				
		return subS;
		
	}
	
	/**
	 * Normalise some type/element name to make it usable as part of a variable/member/class name.
	 * @param name
	 * @return Normalised name (usable as part of a class name)
	 */
	public final String normalizeName( String name )
	{
		if ( name == null )
		{
			name = "";
		}
		else
		{
			name = name.trim().replace(' ', '_' ) ;
		}
		
		if (name.isEmpty())
		{
			name = "Unnamed"+String.valueOf(++symbolGenNameCounter);
		}
		return name;
	}
	

	
}
