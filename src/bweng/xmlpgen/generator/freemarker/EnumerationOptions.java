/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.generator.freemarker;

import java.util.HashMap;

import bweng.xmlpgen.generator.Generator;
import bweng.xmlpgen.xsd.EnumValue;

public final class EnumerationOptions extends HashMap<String, Object>
{
	private static final long serialVersionUID = 3521331667435546857L;

	EnumerationOptions( EnumValue en, FreemarkerGenerator gen)
	{
		put("GTypedValue", Generator.asSymbol( en.getType().getName().toUpperCase() +"_"+ en.getValue()) );
		put("GEValue", Generator.asSymbol( "E "+en.getValue()) ); // space will force the first character upper-case
		put("GValue", Generator.asSymbol( en.getValue()) );
		put("value", en.getValue() );
  		put("documentation", en.getDocumentation());
	}
}
