/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.generator.freemarker;

import bweng.xmlpgen.tools.PropertyTree;

import bweng.xmlpgen.xsd.Type;

final class TypeOptions extends PropertyTree
{
	private static final long serialVersionUID = 3656580607766502704L;

	public TypeOptions (Type t, FreemarkerGenerator gen)
	{
		put("internaltype",t);
		put("namespace", t.getNamespace());
		put("name", t.getName());
		put("documentation", t.getDocumentation());
      put("atomic",t.isAtomic());
      put("mixed",t.isMixed());
	}
}