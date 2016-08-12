/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.generator.freemarker;


import bweng.xmlpgen.generator.Generator;
import bweng.xmlpgen.tools.PropertyTree;
import bweng.xmlpgen.xsd.Member;

final class MemberOptions extends PropertyTree
{
	public MemberOptions (Member m, FreemarkerGenerator gen)
	{
		put("name", m.getName());
		put("documentation", m.getDocumentation());
		put("type", gen.getTypeOptions(m.getType()));
		put("GName", Generator.asSymbol(m.getName()));
		put("occurence", m.getOccurence());
	}
}