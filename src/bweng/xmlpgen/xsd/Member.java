/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.xsd;

import bweng.xmlpgen.xsd.Model.VisitOrder;


public final class Member 
{
	Type   type;
	String namespace;
	String name;
	Documentation documentation;
	Occurrence occurence = Occurrence.UNBOUND;

	public Member( String namespace, String name, Documentation documentation, Type type )
	{
		this.namespace = namespace;
		this.type = type;
		this.name = name;
		this.documentation = documentation;
	}

	
	public Type getType()
	{
		return type;
	}

	public String getNamespace()
	{
		return namespace;
	}

	public String getName()
	{
		return name;
	}

	public Documentation getDocumentation()
	{
		return documentation;
	}
	
	/**
	 * Occurrence
	 */
	public void setOccurrence( Occurrence o )
	{
		occurence = o;
	}
	
	public Occurrence getOccurence()
	{
		return occurence;
	}
	
	//////////////////////////////////////////////////////////////
	// Generator support
	//////////////////////////////////////////////////////////////	

	String generatorName;
	
	/**
	 * Name of this element that generator uses.
	 */
	public String getGName()
	{
		return generatorName;
	}

	/**
	 * Sets the name of this element that generator uses.
	 */
	public void setGName(String generatorName)
	{
		this.generatorName = generatorName;
	}

	protected void visit( int level, ModelVisitor visitor, VisitOrder order )
	{	
		if ( order == VisitOrder.PREFIX )
			visitor.visitMember(level, this);
		if ( type != null)
			type.visit( level+1, visitor, order );
		if ( order == VisitOrder.POSTFIX )
			visitor.visitMember(level, this);
	}

}
