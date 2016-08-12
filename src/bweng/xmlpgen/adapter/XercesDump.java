/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.adapter;

import java.io.IOException;
import java.io.StringReader;

import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeGroupDefinition;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSModelGroupDefinition;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSNamespaceItemList;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSWildcard;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XercesDump
{
	
	DOMParser annotationParser;

	
	public XercesDump()
	{
		annotationParser = new DOMParser();
	}


	String getCompositorName( short comp )
	{
		switch (comp)
		{
		case XSModelGroup.COMPOSITOR_ALL : return "All";
		case XSModelGroup.COMPOSITOR_CHOICE: return "Choice";
		case XSModelGroup.COMPOSITOR_SEQUENCE: return "Sequence";
		}
		return String.valueOf(comp);
	}
			
	protected void append( String prefix, XSNamedMap map,  StringBuffer sb )
	{
		int anCount = map.getLength();
		for (int anIdx=0;anIdx<anCount;++anIdx)
		{
			append( prefix, map.item(anIdx), sb );			
		}		
	}
	
	void append( String prefix, XSParticle particle , StringBuffer sb )
	{
		if (null != particle)
		{
			XSTerm term = particle.getTerm();
			if ( null != term )
			{
				append(prefix, term, sb);
			}
			prefix += "\t";
			
			if ( particle.getMinOccurs() != 1 ||  particle.getMaxOccurs() != 1 ||  particle.getMaxOccursUnbounded() )
			{
				sb.append(prefix).append( "Occurrence [" );
				sb.append( particle.getMinOccurs() ).append('-');
				if ( particle.getMaxOccursUnbounded() )
				{
					sb.append( "unbound");
				}
				else
				{
					sb.append(particle.getMaxOccurs());
				}
				sb.append(']');
			}		
		}
	
	}

	void append( String prefix, XSObject obj, StringBuffer sb )
	{
		if ( null != obj )
		{	
		
			String nextLevel = prefix+"\t";
			String name = obj.getName();
					
			switch ( obj.getType() )
			{			
			case XSConstants.ELEMENT_DECLARATION:
				if ( obj instanceof XSElementDecl )
				{
					sb.append( prefix ).append ("Element" );
					if ( null != name && !name.isEmpty()) 
						sb.append( " <" ).append(name).append("> ");
					XSElementDecl element = (XSElementDecl)obj;
					append( nextLevel, element.getAnnotations(), sb );
					append( nextLevel, element.getTypeDefinition(), sb );
				}
				break;

			case XSConstants.ATTRIBUTE_DECLARATION:
				if ( obj instanceof XSAttributeDeclaration )
				{
					XSAttributeDeclaration ad = (XSAttributeDeclaration)obj;
					append( prefix, ad.getTypeDefinition(), sb );					
				}
				break;
				
				
			case XSConstants.ANNOTATION:
				append( prefix, (XSAnnotation)obj, sb );
				break;
			case XSConstants.TYPE_DEFINITION:
				if ( obj instanceof XSTypeDefinition )
				{
					sb.append( prefix );
					XSTypeDefinition xst = (XSTypeDefinition)obj;
					if ( xst instanceof XSComplexTypeDefinition)
					{						
						XSComplexTypeDefinition cType = (XSComplexTypeDefinition)xst;
						if ( null != name && !name.isEmpty()) 
							sb.append( "<" ).append(name).append("> ");
						sb.append ("Complex Type" );
						append( nextLevel, cType.getAnnotations()  , sb );
						append( nextLevel, cType.getParticle()     , sb );
						append( nextLevel, cType.getAttributeUses(), sb );
					}
					else if (xst instanceof XSSimpleTypeDefinition)
					{
						XSSimpleTypeDefinition xsSType = (XSSimpleTypeDefinition)xst;
						
						if ( null != name && !name.isEmpty()) 
							sb.append( "<" ).append(name).append("> ");
						sb.append( "Simple Type based on " ).append( xst.getBaseType().getName() );
						
						sb.append( nextLevel ).append( "Variety ");
						switch ( xsSType.getVariety() )
						{
						case XSSimpleTypeDefinition.VARIETY_ATOMIC:
							sb.append( "ATOMIC" );
							break;
						case XSSimpleTypeDefinition.VARIETY_LIST:
							sb.append( "LIST" );
							break;
						case XSSimpleTypeDefinition.VARIETY_UNION:
							sb.append( "UNION" );
							XSObjectList xsMemberTypes = xsSType.getMemberTypes();
							if ( null != xsMemberTypes )
							{
								for (int mIdx = 0; mIdx < xsMemberTypes.getLength() ; ++mIdx ) 
								{
									try 
									{
										append( nextLevel, (XSSimpleTypeDefinition)xsMemberTypes.item(mIdx), sb );
									} 
									catch (ClassCastException e) 
									{
										sb.append( nextLevel ).append('#').append(mIdx).append(" has not expected class type");
									}
								} 
							}
							break;
						case XSSimpleTypeDefinition.VARIETY_ABSENT:
						default:
							sb.append( "ABSENT" );
							break;
						}
						
					}
				}
				break;
			case XSConstants.ATTRIBUTE_USE:
				if ( obj instanceof XSAttributeUse )
				{
					sb.append( prefix );
					sb.append( "Attribute " );
					if ( null != name && !name.isEmpty()) 
						sb.append( "<" ).append(name).append("> ");
					XSAttributeUse attUse = (XSAttributeUse )obj;
					append( nextLevel, attUse.getAnnotations(), sb );
					append( nextLevel, attUse.getAttrDeclaration(), sb );
				}
				break;
			case XSConstants.MODEL_GROUP_DEFINITION:
				if ( obj instanceof XSModelGroupDefinition )
				{
					XSModelGroupDefinition gdef = (XSModelGroupDefinition)obj;
					sb.append( prefix ).append( "ModelGroupDef " );
					if ( null != name && !name.isEmpty()) 
						sb.append( "<" ).append(name).append("> ");
					append( nextLevel, gdef.getAnnotations(), sb );
					obj = gdef.getModelGroup();
				}
				// fall through
			case XSConstants.MODEL_GROUP:
				if ( obj instanceof XSModelGroup )
				{
					XSModelGroup grp = (XSModelGroup)obj;
					sb.append( prefix );
					if ( null != name && !name.isEmpty()) 
						sb.append( "<" ).append(name).append("> ");
					sb.append( "ModelGroup " );
					sb.append( getCompositorName( grp.getCompositor() ) ); 
					append( nextLevel, grp.getAnnotations(), sb );
					append( nextLevel, grp.getParticles()  , sb );
				}
				break;
			case XSConstants.ATTRIBUTE_GROUP:
				if ( obj instanceof XSAttributeGroupDefinition )
				{
					XSAttributeGroupDefinition grp = (XSAttributeGroupDefinition)obj;
					sb.append( prefix );
					if ( null != name && !name.isEmpty()) 
						sb.append( "<" ).append(name).append("> ");
					sb.append( "AttributeGroup " );
					append( nextLevel, grp.getAttributeWildcard(), sb );
					append( nextLevel, grp.getAttributeUses(), sb );
				}
				break;
				
			case XSConstants.WILDCARD:
				if ( obj instanceof XSWildcard )
				{
					XSWildcard wc = (XSWildcard)obj;
					sb.append( prefix );
					if ( null != name && !name.isEmpty()) 
						sb.append( "<" ).append(name).append("> ");
					sb.append( "Wildcard " );
					append( nextLevel, wc.getAnnotations(), sb );
					sb.append( " Constraint ").append( wc.getConstraintType());
					sb.append( " ProcessContents ").append( wc.getProcessContents() );
				}
				break;
				
			case XSConstants.PARTICLE:
				append( prefix , (XSParticle)obj, sb);
				break;
			default:
				sb.append( prefix );
				if ( null != name && !name.isEmpty()) 
					sb.append( "<" ).append(name).append("> ");
				sb.append("Unsupported type ").append( obj.getType() );
			}
		}
	}

	
	void append( String prefix, XSObjectList list, StringBuffer sb )
	{
		if ( null != list )
		{
			int anCount = list.getLength();
			for (int anIdx=0;anIdx<anCount;++anIdx)
			{
				append( prefix, list.item(anIdx), sb );
			}
		}
	}	
	
	void appendText( String prefix, String content, StringBuffer sb )
	{
		if ( null != content )
		{
			String[] lines = content.split("\n");
			if ( null != lines )
			{
				int lineCount = 0;
				for (int lIdx=0 ; lIdx < lines.length ; ++lIdx )
				{
					lines[lIdx] = lines[lIdx].trim();
					if ( lines[lIdx].length() > 0 )
					{
						++lineCount;
					}
				}
				for (int lIdx=0 ; lIdx < lines.length ; ++lIdx )
				{
					if ( lines[lIdx].length() > 0 )
					{
						if ( lineCount > 1 )
						{
							sb.append( prefix ).append(">> ").append(lines[lIdx]);
						}
						else
						{
							sb.append( lines[lIdx]);
						}
					}
				}
			}
		}
	}

	
	void append( String prefix, XSAnnotation an, StringBuffer sb )
	{
		if ( null != an )
		{
			sb.append( prefix ).append( "Annotation: " );
			prefix += "\t";
			
			StringReader aReader = new StringReader(an.getAnnotationString());
			InputSource aSource = new InputSource(aReader);
			
			annotationParser.reset();
	
			try {
				annotationParser.parse(aSource);
			} 
			catch (SAXException | IOException e) 
			{
				e.printStackTrace();
			}
			Document aDocument = annotationParser.getDocument();
			Element annotation = (aDocument != null) ? aDocument.getDocumentElement() : null;
			
			if ( annotation != null )
			{
				appendText( prefix, annotation.getTextContent(), sb );
			}
		}
	}

	public String dumpModel( XSModel model )
	{
		try
		{
			if ( null == model ) return "";
			
			StringBuffer sb = new StringBuffer(1024); 
			{
				StringList ns = model.getNamespaces();
				int count = ns.getLength();		
				for (int idx=0 ; idx < count ; ++idx )
				{				
					sb.append("Namespace #").append( idx ).append( " <").append( ns.item(idx) ) .append(">\n");
				}
			}
			sb.append("\n");
			{
				XSNamespaceItemList nsiList = model.getNamespaceItems();
				int count = nsiList.getLength();
				
				for (int idx=0 ; idx < count ; ++idx )
				{
					XSNamespaceItem item = nsiList.item(idx);
					if ( !item.getSchemaNamespace().endsWith( "/XMLSchema" ))
					{
						sb.append( "NamespaceItem SchemaNamespace <" ).append( item.getSchemaNamespace() ).append(">");
					    append( "\n", item.getAnnotations(), sb );
						sb.append("\nElements:");
						append( "\n\t", item.getComponents( XSConstants.ELEMENT_DECLARATION ), sb );
						sb.append("\nTypes:");
						append( "\n\t", item.getComponents( XSConstants.TYPE_DEFINITION  ), sb );
						return sb.toString();
					}
				}
			}
			
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		return "";
	}

}
