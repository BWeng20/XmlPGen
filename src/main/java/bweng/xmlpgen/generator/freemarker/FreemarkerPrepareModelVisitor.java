/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.generator.freemarker;

import bweng.xmlpgen.generator.Generator;
import bweng.xmlpgen.generator.Generator.Pattern;
import static bweng.xmlpgen.generator.Generator.asTypeName;
import static bweng.xmlpgen.generator.Generator.normalizeSymbol;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bweng.xmlpgen.tools.FileGenerateSpecification;
import bweng.xmlpgen.tools.Logger;
import bweng.xmlpgen.xsd.Attribute;
import bweng.xmlpgen.xsd.EnumValue;
import bweng.xmlpgen.xsd.Member;
import bweng.xmlpgen.xsd.ModelVisitor;
import bweng.xmlpgen.xsd.Namespace;
import bweng.xmlpgen.xsd.Occurrence;
import bweng.xmlpgen.xsd.Type;

/**
 * Model visitor that prepares the freemarker data model.
 * @see FreemarkerGenerator#generate(java.lang.String, bweng.xmlpgen.xsd.Model, bweng.xmlpgen.tools.FileGenerateSpecification) 
 */
class FreemarkerPrepareModelVisitor implements ModelVisitor
{
	Logger logger;
	
	private final FreemarkerGenerator freemarkerGenerator;
	
	FileGenerateSpecification fc;
	ArrayList<Set<Type>> levelStack = new ArrayList<Set<Type>>();
	
	FreemarkerPrepareModelVisitor(FreemarkerGenerator freemarkerGenerator, FileGenerateSpecification fc )
	{
		this.freemarkerGenerator = freemarkerGenerator;
		this.fc = fc;
      this.logger = Logger.getLogger();
	}

	@Override
	public void visitMember(int level, Member e)
	{
		
	}

	@Override
	public void visitAttribute(int level, Attribute a)
	{
	}
   
   void addStandardTypeOptions( Map<String,Object> typeOption, String typeName )
   {
      typeOption.put( "GNamespace"  , freemarkerGenerator.options.getTreeProperty( "type."+typeName+".namespace", ""));
      typeOption.put( "GPath"       , freemarkerGenerator.options.getTreeProperty( "type."+typeName+".include"  , "")); 
      typeOption.put( "pattern.tostring", freemarkerGenerator.options.getTreeProperty( "type."+typeName+".pattern.tostring", ""));

      String gname = freemarkerGenerator.options.getTreeProperty( "type."+typeName+".name"     , "");
      typeOption.put( "GName"       , gname);
      typeOption.put( fc.patternName+".GName", gname );
   }

