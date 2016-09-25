/*
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#ifndef XmlPGen_AbstractWriter_h_INCLUDED
#define XmlPGen_AbstractWriter_h_INCLUDED

#include "Schema.h"
#include <map>
#include <vector>

namespace XmlPGen 
{
   int const NEWLINES = 1;
   int const INDENT   = 2;
   int const PRETTY   = 3;

   struct WriterStackEntry
   {
      char const * element;
      ::std::vector<::std::shared_ptr<Schema> > addedSchemata;
      ::std::shared_ptr<Schema> previousSchema;
      ::std::string previousAlias;
   };

	class AbstractWriter 
	{
	public:
		enum class EState
		{
			ELEMENT,
			CONTENT,
         END_ELEMENT,
			ERROR
		};

		AbstractWriter(int aFlags);
		~AbstractWriter();

		void setIndentChar(int const aIndent, char const aIC);

      virtual void header( );

      // Starts a root element with xmlns attributes for each namespace.
      void start(char const * name, ::std::vector<::std::shared_ptr<Schema> > const & schemata, ::std::shared_ptr<Schema> const & schema );
      // Starts a element from a different schema.
      void start(char const * name, ::std::shared_ptr<Schema> const & schema );

      void start(char const * name);
      void attribute(char const * name, char const * value);
		void attribute_none_empty(char const * name, char const * value);
		void content(char const * text);
		void cdata(char const * c);
		void end();

      void element(char const * name, char const * value);
		void element_none_empty(char const * name, char const * value);
		void end_all();
		void nl();

	protected:
      // Needs to be implemented 
		virtual void write(char const * c, size_t len) = 0;

      void write(char const * );
      void write_expanded(char const * c);
      void write( ::std::shared_ptr<Schema> schema ); 
      inline void write(::std::string const & s) { if (!s.empty()) write(s.c_str(), s.size()); }

      char const * getSchemaAlias(::std::shared_ptr<Schema> const & schema);


	private:

      int              flags;
		int              indent;
		static int const indent_max = 20;
		char             ic_[indent_max];

		EState           state;

      ::std::map< ::std::shared_ptr<Schema>, ::std::string > schema2alias;

      ::std::shared_ptr<Schema> currentSchema;
      ::std::string             currentAlias;
      int                       aliasCount;


		static int const stack_max = 200;
      WriterStackEntry stack[stack_max];
		int              stackIdx;
	};
}

#endif