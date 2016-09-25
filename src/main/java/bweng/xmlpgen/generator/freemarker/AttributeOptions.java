/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.generator.freemarker;

import java.util.HashMap;

import bweng.xmlpgen.generator.Generator;
import bweng.xmlpgen.xsd.Attribute;

final class AttributeOptions extends HashMap<String, Object>
{
	private static final long serialVersionUID = 6053129773839963554L;

	public AttributeOptions (Attribute a, FreemarkerGenerator gen)
	{
		put("GName", Generator.asSymbol(a.getName()));
		put("name", a.getName());
      put("requiered", a.getRequiered() );
		put("documentation", a.getDocumentation());
		put("type", gen.getTypeOptions(a.getType()));

	}	
}