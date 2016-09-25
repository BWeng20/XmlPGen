/* Part of XmlPGen
 * Copyright (c) 2013-2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#ifndef XmlPGen_Parser_h_INCLUDED
#define XmlPGen_Parser_h_INCLUDED

#include "Log.h"
#include "Namespace.h"
#include "TypeHandler.h"
#include "TokenMap.h"
#include <vector>

namespace XmlPGen
{
    class Schema;

    class Parser
    {
    public:

      explicit Parser( ::std::shared_ptr<Log> );

      void addSchema( ::std::shared_ptr<Schema> const & );

      /**
       * parse a XML document.
       */
      bool parse( ::std::shared_ptr< ::std::istream> i, char const * optFileName );

      /**
       * parse a XML document from file.
       */
      bool parse( char const * fileName );

      int getToken( char const * p ) const;

      virtual ~Parser();

    protected:

        virtual bool visitDocument() = 0;

        /**
         * Enters a node.
         * @param ns   Pointer to namespace name. The parser assumes that the same pointer always points to the same name.
         * @param name Name of the element.
         */
    	  void enterNode( char const * ns, char const * name );
    	  void setAttribute( char const * name, char const * value );
    	  void setTextContent( char const * value );
    	  void leaveNode( );

        ::std::shared_ptr< ::std::istream> input;
        ::std::string filename;

    private:
       void clean_up();

       ::std::shared_ptr<Log> log;

       struct stack_element
       {
          TypeHandler * element;
          char const  * ns;
          Schema      * schema;
       };

       stack_element current_element;
       ::std::shared_ptr<Schema> current_schema;

       ::std::vector<stack_element> element_stack;
       ::std::vector<::std::shared_ptr<Schema> > schemata;

       TokenChar const * const token_map;

    };
}

#endif