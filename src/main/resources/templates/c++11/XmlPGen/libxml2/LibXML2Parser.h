/* Part of XmlPGen
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

#include  <istream>
#include  <memory>
#include  "XmlPGen/Parser.h"
#include  "libxml/parser.h"
#include  "XmlPGen/TypeHandler.h"

struct _xmlNode;

namespace XmlPGen 
{
   /**
    * Parser implementation for LibXML2.
    */
	class LibXML2Parser : public ::XmlPGen::Parser
	{
	public:

      /**
       * Parse a XML document from some input-stream.
       */
		explicit LibXML2Parser(::std::shared_ptr<Log> log = ::std::shared_ptr<Log>(0) );

	protected:
      virtual bool visitDocument() override;

      void visitNode(::xmlNodePtr node);

      ::xmlDocPtr doc;
	};
}
