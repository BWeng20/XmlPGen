/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.tools;

import java.util.HashMap;
import java.util.Map;

/**
 *  Map with automatic splitting of keys so that freemarker can directly work with this.
 */
public class PropertyTree extends HashMap<String, Object>
{
	private static final long serialVersionUID = 9116223822329211114L;
  
   private static Logger logger = Logger.getLogger(); 
	
	@SuppressWarnings("unchecked")
	public String getTreeProperty( String key, String def )
	{
      Object val = get( key );
      return ( val != null )? val.toString() : def;
	}
   
   @Override
	public Object get( Object key )
	{
		Object val = null;
		if ( key != null )
		{
			String keys[] = ((String)key).split("\\.");
         if ( keys.length>0)
         {
            val = super.get(keys[0]);
            if ( keys.length > 1)
            {
               if ( val instanceof Map<?,?> )
               {
                  Map<String, Object> map = (Map<String, Object>)val;
                  for (int ki=1 ; ki<keys.length ; ++ki)
                  {
                     val = map.get(keys[ki]);
                     if ( val instanceof Map)
                        map = (Map<String, Object>)val;
                     else
                     { 
                        if ( ki < (keys.length-1) )
                           val = null;
                        break;
                     }
                  }
               }
            }
         }
		}     
		return val;
	}
   
	@Override
	public Object put(String k, Object v)
	{
		if ( k != null )
		{
			String keys[] = ((String)k).split("\\.");
         if ( keys.length > 1)
         {
            Map<String, Object> map = this;
            Object val = null;
            for (int ki=0 ; ki < (keys.length-1) ; ++ki)
            {
               val = map.get(keys[ki]);
               if ( val instanceof Map)
                  map = (Map<String, Object>)val;
               else if ( val == null )
               {
                  Map<String, Object> keymap = new HashMap<>();
                  map.put( keys[ki], keymap);
                  map = keymap;
               }
               else
               {
                  logger.log("Failed to set '"+k+"'/'"+v+"': Can't extent key '"+keys[ki]+"' at index "+ki );
                  return null;
               }
            }
            return map.put(keys[keys.length-1], v);
         }
		}
      return super.put(k, v);
	}
}
