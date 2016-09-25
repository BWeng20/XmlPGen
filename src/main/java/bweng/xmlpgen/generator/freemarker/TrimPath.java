/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.generator.freemarker;

import java.util.List;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import java.io.File;

/**
 * Freemarker method, called from templates to trim a path 
 */
class TrimPath implements TemplateMethodModelEx 
{  
   public TrimPath( FreemarkerGenerator generator )
   {
      this.freemarkerGenerator = generator;
   }
   
   final FreemarkerGenerator freemarkerGenerator;

   final static String sameDir = "."+File.separator;
   
   String cleanName( String t )
   {
      if (t.startsWith("\"")) t = t.substring(1);
      if (t.endsWith("\"")) t = t.substring(0,t.length()-1);
      if (t.startsWith(sameDir))
         t = t.substring( sameDir.length() );
      
      return t;
   }
   
   /**
    * Expects two arguments: (1) Current file (2) include path
    */
	@SuppressWarnings("rawtypes")
	public Object exec( List args) throws TemplateModelException
    {
    	if ( args.size() > 1 )
    	{
    	   File currentFile = new File(cleanName(args.get(0).toString()));
         File includeFile = new File(cleanName(args.get(1).toString()));
         
         String parent = currentFile.getParent();
         String includeParent = includeFile.getParent();
         if ( parent == null ||
              parent.equals( includeParent ))
            return "\""+includeFile.getName()+"\"";
         else
            return args.get(1);
    	}
    	else
    	{
    		throw new TemplateModelException("Missing argument");
    	}
    }
}