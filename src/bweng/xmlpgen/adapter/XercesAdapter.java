/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.adapter;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
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
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import bweng.xmlpgen.tools.Configuration;
import bweng.xmlpgen.tools.Logger;
import bweng.xmlpgen.xsd.Documentation;
import bweng.xmlpgen.xsd.Member;
import bweng.xmlpgen.xsd.Model;
import bweng.xmlpgen.xsd.Namespace;
import bweng.xmlpgen.xsd.Occurrence;
import bweng.xmlpgen.xsd.Type;
import bweng.xmlpgen.xsd.Type.BASE_TYPE;
import bweng.xmlpgen.xsd.UnsupportedException;
import org.apache.xerces.xs.XSMultiValueFacet;

public class XercesAdapter extends XsdAdapter
{
	
	DOMParser annotationParser;
	Logger logger;
   
   Namespace xmlSchemaNS = new Namespace( "XMLSchema", "http://www.w3.org/2001/XMLSchema", null );
	Type wildcardType = new Type( xmlSchemaNS, "any", new Documentation());
	
	public XercesAdapter(Configuration cfg )
	{
		super( cfg );
		
		annotationParser = new DOMParser();
		logger = Logger.getLogger();
	}

	
	//////////////////////////////////////////////////
	//
	// IO
	//
	//////////////////////////////////////////////////
	
