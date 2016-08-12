/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.xsd;

import bweng.xmlpgen.tools.Logger;

public class LogModelVisitor implements ModelVisitor
{
	Logger log = Logger.getLogger();
	
	private String getPrefix(int level)
	{
		return "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t".substring(0, level);
	}
	
	@Override
	public void visitMember(int level, Member e)
	{
		Type type = e.getType(); 
		log.log( getPrefix(level)+"Element <"+e.getName()+"> ["+((type == null)?"none": type.name)+"] '"+ e.getDocumentation().getFirstLine()+"'" );
	}

	@Override
	public void visitAttribute(int level, Attribute a)
	{
		log.log(  getPrefix(level)+"Attribute <"+a.getName()+"> '"+a.getDocumentation().getFirstLine()+"'" );
	}

	@Override
	public void visitType(int level, Type t)
	{
		StringBuffer sb = new StringBuffer(100);
		sb.append( getPrefix(level) ).append("Type <");
		Namespace ns = t.getNamespace();
		if ( ns != null )
			sb.append( ns.getName() ).append(":");
			
		sb.append(t.getName()).append("> ");
		sb.append( t.getVariety() );
		switch ( t.getVariety() )
		{
		case SIMPLE_ABSENT: 
		case NONE:
		case SIMPLE_ATOMIC:
			sb.append( " base " ).append( t.getBaseType() );
			break;
		case COMPLEX:
		case SIMPLE_LIST:
		case SIMPLE_UNION:
		case WILDCARD:
			break;
		case COMPLEX_CHOICE:
			break;
		case COMPLEX_SEQUENCE:
			break;
		default:
			break;
		}
		
		log.log( sb.toString() );
	}

	@Override
	public void visitNamespace(int level, Namespace ns)
	{
		log.log( getPrefix(level) + "Namespace <"+ns.getName() + "> " + ns.getUri() + " '"+ ns.getDocumentation().getFirstLine()+"'" );
	}

}
