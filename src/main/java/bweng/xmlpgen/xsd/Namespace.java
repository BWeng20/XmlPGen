/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.xsd;

import java.util.ArrayList;
import java.util.List;

import bweng.xmlpgen.xsd.Model.VisitOrder;
import java.io.File;

public final class Namespace 
{
   /// Generated short name of the namespace. 
	String name;
   /// Identification of the namespace 
	String uri;
	Documentation documentation;
   ArrayList<File> files = new ArrayList<File>();
	ArrayList<bweng.xmlpgen.xsd.Member> elements = new ArrayList<bweng.xmlpgen.xsd.Member>(); 
	
   TokenLookupTable lookup;
   
	public Namespace(String name, String uri, Documentation documentation)
	{
		this.name = name;
		this.uri  = uri;
		this.documentation = documentation;
	}
	
	public String getName() 
	{
		return name;
	}

   public ArrayList<File> getFiles() 
	{
		return files;
	}

   public void addFile( File file) 
	{
		files.add(file);
	}
   
	public String getUri() 
	{
		return uri;
	}

   
   public TokenLookupTable getLookup()
   {
      return lookup;
   }

   public void setLookup(TokenLookupTable lookup)
   {
      this.lookup= lookup;
   }
	
	public Documentation getDocumentation() 
	{
		return documentation;
	}
		
	public void addElement(bweng.xmlpgen.xsd.Member el) 
	{
		elements.add( el );
	}
	
   /**
    * This method should return always one element for a valid schema.
    * @return declared root elements
    */
	public List<Member> getElements()
	{
		return elements;
	}

   /**
    * This method return the root element for a valid schema.
    * @return declared root elements
    */
   public Member getRoot()
	{
		return elements.size()>0?elements.get(0):null;
	}

   
	public void visit( int level, ModelVisitor visitor, VisitOrder order )
	{
		if ( order == VisitOrder.PREFIX )
			visitor.visitNamespace(level, this);
		for (Member e : elements)
			e.visit(level+1, visitor, order);
		if ( order == VisitOrder.POSTFIX )
			visitor.visitNamespace(level, this);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof Namespace )
		{
			Namespace other = (Namespace)obj;
			return name.equals( other.getName() );
		}
		else
			return super.equals(obj);
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}

	@Override
	public String toString()
	{
		return name;
	}	
}
