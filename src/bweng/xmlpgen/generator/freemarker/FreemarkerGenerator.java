/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.generator.freemarker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import bweng.xmlpgen.generator.Generator;
import bweng.xmlpgen.tools.FileGenerateSpecification;
import bweng.xmlpgen.tools.PropertyTree;
import bweng.xmlpgen.xsd.Attribute;
import bweng.xmlpgen.xsd.Documentation;
import bweng.xmlpgen.xsd.Member;
import bweng.xmlpgen.xsd.Model;
import bweng.xmlpgen.xsd.Namespace;
import bweng.xmlpgen.xsd.Type;
import freemarker.cache.TemplateLoader;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class FreemarkerGenerator extends Generator
{
   public static final Version freemarkerVersion = new Version( 2,3,24);
	
	public static final String PROPERTY_TEMPLATE_DIR= "freemarker.TemplateBaseDirectory";	
	
	freemarker.template.Configuration fmConfiguration;
	PropertyTree options;
   HashSet<Type> generatedTypes = new HashSet<Type>();
    
   HashMap<Type,TypeOptions> typesOptions = new  HashMap<Type,TypeOptions>();
   HashMap<Namespace,NamespaceOptions> namespaceOptions = new  HashMap<Namespace,NamespaceOptions>();
    
    TypeOptions getTypeOptions( Type t )
    {
    	TypeOptions typeOptions = (TypeOptions)typesOptions.get(t);
    	if ( typeOptions == null)
    	{
    		typeOptions = new TypeOptions(t, this);
    		typesOptions.put(t,typeOptions );
    	}
    	return typeOptions;    	
    }
    
    NamespaceOptions getNamespaceOptions( Namespace ns )
    {
       if ( ns != null )
       {
         NamespaceOptions nsOptions = (NamespaceOptions)namespaceOptions.get(ns);
         if ( nsOptions == null)
         {
            nsOptions = new NamespaceOptions(ns,this);
            namespaceOptions.put(ns,nsOptions );
         }
         return nsOptions;    	
       }
       else
          return null;
    }

    HashMap<Attribute,AttributeOptions> attributesOptions = new  HashMap<Attribute,AttributeOptions>();

    AttributeOptions getAttributeOptions( Attribute a )
    {
    	AttributeOptions attributeOptions = (AttributeOptions)attributesOptions.get(a);
    	if ( attributeOptions == null)
    	{
    		attributeOptions = new AttributeOptions(a, this);
    		attributesOptions.put(a, attributeOptions );
    	}
    	return attributeOptions;    	
    }

   
   // Standard types
   // Listtype, added to element that contains lists to resolve iuncludes.
   Namespace coreNamespace = new Namespace("Core", "", new Documentation());
   Type listType = new Type( coreNamespace, "List", new Documentation());
   TypeOptions listTypeOptions;

   // String type, added to mixed element.
   Type stringType;
   TypeOptions stringTypeOptions;

   // POinter type, added to complex elements.
   Type pointerType;
   TypeOptions pointerTypeOptions;

   
   URL templateURL;

   public URL getTemplateURL(String template)
	{
      try
      {
         return new URL( templateURL.toString()+template );
      }
      catch (Exception ex)
      {
         return null;
      }
	}
   
   
	public FreemarkerGenerator( String targetDirectory, bweng.xmlpgen.tools.Configuration cfg ) throws IOException
	{
		super(targetDirectory, cfg);
      this.nonamespaceOptions = new PropertyTree();
      this.nonamespaceFiles = new ArrayList<>();
           
      fmConfiguration = new freemarker.template.Configuration(freemarkerVersion);
      log.log("Freemarker Version "+fmConfiguration.getVersion() );

		Properties optionProps = new Properties();
		try 
		{
         URL url = cfg.getBaseURL();
         String templateDir = cfg.getProperty( PROPERTY_TEMPLATE_DIR, "")+"/";
         templateURL = new URL( url.toString()+templateDir );

         fmConfiguration.setTemplateLoader( new TemplateLoader()
         {
            @Override
            public Object findTemplateSource(String name) 
            {
               URL url = getTemplateURL(  name );
               if ( url != null )
               {
                  try
                  {
                     return url.openStream();
                  }
                  catch (Exception ex)
                  {
                     // ex.printStackTrace();
                  }
               }
               return null;
            }

            @Override
            public long getLastModified(Object object)
            {
               return 0;
            }

            @Override
            public Reader getReader(Object templateSource, String encoding) throws IOException
            {
               return new InputStreamReader( (InputStream)templateSource, encoding );
            }

            @Override
            public void closeTemplateSource(Object templateSource) throws IOException
            {
               ((InputStream)templateSource).close();
            }
            
         } );
         URL optUrl = new URL( templateURL.toString()+"options.ini" );
      	log.log( "Read options from "+ optUrl );
			optionProps.load( optUrl.openStream() );
		}
		catch (Exception e) 
		{
			log.log( e );
		}
		options = cfg.getPropertyTree("freemarker", optionProps);
      
      String langTag = options.getTreeProperty("locale", "en_GB");
      Locale l = Locale.forLanguageTag(langTag);
      if ( l != null )
         fmConfiguration.setLocale(l);
      else
         log.log("Invalid locale '+langTag+'");
		
		listTypeOptions = new TypeOptions(listType, this);
		listTypeOptions.put("generate", false );
		listTypeOptions.put("usage", "ByReference" );
		listTypeOptions.put("GNamespace", "" );
		listTypeOptions.put("GPath", options.getTreeProperty( "type.list.include" , ""));
      listTypeOptions.put("isEnumeration", false );
		typesOptions.put( listType, listTypeOptions);
      
           
      stringType = new Type( new Namespace( options.getTreeProperty("type.string.namespace",""), "", new Documentation() ),
                             options.getTreeProperty( "type.string.name", "string"), new Documentation());
		stringTypeOptions = new TypeOptions(stringType, this);
		stringTypeOptions.put("generate", false );
		stringTypeOptions.put("usage", "ByReference" );
		stringTypeOptions.put("GNamespace", stringType.getNamespace().getName() );
		stringTypeOptions.put("GName", stringType.getName() );
		stringTypeOptions.put("GPath", options.getTreeProperty( "type.string.include" , ""));
      stringTypeOptions.put("isEnumeration", false );
		typesOptions.put( stringType, stringTypeOptions);
      nonamespaceOptions.put("string.type", stringTypeOptions);
      
      pointerType = new Type( coreNamespace, "Pointer", new Documentation());
      pointerTypeOptions = new TypeOptions(pointerType, this);
		pointerTypeOptions.put("generate", false );
		pointerTypeOptions.put("usage", "ByReference" );
		pointerTypeOptions.put("GNamespace", pointerType.getNamespace().getName() );
		pointerTypeOptions.put("GName", pointerType.getName() );
		pointerTypeOptions.put("GPath", options.getTreeProperty( "type.pointer.include" , ""));
      pointerTypeOptions.put("isEnumeration", false );
		typesOptions.put( pointerType, pointerTypeOptions);
      
	}
		
	public void generate(String targetNS, Model model, FileGenerateSpecification fc)
	{
		// log.log("Generating for "+fc.templateUri +" "+fc.namePattern );
		
		model.visit(new FreemarkerPrepareModelVisitor(this, fc), Model.VisitOrder.POSTFIX);

		generatedTypes.clear();
		
		if ( fc.filePattern.contains( Pattern.NAMESPACE ) )
		{
			List<Namespace> nss = model.getNamespaces();
			for (Namespace ns : nss )
			{
				if ( fc.filePattern.contains( Pattern.TYPE ) )
				{
					for (Member e : ns.getElements())
					{
                  if ( ns.getUri().equals( e.getNamespace() ))
                     generateFile( model, ns, e.getType(), fc, options );
					}
				}
				else
				{
					generateFile( model, ns, null, fc, options );
				}
			}
		}
		else
		{
			generateFile( model, null, null, fc, options );			
		}
	}
   
   ArrayList<File> nonamespaceFiles;  
   PropertyTree    nonamespaceOptions;  
	
	@SuppressWarnings("unchecked")
	private void generateFile(Model model, Namespace ns, Type t,  FileGenerateSpecification fc, PropertyTree options) 
	{
		TypeOptions  typeOption = null;
      NamespaceOptions nsOptions = null;
      
		PropertyTree datamodel = new PropertyTree();

      if ( ns != null )
      {
         nsOptions = getNamespaceOptions(ns);
         nsOptions.put("files", ns.getFiles());
         datamodel.put("namespace", nsOptions );
      }
      else
      {
         nonamespaceOptions.put( fc.patternName+".GPath", "\"" +fc.filePattern+"\"" );
         datamodel.put("files", nonamespaceFiles );
      }

      datamodel.putAll(nonamespaceOptions);
      
		if ( t != null )
		{
			typeOption = getTypeOptions(t);
			if ( (! ((Boolean)typeOption.get( "generate" ))) || generatedTypes.contains(t) )
				return;
			
			datamodel.put("type", typeOption);
			generatedTypes.add(t);
			for (Map<String,Object> subtypeOption : (List<Map<String,Object>>)typeOption.get("GSubTypes") )
			{
				Type st = (Type)subtypeOption.get( "internaltype");
            if ( ns.getUri().equals( st.getNamespace().getUri() ))
				   generateFile(model, ns, st,  fc, options);
			}
			// log.log("Generate for "+t.getName() );
		}		

		datamodel.put("xsd", model);
		datamodel.put("options", options);
      
      // Add dynamic methods:
		datamodel.put("GetGeneratedPaths", new GetGeneratedPaths(this));
      datamodel.put("TrimPath", new TrimPath(this));
		
		try 
		{
			Template tpl = fmConfiguration.getTemplate(fc.templateUri);
         
         ClassLoader  x = getClass().getClassLoader();
			
			File file = (t != null ) ? resolveFilePattern( fc.filePattern, t ) : resolveFilePattern(fc.filePattern, ns );
			datamodel.put("file", file);
			
			// log.log( "File -> "+file );
			
			File dir = file.getParentFile();
			if ( !dir.exists() )
			{
				if ( !dir.mkdirs())
					throw new IOException("Can't create directory <"+dir.getAbsolutePath()+">");
			}

			FileWriter sw = new FileWriter(file);
			tpl.process(datamodel, sw);
			sw.close();
         if ( ns != null )
            ns.addFile(file);
         else
            nonamespaceFiles.add(file);
			addFile( file.getAbsolutePath() );
		}
		catch (TemplateException | IOException e1) 
		{
			log.log( e1 );
		}
	}
	
}

