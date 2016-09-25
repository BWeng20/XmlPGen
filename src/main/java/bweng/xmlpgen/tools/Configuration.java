/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.tools;

import bweng.xmlpgen.XmlPGen;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public final class Configuration 
{
	private Properties pro;
	private URL        baseURL;
	private Map<Object,Object> specialValues = new HashMap<Object,Object>();
	private Map<String,String> aliases = new HashMap<String,String>();
   private boolean runningFromJar;
	
	public static final String PROPERTY_XSD_ADAPTER = "XsdAdapter";
	public static final String PROPERTY_GENERATOR   = "Generator";
	public static final String PROPERTY_FILES       = "Files";
	
	public Configuration( String cfgFile, String[] additionalArgs ) 
	{
      try
      {
         String protcol = Configuration.class.getResource("Configuration.class").getProtocol();
         runningFromJar = protcol.equals( "jar");
      }
      catch ( Exception e)
      {
         runningFromJar = false;
      }
      
      InputStream is = null;
      String cfgPath="";
      URL url;
      try
      {
         url = XmlPGen.class.getResource("/"+cfgFile);

         if ( url != null)
         {
            String tmp =  url.toString();
            
            int idx = tmp.lastIndexOf('/');
            if (idx > 0)
               tmp = tmp.substring(0,idx+1);
            
            baseURL = new URL(tmp);
            
            is = url.openStream();
            cfgPath = url.toString();
         }

      }
      catch (Exception e)
      {}

      Logger log = Logger.getLogger();
      
      if ( is == null )
      {
         File file = new File( cfgFile );
         try
         {
            is = new FileInputStream( file );
            cfgPath = cfgFile;
            try
            {
               baseURL = file.getParentFile().toURI().toURL();
            }
            catch (MalformedURLException ex)
            {
               log.log(ex);
               log.log("Can't extract base URL from <"+cfgFile+">. ABORT");
               System.exit(-1);
            }
         }
         catch (FileNotFoundException ex)
         {
         }
      }
		pro = new Properties();
		try 
		{
			pro.load( is );
			
			log.log("Base URL <"+baseURL+">");
			log.log("Configuration <"+cfgPath+">");
		} 
		catch (IOException e) 
		{
			log.log( e );
         log.log("Can't read <"+cfgPath+">. ABORT" );
         System.exit(-1);
		}
		// Add some special values that need to be converted.
		specialValues.put("true", Boolean.TRUE );
		specialValues.put("false", Boolean.FALSE );
		
		// Alias map to use for target name spaces.		
		for (String arg : additionalArgs )
		{
			if ( arg.startsWith("-alias:" ))
			{
				int sepIdx = arg.indexOf('=');
				if ( sepIdx > 0 )
				{
					aliases.put( arg.substring( sepIdx+1 ), arg.substring(7, sepIdx ));				
				}
			}		
		}
	}
  
	public URL getBaseURL()
	{
		return baseURL;
	}

   public boolean isRunningFromJar()
   {
      return runningFromJar;
   }
	
	public String getProperty( String key, String defaultVal )
	{
		String val = pro.getProperty(key);
		return ( (val == null) || val.isEmpty() ) ? defaultVal : val;
	}

   public boolean getBooleanProperty( String key, boolean defaultVal )
	{
		String val = pro.getProperty(key);
      return ( val == null ) ? defaultVal : Boolean.valueOf(val);
	}

	/**
	 * Builds up a hierarchical option tree that can be used e.g. by Freemarker templates to easily access options.<br>
	 * E.g. "generator" will collect all properties that starts with "generator." in a hierarchical map.
	 * <pre>
	 *  generator.file1.name =File1.txt
	 *  generator.file1.comment =Something1
	 *  generator.file2.name =File2.txt
	 *  generator.file2.comment =Something2 </pre>
	 *  Will result in
	 * <pre>
	 *  Map 
	 *  { "file1" -> Map 
	 *    {
	 *      name    -> "File1.txt"
	 *      comment -> "Something1"
	 *    }
	 *    "file2" -> Map 
	 *    {
	 *      name    -> "File2.txt"
	 *      comment -> "Something2"
	 *    }
	 *  }
	 * </pre>
	 * In a Freemarker template the values can accessed like this (assuming the map is put as "options" in the data model):
	 * <pre>
	 *  ${options.file1.name}
	 *  ${options.file2.comment}
	 * </pre> 
	 * @param baseKey Base key for the tree. 
	 * @param defaults Default values. Keys must not start with the base-key.
	 * @return Hierarchical map
	 */
	public PropertyTree getPropertyTree( String baseKey, Map<Object,Object> defaults )
	{
		PropertyTree map = new PropertyTree();
		if (defaults != null ) 
			generatePropertyTree("",defaults,map);
		
		generatePropertyTree(baseKey,pro,map);
		return map;		
	}
	
	@SuppressWarnings("unchecked")
	private void generatePropertyTree( String baseKey, Map<Object,Object> properties, PropertyTree map )
	{		
		if ( !baseKey.isEmpty() )
			baseKey = baseKey+'.';
		int baseLen = baseKey.length();
		for ( Entry<Object,Object> e : properties.entrySet() )
		{
			String key = (String)e.getKey();
			if ( key.startsWith(baseKey) )
			{
				String skey = key.substring(baseLen);
            Object val = e.getValue();
				Object sval = specialValues.get( val.toString().toLowerCase() );
            if ( sval != null )
               map.put(skey, sval );
            else
               map.put(skey, val );
			}
		}
	}
	
	public FileGenerateSpecification[] getFileSpecifications( String key )
	{
		String line = pro.getProperty(key);
		if ( null != line )
		{
			String specs[] = line.split(",");
			
			FileGenerateSpecification fspecs[] = new FileGenerateSpecification[specs.length];
			int idx = 0;
			for (String spec : specs)
			{
				fspecs[idx++] = new FileGenerateSpecification( spec.trim() );
				
			}
			return fspecs;
		}
		else
		{
			return new FileGenerateSpecification[0];
		}
	}
	
	public String getNamespaceAlias( String ns )
	{
		return aliases.get( ns );
	}
}