	XSModel loadXSModel( String uri )
	{
		logger.log("Loading "+uri);
      
		// Get DOM Implementation using DOM Registry
		System.setProperty(DOMImplementationRegistry.PROPERTY, "org.apache.xerces.dom.DOMXSImplementationSourceImpl");

		XSImplementation impl =null;
		
		DOMImplementationRegistry registry;
		try {
			registry = DOMImplementationRegistry.newInstance();
			impl = (XSImplementation) registry.getDOMImplementation("XS-Loader");
			
		} 
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException e) 
		{
			e.printStackTrace();
			return null;
		}
		XSLoader schemaLoader = impl.createXSLoader(null);
		try
		{
			XSModel model = schemaLoader.loadURI(uri);
			return model;
			
		} catch (Exception e) 
		{
			logger.log( e );
			return null;
		}
	}

	//////////////////////////////////////////////////
	//
	// Type Cache
	//
	//////////////////////////////////////////////////
	
	HashMap<String, HashMap<Object,Object>> cache = new HashMap<String, HashMap<Object,Object>>();  

	protected final HashMap<Object,Object> getTypeCache(  Class<?> clazz )
	{
		HashMap<Object,Object> typeCache = cache.get( clazz.getSimpleName() );
		if ( null == typeCache )
		{
			typeCache = new HashMap<Object,Object>(10);
			cache.put(  clazz.getSimpleName(), typeCache);
		}
		return typeCache;
	}
	
	
	@SuppressWarnings("unchecked")
	protected final <E> E getFromCache( XSObject obj, Class<E> clazz )
	{	
		HashMap<Object,Object> typeCache = getTypeCache( clazz );
		
		String name = obj.getName();
		if ( name == null || name.isEmpty() )
		{
			return (E)typeCache.get( obj );	
		}
		else
		{
			return (E)typeCache.get( obj.getNamespace()+":"+name );
		}
	}	

	protected <E> void putToCache( XSObject obj, E item )
	{
		HashMap<Object,Object> typeCache = getTypeCache( item.getClass() );
		
		String name = obj.getName();
		if ( name == null || name.isEmpty() )
		{
			typeCache.put( obj, item );	
		}
		else
		{
			typeCache.put( obj.getNamespace()+":"+name, item );
		}
		
	}
	
	Namespace getNamespace( XSNamespaceItem  xsNs )
	{
		return (xsNs != null) ? getNamespace( xsNs.getSchemaNamespace(), xsNs.getAnnotations() ) 
				              : null ;
		
	}
	
	Namespace getNamespace( String xsNs, XSObjectList anList )
	{
		HashMap<Object,Object> typeCache = getTypeCache( Namespace.class );
		Namespace ns = (Namespace )typeCache.get(xsNs );
		if ( null == ns )
		{
			String name = normalizeNamespace( xsNs );
			ns = new bweng.xmlpgen.xsd.Namespace( name, xsNs, convertAnnotations(anList) );
			typeCache.put( xsNs, ns );
		}
		return ns;
	}
	
	Namespace getNamespaceFor( XSObject xsObj )
	{
		XSNamespaceItem nsItem = xsObj.getNamespaceItem();
		if ( null == nsItem )
			return getNamespace( xsObj.getNamespace(), null );
		else
			return getNamespace( nsItem );
	}

	
	//////////////////////////////////////////////////
	//
	// Conversion from Xerces Model to PGen Model
	//
	//////////////////////////////////////////////////
	
	void convertAnnotation( Documentation doc, XSAnnotation an )
	{
		if ( null != an )
		{
			StringReader aReader = new StringReader(an.getAnnotationString());
			InputSource aSource = new InputSource(aReader);
			annotationParser.reset();
	
			try 
			{
				annotationParser.parse(aSource);
			} 
			catch (SAXException | IOException e) 
			{
				logger.log( e );
			}
			Document aDocument = annotationParser.getDocument();
			Element annotation = (aDocument != null) ? aDocument.getDocumentElement() : null;
			
			if ( annotation != null )
			{
				String text = annotation.getTextContent();
            String[] lines = text.trim().split("\n");
				for (String line : lines)
					doc.addLine( line );
			}
		}
	}
	
	Documentation convertAnnotations( XSObjectList anList )
	{		
		Documentation doc = new Documentation();
		if ( anList != null )
		{
			for (int i=0 ; i<anList.getLength() ; ++i)
			{
				XSObject obj = anList.item(i);
				if ( null != obj && obj instanceof XSAnnotation)
				{
					convertAnnotation(doc, (XSAnnotation)obj );
				}
			}
		}			
		return doc;
	}


	bweng.xmlpgen.xsd.Attribute convert( XSAttributeDeclaration att )
	{		
		bweng.xmlpgen.xsd.Attribute attribute = new bweng.xmlpgen.xsd.Attribute( normalizeName(att.getName()), convertAnnotations( att.getAnnotations() ), convert( att.getTypeDefinition()) );	
		return attribute;
	}

	bweng.xmlpgen.xsd.Attribute convert( XSAttributeUse attUse )
	{
      bweng.xmlpgen.xsd.Attribute att = convert( attUse.getAttrDeclaration() );
      att.setRequiered(attUse.getRequired());
      return att;
	}

	
	bweng.xmlpgen.xsd.Type convert( XSSimpleTypeDefinition xsSType )
	{
		bweng.xmlpgen.xsd.Type type = getFromCache(xsSType, bweng.xmlpgen.xsd.Type.class );
		if ( null == type )
		{
			type = new bweng.xmlpgen.xsd.Type( getNamespace( xsSType.getNamespaceItem() ), normalizeName( xsSType.getName() ), convertAnnotations( xsSType.getAnnotations() ) ); 
			switch ( xsSType.getVariety() )
			{
			case XSSimpleTypeDefinition.VARIETY_ATOMIC:
				type.setVariety(bweng.xmlpgen.xsd.Type.VARIETY.SIMPLE_ATOMIC);
				{				
					short kind = xsSType.getBuiltInKind();
					switch ( kind )
					{
               case XSConstants.FLOAT_DT:
						type.setBaseType(bweng.xmlpgen.xsd.Type.BASE_TYPE.FLOAT32);
                  break;
					case XSConstants.DOUBLE_DT:
					case XSConstants.DECIMAL_DT:
						type.setBaseType(bweng.xmlpgen.xsd.Type.BASE_TYPE.FLOAT64);
						break;						
					case XSConstants.BYTE_DT:
						type.setBaseType(bweng.xmlpgen.xsd.Type.BASE_TYPE.INT8);
						break;						
					case XSConstants.SHORT_DT:
						type.setBaseType(bweng.xmlpgen.xsd.Type.BASE_TYPE.INT16);
						break;
					case XSConstants.INT_DT:
						type.setBaseType(bweng.xmlpgen.xsd.Type.BASE_TYPE.INT32);
						break;						
					case XSConstants.INTEGER_DT:
					case XSConstants.NEGATIVEINTEGER_DT:
					case XSConstants.NONPOSITIVEINTEGER_DT:
					case XSConstants.LONG_DT:
						type.setBaseType(bweng.xmlpgen.xsd.Type.BASE_TYPE.INT64);
						break;
					case XSConstants.UNSIGNEDBYTE_DT:
						type.setBaseType(bweng.xmlpgen.xsd.Type.BASE_TYPE.UINT8);
						break;						
					case XSConstants.UNSIGNEDSHORT_DT:
						type.setBaseType(bweng.xmlpgen.xsd.Type.BASE_TYPE.UINT16);
						break;						
					case XSConstants.UNSIGNEDINT_DT:
						type.setBaseType(bweng.xmlpgen.xsd.Type.BASE_TYPE.UINT32);
						break;
						
					case XSConstants.POSITIVEINTEGER_DT:
					case XSConstants.NONNEGATIVEINTEGER_DT:
					case XSConstants.UNSIGNEDLONG_DT:
						type.setBaseType(bweng.xmlpgen.xsd.Type.BASE_TYPE.UINT64);
						break;						
						
					case XSConstants.BOOLEAN_DT:
						type.setBaseType(bweng.xmlpgen.xsd.Type.BASE_TYPE.BOOLEAN);
						break;
					case XSConstants.DATE_DT:
						type.setBaseType(bweng.xmlpgen.xsd.Type.BASE_TYPE.DATE);
						break;
					case XSConstants.DATETIME_DT:
						type.setBaseType(bweng.xmlpgen.xsd.Type.BASE_TYPE.DATETIME);
						break;
					case XSConstants.TIME_DT:
						type.setBaseType(bweng.xmlpgen.xsd.Type.BASE_TYPE.TIME);
						break;
					case XSConstants.NORMALIZEDSTRING_DT:
					case XSConstants.STRING_DT:
					case XSConstants.ANYURI_DT:
					case XSConstants.BASE64BINARY_DT:
					case XSConstants.GYEAR_DT:
					case XSConstants.GYEARMONTH_DT:
					case XSConstants.GMONTH_DT:
					case XSConstants.GMONTHDAY_DT:
					case XSConstants.GDAY_DT:
						type.setBaseType(bweng.xmlpgen.xsd.Type.BASE_TYPE.STRING);
						break;
					case XSConstants.MODEL_GROUP:
						type.setBaseType(bweng.xmlpgen.xsd.Type.BASE_TYPE.NONE);
						break;
               case XSConstants.HEXBINARY_DT:
						type.setBaseType(bweng.xmlpgen.xsd.Type.BASE_TYPE.BINARY );
						break;
					default:
					case XSConstants.ANYSIMPLETYPE_DT:
						throw new UnsupportedException("BuildIn kind "+kind+" is not supported. Type "+xsSType);
					}
					
					
					// Because we don't care about validity, this generator handles only enumerations of string types.
					switch (  type.getBaseType() )
					{
					case BOOLEAN:
					case DATE:
					case DATETIME:
					case FLOAT32:
					case FLOAT64:
					case INT16:
					case INT32:
					case INT64:
					case INT8:
					case NONE:
					case TIME:
					case UINT16:
					case UINT32:
					case UINT64:
					case UINT8:
						break;
					case STRING:					
                  XSObjectList mvfList = xsSType.getMultiValueFacets();
                  if ( mvfList != null )
                  {
                     for (int fi=0 ; fi < mvfList.size(); ++fi )
                     {
                        XSMultiValueFacet mvf = (XSMultiValueFacet)mvfList.item(fi);
                        if ( mvf.getFacetKind() == XSSimpleTypeDefinition.FACET_ENUMERATION )
                        {
                           StringList   enumValueList = mvf.getLexicalFacetValues();
                           XSObjectList enumAnnotationList = mvf.getAnnotations();
                           if ( enumValueList != null && enumValueList.size() > 0 )
                           {
                              for ( int ei=0 ; ei<enumValueList.size(); ++ei)
                              {  
                                 Documentation doc = new Documentation();
                                 if (enumAnnotationList!=null && ei<enumAnnotationList.size() )
                                   	convertAnnotation( doc, (XSAnnotation)enumAnnotationList.item(ei) );
                                 type.addEnumValue( enumValueList.item(ei), doc );
                              }
                           }
                        }
                     }                    
                  }
						break;
					default:
						break;
					
					}
				}
				break;
			case XSSimpleTypeDefinition.VARIETY_LIST:
				type.setVariety(bweng.xmlpgen.xsd.Type.VARIETY.SIMPLE_LIST);
				type.addItemType( convert( xsSType.getItemType()) );
				break;
			case XSSimpleTypeDefinition.VARIETY_UNION:
				type.setVariety(bweng.xmlpgen.xsd.Type.VARIETY.SIMPLE_UNION);
				XSObjectList xsMemberTypes = xsSType.getMemberTypes();
				if ( null != xsMemberTypes )
				{
					for (int mIdx = 0; mIdx < xsMemberTypes.getLength() ; ++mIdx ) 
					{
						try 
						{
							XSSimpleTypeDefinition xsMemberType = (XSSimpleTypeDefinition)xsMemberTypes.item(mIdx);
							type.addItemType( convert(xsMemberType) );
						} 
						catch (ClassCastException e) 
						{
							logger.log( xsSType.getName()+" MemberType item #"+mIdx+" is "+xsMemberTypes.item(mIdx).getClass().getSimpleName()+" - expected was XSSimpleTypeDefinition" );
						}
					} 
				}
				break;
			case XSSimpleTypeDefinition.VARIETY_ABSENT:
			default:
				type.setVariety(bweng.xmlpgen.xsd.Type.VARIETY.SIMPLE_ABSENT);
				break;
			}
			putToCache( xsSType, type );
		}
		return type;		
	}


	private bweng.xmlpgen.xsd.EnumValue convertEnum(Object object, BASE_TYPE baseType)
	{
		// TODO Auto-generated method stub
		return null;
	}


	void addParticle( bweng.xmlpgen.xsd.Type type, XSParticle particle, boolean parentGroupUnbound )
	{
		if ( particle != null )
		{
			Member m = null;
			XSTerm term = particle.getTerm();
			if ( term != null)
			{
				switch (term.getType())
				{
				case XSConstants.ELEMENT_DECLARATION:
					m = convert( type.getNamespace(), (XSElementDeclaration)term ) ;
					break;
				case XSConstants.MODEL_GROUP:
					{
						XSModelGroup grp = (XSModelGroup)term;
						XSObjectList parts = grp.getParticles();
						
						switch ( grp.getCompositor() )
						{
						case XSModelGroup.COMPOSITOR_SEQUENCE:
							type.setVariety(bweng.xmlpgen.xsd.Type.VARIETY.COMPLEX_SEQUENCE);
							break;
						case XSModelGroup.COMPOSITOR_CHOICE:
							type.setVariety(bweng.xmlpgen.xsd.Type.VARIETY.COMPLEX_CHOICE);
							break;
						case XSModelGroup.COMPOSITOR_ALL:  
							break;
						}
						for (int pIdx=0 ; pIdx < parts.getLength(); ++pIdx)
						{					
							addParticle( type, (XSParticle)parts.item(pIdx), particle.getMaxOccursUnbounded());
						}
					}
					break;
				case XSConstants.WILDCARD:
					XSWildcard wc = (XSWildcard)term;
					m = new Member(type.getNamespace().getName(),wc.getName(), convertAnnotations(wc.getAnnotations()), wildcardType );
					break;
				}
			}
			if ( m != null )
			{
				// Occurrence
				int min = particle.getMinOccurs();
				int max = Occurrence.UNBOUND.getMax();
				
				if ( !(parentGroupUnbound || particle.getMaxOccursUnbounded()) )
				{
					max = particle.getMaxOccurs();
				}
				m.setOccurrence( new Occurrence(min, max));
				type.addMembers( m );
			}
			
		}		
	}

	
	bweng.xmlpgen.xsd.Type convert( XSElementDeclaration usingElement, XSTypeDefinition xsType )
	{
		if ( null != xsType )
		{
			Namespace ns = getNamespaceFor(xsType);
			bweng.xmlpgen.xsd.Type type = getFromCache(xsType, bweng.xmlpgen.xsd.Type.class );
			if ( null == type )
			{
				String name;
				if ( xsType.getAnonymous() )
				{					
					name = normalizeName( usingElement.getEnclosingCTDefinition().getName() ) + "_" + normalizeName( usingElement.getName() );
				}
				else
				{
					name = normalizeName( xsType.getName() );
				}
						
				if ( xsType instanceof XSComplexTypeDefinition)
				{						
					XSComplexTypeDefinition xsCxType = (XSComplexTypeDefinition)xsType;
               
					type = new bweng.xmlpgen.xsd.Type( ns, name, convertAnnotations( xsCxType.getAnnotations() ) );
               type.setMixed( xsCxType.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_MIXED );
					type.setVariety(bweng.xmlpgen.xsd.Type.VARIETY.COMPLEX);
					XSObjectList xsAttributes = xsCxType.getAttributeUses();
					for (int xsIdx = 0 ; xsIdx < xsAttributes.getLength(); ++xsIdx)
					{
						XSObject xsAtt = xsAttributes.item(xsIdx);
						if ( xsAtt instanceof XSAttributeUse)
						{
							type.addAttribute( convert( (XSAttributeUse)xsAtt ));
						}
						else
						{
							logger.log( name + " AttributeUses item #"+xsIdx+" has type "+xsAtt.getClass().getSimpleName()+" - expected was XSAttributeUse" );
						}
					}
					XSParticle particle = xsCxType.getParticle();
					if ( particle != null )
					{						
						// Sub elements
						addParticle( type, particle, false );
						
					}
					putToCache( xsType, type);
				}
				else if (xsType instanceof XSSimpleTypeDefinition)
				{			
					type = convert( (XSSimpleTypeDefinition)xsType );
				}			
				else
				{
					logger.log( name + " has unexpeced type "+xsType.getClass().getSimpleName() );
				}
			}
			return type;		
		}
		return null;
	}
	
	bweng.xmlpgen.xsd.Member convert( Namespace ns, XSElementDeclaration xsElement )
	{
		if ( xsElement != null )
		{
			return new bweng.xmlpgen.xsd.Member(
				     xsElement.getNamespace()
				   , normalizeName( xsElement.getName())
				   , convertAnnotations(xsElement.getAnnotations())
				   , convert(xsElement, xsElement.getTypeDefinition() ));
		}
		else
			return null;
	}

	
	bweng.xmlpgen.xsd.Namespace convert( XSNamespaceItem xsNs)
	{
		if ( null != xsNs )
		{	
						
			bweng.xmlpgen.xsd.Namespace ns = getNamespace( xsNs );
			
			XSNamedMap map = xsNs.getComponents( XSConstants.ELEMENT_DECLARATION );
			int elCount = map.getLength();
			for (int elIdx=0;elIdx<elCount;++elIdx)
			{
				bweng.xmlpgen.xsd.Member el = convert( ns, (XSElementDeclaration)map.item(elIdx) );
				if ( null != el )
				{
					ns.addElement( el );
				}
			}
			return ns;
		}
		return null;
	}

	//////////////////////////////////////////////////
	//
	// Implementation of XsdAdapter
	//
	//////////////////////////////////////////////////
	
	public String dumpModel( String uri )
	{
		XercesDump dumper = new XercesDump(); 
		return dumper.dumpModel( loadXSModel( uri ) );
	}

	public void loadModel( Model model, String uri )
	{
		XSModel xsModel = loadXSModel( uri );
		if ( null != xsModel && null != model)
		{
			XSNamespaceItemList xsNsList = xsModel.getNamespaceItems();
			for (int nsIdx=0 ; nsIdx < xsNsList.getLength() ; ++nsIdx )
			{
				XSNamespaceItem xsNs = xsNsList.item(nsIdx);
				if ( !xsNs.getSchemaNamespace().endsWith( "/XMLSchema" ) )
				{
					model.addNamespace( convert( xsNs ));
				}
			}
			// logger.log("Dumping Model ================");
			// model.log( "", logger );
		}
	}

	
}
