/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.generator.freemarker;

import bweng.xmlpgen.tools.PropertyTree;
import bweng.xmlpgen.xsd.Member;

import bweng.xmlpgen.xsd.Namespace;

final class NamespaceOptions extends PropertyTree
{
	public NamespaceOptions (Namespace ns, FreemarkerGenerator gen)
	{
		put("name", ns.getName());
		put("documentation", ns.getDocumentation());
      put("uri", ns.getUri());
      put("elements", ns.getElements());;
      Member root = ns.getRoot();
      if ( root != null )
      {
         MemberOptions memOpt = new MemberOptions(root, gen);
         for ( String key : memOpt.keySet() )
            put("root."+key, memOpt.get(key) );
         TypeOptions typeOptions = gen.getTypeOptions(root.getType());
         put("root.type", typeOptions );
      }
      put("lookup", ns.getLookup());
	}
}