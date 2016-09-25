/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import bweng.xmlpgen.adapter.XsdAdapter;
import bweng.xmlpgen.generator.Generator;
import bweng.xmlpgen.generator.TokenModelVisitor;
import bweng.xmlpgen.tools.Configuration;
import bweng.xmlpgen.tools.FileGenerateSpecification;
import bweng.xmlpgen.tools.Logger;
import bweng.xmlpgen.xsd.LogModelVisitor;
import bweng.xmlpgen.xsd.Model;
import bweng.xmlpgen.xsd.Namespace;
import java.util.List;


public class XmlPGen {
	
	Logger logger = Logger.getLogger(); 
	
	public XmlPGen( String args[] ) 
	{
		String uri             = args[0];
		String targetDirectory = args[1]; 
		String config          = args.length>2 ? args[2] : "templates/c++11.ini";

		Configuration cfg = new Configuration(config, Arrays.copyOfRange(args, 2, args.length));
	
		String adapterClass = cfg.getProperty(Configuration.PROPERTY_XSD_ADAPTER, "de.bwe.adapter.XercesAdapter");
		XsdAdapter adapter = null;

		logger.log("Instantiate XSD Adapter "+adapterClass);
      
      try 
		{
			Class<?> adpClazz = Class.forName(adapterClass);
			Constructor<?> ctor = adpClazz.getConstructor(bweng.xmlpgen.tools.Configuration.class); 
			adapter = (XsdAdapter)ctor.newInstance(cfg);
		} 
		catch (InstantiationException | IllegalAccessException | ClassNotFoundException 
				| NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) 
		{
			logger.log( e );
			return;
		}
		Model model = new Model();

		String targetNS = null; 
		int nsSep = uri.indexOf('#');
		if ( nsSep >= 0 )
		{
			targetNS = uri.substring( 0, nsSep+1 );
			uri = uri.substring( nsSep+1 );
		}
		adapter.loadModel(model, uri);
			
      if ( cfg.getBooleanProperty("log.model",false) )
      {
         logger.log("======================================================");
         logger.log("Model");
   		LogModelVisitor v = new LogModelVisitor();
      	model.visit(v, Model.VisitOrder.PREFIX );
         logger.log("======================================================");
      }
      
      {
         List<Namespace> namespaces = model.getNamespaces();
         for ( Namespace ns : namespaces)
         {
            TokenModelVisitor tmv = new TokenModelVisitor();
            ns.visit(0, tmv, Model.VisitOrder.PREFIX);
            ns.setLookup( tmv.builder.finalizeLookup() );           
         }
      }
           
		String generatorClass = cfg.getProperty(Configuration.PROPERTY_GENERATOR, "de.bwe.generator.FreemarkerGenerator");
		Generator gen = null;
		logger.log("Instantiate Generator "+generatorClass+"...");
		try 
		{
			Class<?> gebClazz = Class.forName(generatorClass);
			Constructor<?> ctor = gebClazz.getConstructor(String.class,bweng.xmlpgen.tools.Configuration.class); 
			gen = (Generator)ctor.newInstance(targetDirectory,cfg);
		} 
		catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) 
		{
			logger.log( e );
			return;
		}

      logger.log( "Start Generator..." );
		
		FileGenerateSpecification[]  files = cfg.getFileSpecifications(Configuration.PROPERTY_FILES);
		for (FileGenerateSpecification fc : files)
		{
			gen.generate( targetNS, model, fc );
		}
      
      logger.log( "Generator finished" );

      if ( cfg.getBooleanProperty("log.files",false) )
      {
         logger.log("======================================================");
         logger.log("Generated Files");
		   List<String> flist = gen.getFiles();
		   for (String fl : flist )
		   	logger.log( fl );
         logger.log("======================================================");
      }
      
			
	}

	public static void usage()
	{
		System.out.println("XmlPGen [name#]<schema.xsd> <target directory> [<config.ini>] {-alias:<uri>=<name>]}");
	}
	
	public static void main(String[] args) 
	{
		
		if ( args.length < 2 )
		{
			usage();
			return;			
		}
		
		new XmlPGen( args );	
	}	
	
	
}
