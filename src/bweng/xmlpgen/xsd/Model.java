/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.xsd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Model 
{
	ArrayList<Namespace> namespaces = new ArrayList<Namespace>();
   
	public void addNamespace( Namespace ns )
	{
		namespaces.add(ns);
	}
	
	public List<Namespace> getNamespaces()
	{
		return namespaces;
	}
	
	public enum VisitOrder
	{
		PREFIX ,
		POSTFIX
	}
	
	/**
	 * 
	 * @param visitor
	 * @param order
	 */
	public void visit( ModelVisitor visitor, VisitOrder order )
	{
		for (Namespace ns : namespaces)
		{
			ns.visit( 0, visitor, order );
		}
	}

}
