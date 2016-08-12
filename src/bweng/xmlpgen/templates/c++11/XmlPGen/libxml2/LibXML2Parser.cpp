/* Part of XmlPGen
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#include "LibXML2Parser.h"
#include <fstream>

namespace XmlPGen 
{

   LibXML2Parser::LibXML2Parser(::std::shared_ptr<Log> log  )
   : Parsere( log )
   {
   }

   void LibXML2Parser::visitNode(::xmlNodePtr node)
   {
      if (node)
      {
         enterNode((char const*)(node->ns ? node->ns->href : nullptr), (char const*)node->name);
         xmlChar * text = ::xmlNodeListGetString(doc, node->children, 1 );
         if ( text != nullptr )
         {
            if (::strlen((char const*)text) > 0)
               setTextContent((char const*)text );
            ::xmlFree( text );
         }
         for (::xmlAttrPtr attr = node->properties; attr != nullptr; attr = attr->next)
            if (attr->children != nullptr)
               setAttribute((char const*)attr->name, (char const*)attr->children->content);
         for (::xmlNodePtr child = node->children; child != nullptr; child = child->next)
            if ( !::xmlNodeIsText(child) )
                visitNode(child);
         leaveNode();
      }
   }

   bool LibXML2Parser::visitDocument()
	{
      if ( doc ) xmlFreeDoc( doc );
      doc = nullptr;

		if (input && input->good())
		{
			char const * fopt = filename.empty() ? nullptr : filename.c_str();

			::std::streamsize const buffer_capacity = 1024;
			char buffer[buffer_capacity];
			::std::streamsize read = input->read(buffer, buffer_capacity).gcount();
         if (!input->bad())
         {
            ::xmlParserCtxtPtr ctxt = ::xmlCreatePushParserCtxt(NULL, NULL, buffer, static_cast<int>(read), fopt);
            if (ctxt == nullptr)
            {
               return false;
            }
            ::xmlCtxtUseOptions(ctxt, XML_PARSE_NOBLANKS );            
            do
            {
               read = input->read(buffer, buffer_capacity).gcount();
               if (read > 0)
                  ::xmlParseChunk(ctxt, buffer, static_cast<int>(read), 0);
            } while (!(input->bad() || input->eof()));
            ::xmlParseChunk(ctxt, buffer, 0, 1);
            doc = ctxt->myDoc;
            xmlFreeParserCtxt(ctxt);
         }
         if ( doc != nullptr )
         { 

            for (::xmlNodePtr node = doc->children; node != nullptr; node = node->next)
               visitNode(node);
            xmlFreeDoc(doc);
            return true;
			}
		}
		return false;
	}
}