	@Override
	public void visitType(int level, Type t)
	{
      boolean needsPointer = false;
      
		while ( levelStack.size() < (level+1) )
			levelStack.add(new HashSet<Type>());
		Set<Type> st =  levelStack.get(level);
		st.add(t);
		
		TypeOptions typeOption = this.freemarkerGenerator.getTypeOptions(t);
			
		List<AttributeOptions> attList = new ArrayList<AttributeOptions>(t.getAttributes().size());
		for (Attribute attr : t.getAttributes() )
		{
			AttributeOptions attOpt = this.freemarkerGenerator.getAttributeOptions(attr);
			attList.add( attOpt );
		}
		typeOption.put("attributes", attList );
		
		List<MemberOptions> memList = new ArrayList<MemberOptions>(t.getMembers().size());
		for (Member mem : t.getMembers() )
		{
			MemberOptions memOpt = new MemberOptions(mem, freemarkerGenerator);
			memList.add( memOpt );
		}
		typeOption.put("members", memList);

		typeOption.put("isEnumeration", t.isEnum() );
		
		if ( t.isEnum() )
		{
			List<EnumerationOptions> enList = new ArrayList<EnumerationOptions>(t.getEnums().size());
         int maxLength = 0;
			for (EnumValue en : t.getEnums())
			{
				EnumerationOptions enumOpt = new EnumerationOptions(en, freemarkerGenerator);
				enList.add( enumOpt );
            if ( maxLength < en.getValue().length() )
                maxLength = en.getValue().length();
			}
			typeOption.put("enumeration.values", enList);
         // Put also max-length for easy aligmemnts.
			typeOption.put("enumeration.maxlength", maxLength);
		}
		
		List<Map<String,Object>> subTypes = new ArrayList<Map<String,Object>>();

		// Add all from deeper sub nodes. So we will get all used types. 
		for (int i=levelStack.size()-1 ; i>level ; --i)
		{
			for (Type ti : levelStack.get(i))
				subTypes.add(this.freemarkerGenerator.typesOptions.get(ti));
		}

		for (Member m : t.getMembers())
		{
			int max = m.getOccurence().getMax();
			if (  max > 1 || max == Occurrence.UNBOUND.getMax())
			{
				subTypes.add( this.freemarkerGenerator.listTypeOptions );
				break;
			}
		}
      if ( t.isMixed() )
			subTypes.add( this.freemarkerGenerator.stringTypeOptions );
      
      if ( t.isEnum() )
      {
         typeOption.put( "generate", true );
			typeOption.put( "usage", "ByValue" );
  			typeOption.put( fc.patternName+".GPath", "\"" + freemarkerGenerator.resolveIncludePattern(fc.filePattern, t ) +"\"" );
         typeOption.put( "GNamespace", t.getNamespace() != null ? t.getNamespace().getName() : "");
         
         String gname = this.freemarkerGenerator.resolveNamePattern( fc.namePattern, t );
			typeOption.put( "GName", gname );
         // Put the generated name also in pattern scope for cross-pattern use.
         typeOption.put( fc.patternName+".GName", gname );
      }
      else if (t.getVariety() == bweng.xmlpgen.xsd.Type.VARIETY.SIMPLE_ATOMIC)
		{
			typeOption.put( "GNamespace", "" );
			typeOption.put( "generate", false );
			typeOption.put( "usage", "ByValue" );

			switch (t.getBaseType())
			{
			case NONE: break;
			case FLOAT32:addStandardTypeOptions( typeOption, "float") ; break;
			case FLOAT64:addStandardTypeOptions( typeOption, "double") ; break;
			case INT8:   addStandardTypeOptions( typeOption, "int8") ; break;
			case INT16:  addStandardTypeOptions( typeOption, "int16"); break;
			case INT32:  addStandardTypeOptions( typeOption, "int32"); break;
			case INT64:  addStandardTypeOptions( typeOption, "int64")  ; break;

			case UINT8:  addStandardTypeOptions( typeOption, "uint8");  break;
			case UINT16: addStandardTypeOptions( typeOption, "uint16"); break;
			case UINT32: addStandardTypeOptions( typeOption, "uint32"); break;
			case UINT64: addStandardTypeOptions( typeOption, "uint64"); break;
				
			case BOOLEAN:addStandardTypeOptions( typeOption, "bool");  break;
			case DATE   :
			case TIME   :
			case DATETIME:
			case STRING: addStandardTypeOptions(  typeOption, "string" );
				typeOption.put( "usage", "ByReference" );
   			break;
			case BINARY: 
			   addStandardTypeOptions( typeOption, "binary") ; 
				typeOption.put( "usage", "ByReference" );
            break;
			}
			
		}
      else if (t.is( "XMLSchema", "anyType") )
      {
         addStandardTypeOptions( typeOption, "any");
			typeOption.put( "generate", false );
         needsPointer = true;
      }
		else
		{
			typeOption.put( "generate", !t.getName().equals("*") );
         needsPointer = true;

         typeOption.put( fc.patternName+".GPath", "\"" + freemarkerGenerator.resolveIncludePattern(fc.filePattern, t ) +"\"" );
			typeOption.put( "GNamespace", t.getNamespace() != null ? t.getNamespace().getName() : "");
         
         String gname = this.freemarkerGenerator.resolveNamePattern( fc.namePattern, t );
			typeOption.put( "GName", gname );
         // Put the generated name also in pattern scope for cross-pattern use.
         typeOption.put( fc.patternName+".GName", gname );
		}

      if ( needsPointer)
      {
         typeOption.put( "usage", "ByPointer" );
         subTypes.add( this.freemarkerGenerator.pointerTypeOptions );
      }
		typeOption.put( "GSubTypes", subTypes );
      
		for (int i=levelStack.size()-1 ; i>level ; --i)
		{
			levelStack.remove(i);
		}
	}

	@Override
	public void visitNamespace(int level, Namespace ns)
	{
      List<Member> roots = ns.getElements();
      if ( roots.size() != 1)
         logger.log("Schema "+ns.getUri()+" has "+roots.size()+" elements (expected is one)");
 
      NamespaceOptions nsOption = this.freemarkerGenerator.getNamespaceOptions(ns);
     
      nsOption.put( fc.patternName+".GPath", "\"" + freemarkerGenerator.resolveIncludePattern(fc.filePattern, ns ) +"\"" );
      String GName;
      if ( fc.namePattern.contains( Pattern.NAMESPACE ) && !fc.namePattern.contains( Pattern.TYPE ) )
        GName = fc.namePattern.replace(Pattern.NAMESPACE, ns.getName());
      else
        GName = ns.getName();
      nsOption.put( "GName", GName );
      nsOption.put( fc.patternName+".GName", GName );
	}
	
}