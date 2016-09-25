/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.xsd;

import bweng.xmlpgen.xsd.Model.VisitOrder;


public final class Attribute 
{
	public String getName()
	{
		return name;
	}

	public Documentation getDocumentation()
	{
		return documentation;
	}

	Type type;
	
	public Type getType()
	{
		return type;
	}

   public boolean getRequiered()
   {
      return requiered;
   }
   
   public void setRequiered( boolean r )
   {
      requiered = r;
   }   
   
	String name;
	Documentation documentation;
   boolean requiered;
	
	public Attribute( String name, Documentation documentation, Type type )
	{
		this.name = name;
		this.documentation = documentation;
		this.type = type;
      this.requiered = false;
	}
	
	//////////////////////////////////////////////////////////////
	// Generator support
	//////////////////////////////////////////////////////////////	
	
	String generatorName;	
	
	public String getGName()
	{
		return generatorName;
	}

	public void setGName(String generatorName)
	{
		this.generatorName = generatorName;
	}
		
	protected void visit( int level, ModelVisitor visitor, VisitOrder order )
	{
		if ( order == VisitOrder.PREFIX )
			visitor.visitAttribute( level, this);
		if ( type != null)
			type.visit( level+1, visitor, order );
		if ( order == VisitOrder.POSTFIX )
			visitor.visitAttribute( level, this);
	}
	
}
