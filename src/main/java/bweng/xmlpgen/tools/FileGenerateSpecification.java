/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.tools;

public final class FileGenerateSpecification 
{
   // Identified the file pattern. To be used to glue properties for header & source together.
	public String patternName; 
	public String filePattern;
	public String namePattern;
	public String templateUri;

	FileGenerateSpecification( String spec )
	{
		if ( spec != null )
		{
			String lines[] = spec.split(":");
			if ( lines.length > 2)
			{
				filePattern = lines[0].trim();
				namePattern = lines[1].trim();
				templateUri = lines[2].trim();
            
            java.util.regex.Pattern p = java.util.regex.Pattern.compile("(\\w+)\\.\\w+");
            java.util.regex.Matcher matcher = p.matcher(templateUri);
            if (matcher.find())
               patternName = matcher.group(1);
            else
               patternName = "";
			}
		}
	}
}
