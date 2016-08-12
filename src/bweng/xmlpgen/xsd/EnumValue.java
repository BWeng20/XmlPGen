/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.xsd;

public final class EnumValue
{
	Type type;
	String value;
   Documentation documentation;

   public Documentation getDocumentation()
   {
      return documentation;
   }
	
	public Type getType()
	{
		return type;
	}
	public String getValue()
	{
		return value;
	}
}
