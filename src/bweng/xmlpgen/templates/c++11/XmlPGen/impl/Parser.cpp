/* Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#include "../Parser.h"
#include "../Schema.h"
#include <fstream>

namespace XmlPGen
{
   Parser::Parser( ::std::shared_ptr<Log> aLog )
   : log { aLog }
   , token_map{ nullptr }
   , current_element { 0,0,0 }
   {
     element_stack.reserve(20);
   }

   void Parser::addSchema( ::std::shared_ptr<Schema> const & schema )
   {
     schemata.push_back( schema );
   }

   bool Parser::parse( ::std::shared_ptr< ::std::istream> i, char const * optFileName )
   { 
	  input = i;
     filename = optFileName;
     bool const returnVal = visitDocument();
     input.reset();
     return returnVal;
   }

   bool Parser::parse( char const * fileName )
   {
     return parse( ::std::dynamic_pointer_cast<::std::istream>(::std::make_shared< ::std::fstream >(fileName, ::std::ios_base::in )), fileName );
   }

   Parser::~Parser()
   {
      clean_up();
   }

   void Parser::clean_up()
   {
      while ( !element_stack.empty() );
        leaveNode();
      // Once again to delete also the last one.
      leaveNode();

      current_element.ns = nullptr;
      current_element.element = nullptr;
      current_element.schema = nullptr;
      current_schema.reset();
   }

   int Parser::getToken( char const * p ) const
   {
      if ( p && *p && current_element.schema )
      {
        int idx = 0;
        TokenChar const * token_map = current_element.schema->getTokenMap();
        while( true )
        {
           while( token_map[idx].c != *p && token_map[idx].c != 0 )
              ++idx;
           if ( token_map[idx].c == 0 ) return 0;
           if ( *(++p) == 0 ) return token_map[idx].id;
           idx = token_map[idx].next;
        }
      }
      return 0;
   }

   void Parser::enterNode(char const * ns, char const * name)
   {
      element_stack.push_back( current_element );
      if ( ns != current_element.ns )
      {
        current_element.ns = ns;
        current_element.schema = nullptr;
        for(auto const & schema : schemata)
           if ( schema && ::strcmp(ns,schema->uri)==0 )
           {
              current_element.schema = schema.get();
              break;
           }
      }
      int const name_token = getToken( name );
      if ( current_element.element ) 
        current_element.element = current_element.element->addElement(current_element.schema ? current_element.schema->uri_token:-1,name_token);
      else if ( current_element.schema && current_element.schema->root_token == name_token )
      {
        current_element.element = current_element.schema->createRootHandler();
      }
printf("> %s::%s\n",ns,name);
   }

   void Parser::setAttribute(char const * name, char const * value)
   {
      if ( current_element.element ) 
      {
         TypeHandler * attr_handler = current_element.element->addAttribute(getToken(name));
         if ( attr_handler ) 
         { 
            attr_handler->addContent(value);
            delete attr_handler;
         }
      }
      printf(" attr %s %s\n",name,value);      
   }

   void Parser::setTextContent(char const * value)
   {
      if ( current_element.element ) current_element.element->addContent(value);
      printf(" text %s\n",value);
   }

   void Parser::leaveNode()
   {
      delete current_element.element;
      if ( !element_stack.empty() )
      {
          current_element = element_stack.back();
          element_stack.pop_back();
      }
      else
          current_element.element = 0;
   }
}
