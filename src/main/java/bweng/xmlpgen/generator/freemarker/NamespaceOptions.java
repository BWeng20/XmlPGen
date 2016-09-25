/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.generator.freemarker;

import bweng.xmlpgen.tools.PropertyTree;
import bweng.xmlpgen.xsd.Member;

import bweng.xmlpgen.xsd.Namespace;
import bweng.xmlpgen.xsd.TokenLookupEntry;
import bweng.xmlpgen.xsd.TokenLookupTable;

final class NamespaceOptions extends PropertyTree
{
	public NamespaceOptions (Namespace ns, FreemarkerGenerator gen)
	{
		put("name", ns.getName());
		put("documentation", ns.getDocumentation());
      put("uri", ns.getUri());
      
      TokenLookupTable lookup = ns.getLookup();
      
      Integer uriTokenId = lookup.getToken().get(ns.getUri());
      if ( uriTokenId != null )
      {
         TokenLookupEntry entry = lookup.getTokenEntry( uriTokenId );
         put("uri_token", entry);
      }
      Integer rootTokenId = lookup.getToken().get( ns.getRoot().getName());
      if ( rootTokenId != null )
      {
         TokenLookupEntry entry = lookup.getTokenEntry( rootTokenId );
         put("root_token", entry);
      }

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