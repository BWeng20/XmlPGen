/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.xsd;

import java.util.ArrayList;
import java.util.List;

import bweng.xmlpgen.xsd.Model.VisitOrder;

public final class Type 
{
	public enum VARIETY
	{
		NONE          ,
		COMPLEX       , 
		SIMPLE_ABSENT , 
		SIMPLE_ATOMIC , 
		SIMPLE_LIST   , 
		SIMPLE_UNION  ,
		COMPLEX_CHOICE,
		COMPLEX_SEQUENCE,
		WILDCARD
	};
	
	public enum BASE_TYPE
	{
		NONE   ,
		
		FLOAT32,
		FLOAT64,
		
		INT8,
		INT16,
		INT32,
		INT64,

		UINT8,
		UINT16,
		UINT32,
		UINT64,
		
		BOOLEAN,
		DATE   ,
		TIME   ,
		DATETIME,
		STRING,
      BINARY
	};
	
	VARIETY   variety  = VARIETY.NONE;
	BASE_TYPE baseType = BASE_TYPE.NONE;
   boolean   mixed      = false;
	
	Namespace namespace;
	String name;
	Documentation documentation;
	
	ArrayList<Attribute> attributes = new ArrayList<Attribute>();
	ArrayList<Type>       itemTypes = new ArrayList<Type>();
	ArrayList<Member>     members = new ArrayList<Member>();
	ArrayList<EnumValue>  enums   = new ArrayList<EnumValue>();
   
	public Type( Namespace namespace, String name, Documentation documentation)
	{
		this.namespace = namespace;
		this.name = name;
		this.documentation = documentation;
	}
	
	
	public void addAttribute( Attribute att)
	{
		attributes.add(att);
	}

	public void setVariety(VARIETY v)
	{
		variety = v;
	}
	
	public VARIETY getVariety()
	{
		return variety;
	}
   
   public void setMixed(boolean m)
   {
		mixed = m;
   }
   
	public boolean isMixed()
	{
      return mixed;
	}

	/**
	 * For UNION types: list of possible types.
	 * For LIST types: Item type (one item).
	 * Empty for other types.
	 * @param itemType
	 */
	public void addItemType(Type itemType) 
	{
		this.itemTypes.add( itemType );
	}
	
	/**
	 * Base type for simple ATOMIC types.
	 */
	public BASE_TYPE getBaseType()
	{
		return baseType;
	}

   public boolean isAtomic()
	{
		return baseType != BASE_TYPE.NONE;
	}

	public void setBaseType( BASE_TYPE baseType )
	{
		this.baseType = baseType;
	}
	
	/**
	 * Members for COMPLEX Types.
	 * 
    * @return List of members
	 */
	public List<Member> getMembers()
	{
		return members;
	}
	
	public void addMembers(Member e)
	{
      if ( e.name == null || e.name.isEmpty() )
      {
         int postfix = 0;
         e.name = e.type.name;
         boolean memberFound;
         do
         {
            memberFound = false;
            for ( Member mo : members)
               if ( mo.name.equalsIgnoreCase(e.name) )
               {
                  ++postfix;
                  e.name = e.type.name+postfix;
                  memberFound = true;
                  break;
               }
         } while (memberFound);
      }
		members.add( e );
	}


	/**
	 * For restricted types with enums.
	 */

	public boolean isEnum()  
	{
		return !enums.isEmpty();
	}
   
   public boolean is( String namespace, String name )
   {
      if ( namespace == null )
      {
         return this.namespace == null && (name != null) && this.name.equals(name);
      }
      else if ( this.namespace != null )
      {
         return namespace.equals( this.namespace.name );
      }
      return false;
   }
	
	/**
	 *  Get list of enumeration values.
	 *  All enumeration values of one type share the same base type (identical to the base type of the containing type).
	 */
	public List<EnumValue> getEnums()
	{
		return enums;
	}
	
	public void addEnumValue(String value, Documentation doc)
	{
		EnumValue e = new EnumValue();
		e.type = this;
		e.value = value;
      e.documentation = doc;
		enums.add(e);		
	}
	
	/**
	 * For UNION types: list of possible types.
	 * For LIST types: Item type (one item).
	 * Empty for other types.
	 * @return Type of null
	 */
	public List<Type> getItemTypes() 
	{
		return itemTypes;
	}

	
	/**
	 * Common informations.
	 */

	
	public Namespace getNamespace()
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

	public List<Attribute> getAttributes()
	{
		return attributes;
	}

	/**
	 * Implements Visitor pattern. 
	 * @see Model#visit
	 * @param level
	 * @param visitor
	 * @param order
	 */
	protected void visit( int level, ModelVisitor visitor, VisitOrder order )
	{
		if ( order == VisitOrder.PREFIX )
			visitor.visitType(level, this);
		++level;
		for (Attribute a : attributes)
			a.visit( level, visitor, order );
		for (Member e : members)
			e.visit( level, visitor, order );
		for (Type t : itemTypes)
			t.visit(level, visitor, order);
		--level;
		if ( order == VisitOrder.POSTFIX )
			visitor.visitType(level, this);
	}
	
	//////////////////////////////////////////////////////////////
	// Collection support
	//////////////////////////////////////////////////////////////	

	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof Type )
		{
			Type other = (Type)obj;
			return other.getName().equals(name) && ((namespace!=null) ? namespace.equals( other.getNamespace()) : other.getNamespace() == null);
		}
		else
			return super.equals(obj);
	}

	@Override
	public int hashCode()
	{
		return name.hashCode() ^ ((namespace != null) ? namespace.hashCode() : 0);
	}

	@Override
	public String toString()
	{
		if (namespace != null)
			return namespace.getName()+"."+ name;
		else			
			return name;
	}
	
 
}
