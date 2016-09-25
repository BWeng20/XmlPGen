/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.generator.freemarker;

import bweng.xmlpgen.xsd.Member;
import freemarker.ext.beans.StringModel;
import freemarker.template.DefaultMapAdapter;
import java.util.HashSet;
import java.util.List;

import freemarker.template.SimpleObjectWrapper;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;

/**
 * Freemarker method, called from templates to get the list of includes.
 */
class GetGeneratedPaths implements TemplateMethodModelEx 
{  
   public GetGeneratedPaths( FreemarkerGenerator generator )
   {
      this.freemarkerGenerator = generator;
   }
   
   final FreemarkerGenerator freemarkerGenerator;

   final private void scan( Object mo, String gpath, HashSet<String> includes  ) throws TemplateModelException
   {
      Object moo = null;
      TypeOptions typeOption = null;

      if ( mo instanceof StringModel )
         moo = ((StringModel)mo).getWrappedObject();
      else if ( mo instanceof DefaultMapAdapter)
         moo = ((DefaultMapAdapter)mo).getWrappedObject();
      else
         throw new TemplateModelException("List Element type "+mo.getClass().getSimpleName()+" unsupported");

      if ( moo != null )
      {
         if ( moo instanceof Member )
            typeOption = freemarkerGenerator.getTypeOptions(((Member)moo).getType());
         else if ( moo instanceof TypeOptions )
            typeOption = (TypeOptions)moo;
         else
            throw new TemplateModelException("Wrapped List Element type "+moo.getClass().getSimpleName()+" unsupported");
      }
      if ( typeOption != null )
      {
         if ( "ByPointer".equals( typeOption.get("usage") ))
         {
            String ptrInclude = (String)freemarkerGenerator.options.get("pointer.include");
            if ( ptrInclude != null )
               includes.add( ptrInclude );
         }
         String incS = (String)typeOption.get(gpath);

         if ( incS == null )
         {
            String subkey = gpath;
            int si = subkey.indexOf('.');
            while( si > 0 && incS == null )
            {
               subkey = subkey.substring( si+1 );
               incS = (String)typeOption.get(subkey);
               si = subkey.indexOf('.');
            }
         }
         if ( incS != null && !incS.isEmpty())
         {
            for (String inc : incS.split(":"))
            {
               if ( !inc.isEmpty() )
                  includes.add( inc );
            }
         }
      }
   }   
   
	@SuppressWarnings("rawtypes")
	public Object exec( List args) throws TemplateModelException
    {
    	if ( args.size() > 1 )
    	{
     		HashSet<String> includes = new HashSet<String>();
    	   final String gpath = args.get(0).toString()+".GPath";
      	Object o = args.get(1);
    		if ( o instanceof TemplateSequenceModel)
         {
            TemplateSequenceModel tsm = (TemplateSequenceModel)o;
    			for (int i = 0; i < tsm.size() ; ++i )
    			{
               scan( tsm.get(i), gpath, includes );
    			}
        		return new SimpleSequence(includes,  new SimpleObjectWrapper(FreemarkerGenerator.freemarkerVersion));
         }
/* Old Freemarker Version         
         else if ( o instanceof SimpleSequence)
    		{
        		SimpleSequence s = (SimpleSequence)o;
    			for (int i = 0; i < s.size() ; ++i )
    			{
               scan( s.get(i), gpath, includes );
    			}
        		return new SimpleSequence(includes,  new SimpleObjectWrapper(FreemarkerGenerator.freemarkerVersion));
    		}
         else if ( o instanceof SimpleHash)
    		{
            scan( o, gpath, includes );
        		return new SimpleSequence(includes,  new SimpleObjectWrapper(FreemarkerGenerator.freemarkerVersion));
         }
*/
         else if ( o instanceof DefaultMapAdapter)
    		{
            scan( o, gpath, includes );
        		return new SimpleSequence(includes,  new SimpleObjectWrapper(FreemarkerGenerator.freemarkerVersion));
         }
    		else
    		{
    			throw new TemplateModelException("Argument must be List of Type Hashs");
    		}
    	}
    	else
    	{
    		throw new TemplateModelException("Missing type-list argument");
    	}
    }
}