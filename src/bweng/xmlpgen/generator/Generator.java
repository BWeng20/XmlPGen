/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.generator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bweng.xmlpgen.tools.Configuration;
import bweng.xmlpgen.tools.FileGenerateSpecification;
import bweng.xmlpgen.tools.Logger;
import bweng.xmlpgen.xsd.Model;
import bweng.xmlpgen.xsd.Namespace;
import bweng.xmlpgen.xsd.Type;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Generator adapter.
 */
public abstract class Generator 
{
	protected Logger log = Logger.getLogger();
	
	public Generator( String targetDirectory, Configuration cfg ) throws IOException
	{
		this.targetDirectory = targetDirectory;
		this.cfg = cfg;
		this.generatedFiles = new ArrayList<>();
		
		File tf = new File(targetDirectory);
		if ( !tf.exists() )
		{
			if ( !tf.mkdirs())
			{
				throw new IOException( "Can't create target directory <"+targetDirectory+">" );
			}
		}
	}	
	
	protected final void addFile( String relPath )
	{
		if ( relPath.startsWith(targetDirectory) )
		{
			relPath = relPath.substring(0, targetDirectory.length() );
			if ( relPath.startsWith( File.pathSeparator) )
			{
				relPath = relPath.substring(1);		
			}
		}
		relPath.replace( '\\', '/' );
		generatedFiles.add( relPath );
	}

	public final List<String> getFiles()
	{
		return generatedFiles;
	}

	
	public abstract void generate(String targetNS, Model model, FileGenerateSpecification fc );
	
	public static interface Pattern
	{
		public static final String TYPE      = "%type%"; 
		public static final String NAMESPACE = "%namespace%"; 
		public static final String CNAMESPACE= "%cnamespace%"; 
	}
   
   private static final HashMap<Character,String> symbolReplacements = new HashMap();
   private static final HashSet<Character> symbolKeep = new HashSet();
   static 
   {
      symbolReplacements.put('-', "_");
      
      symbolKeep.add('_');
   }
	
	static public String asTypeName( String symbol )
   {
   	return normalizeSymbol( symbol, true );
      
   }

   static public String asSymbol( String symbol )
   {
      return normalizeSymbol(symbol, false);
   }
      
	static public String normalizeSymbol( String symbol, boolean capitalize )
	{
		if (symbol != null)
		{
			int size = symbol.length();
			StringBuilder sb = new StringBuilder( size+10 );
			char cs[] = symbol.toCharArray();
			boolean up = capitalize;
			for (char c : cs)
			{
				if ( (0==sb.length()) ? Character.isJavaIdentifierStart(c) : Character.isJavaIdentifierPart(c) )
				{
					if ( up )
					{
						c = Character.toUpperCase(c);
						up = false;
					}
               sb.append(c);
				}
            else if ( (c>='0') && (c<='9'))
            {              
               // digits at the start  prefix with "_"
               sb.append('_');
               sb.append(c);
      			up = false;
            }
            else
				{
               if ( symbolReplacements.containsKey(c) )
               {
                  sb.append( symbolReplacements.get(c) );
               }
               else if (symbolKeep.contains(c) )
                  sb.append( c );
               else
                  up = 0<sb.length();
				}
  				
			}
			return sb.toString();
		}
		else
			return "";
	}
	
	public File resolveFilePattern( String pattern, Type type )
	{
		Namespace ns = type.getNamespace();
		String r = pattern.replace(Pattern.TYPE, asTypeName(type.getName()))
				.replace(Pattern.NAMESPACE, ns == null ? "" : ns.getName())
				.replace(Pattern.CNAMESPACE, ns == null ? "" : asTypeName(ns.getName()));		
		return new File( targetDirectory+File.separatorChar+r );
	}

	public String resolveIncludePattern( String pattern, Type type )
	{
		String r = pattern.replace(Pattern.TYPE, asTypeName(type.getName()));
		Namespace ns = type.getNamespace();
		if ( ns != null )
			r = r.replace(Pattern.NAMESPACE, ns == null ? "" : ns.getName())
		         .replace(Pattern.CNAMESPACE, ns == null ? "" : asTypeName(ns.getName()));
		return r;
	}

	
	public File resolveFilePattern( String pattern, Namespace ns )
	{
		return new File( targetDirectory+File.separatorChar + 
				( pattern.replace(Pattern.NAMESPACE , ns == null ? "" : ns.getName())
		                 .replace(Pattern.CNAMESPACE, ns == null ? "" : asTypeName(ns.getName())) ));
	}

	public String resolveIncludePattern( String pattern, Namespace ns )
	{
		return pattern.replace(Pattern.NAMESPACE, asTypeName(ns.getName())) ;
	}
	
	public String resolveNamePattern( String pattern, Type type )
	{
		String r = pattern.replace(Pattern.TYPE, asTypeName(type.getName()) );
		Namespace ns = type.getNamespace();
		if ( ns != null )
			r = r.replace(Pattern.NAMESPACE, asTypeName(type.getNamespace().getName()));
		return r;
	}

	public String resolveNamePattern( String pattern, Namespace ns )
	{
		return pattern.replace(Pattern.NAMESPACE, asTypeName(ns.getName()));
	}

	
	protected String targetDirectory;
	protected Configuration cfg;
	protected ArrayList<String> generatedFiles;
}
