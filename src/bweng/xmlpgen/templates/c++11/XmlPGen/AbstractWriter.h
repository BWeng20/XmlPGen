/*
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#ifndef XmlPGen_AbstractWriter_h_INCLUDED
#define XmlPGen_AbstractWriter_h_INCLUDED

namespace XmlPGen 
{
   int const NEWLINES = 1;
   int const INDENT   = 2;
   int const PRETTY   = 3;

	class AbstractWriter 
	{
	public:
		enum class EState
		{
			ELEMENT,
			CONTENT,
			ERROR
		};

		AbstractWriter(int aFlags);
		~AbstractWriter();

		inline void setIndentChar(int const aIndent, char const aIC)
		{
			indent = aIndent;
			ic_[0] = aIC;
		}

		void attribute(char const * name, char const * value);
		void attribute_none_empty(char const * name, char const * value);
		void start(char const * name);
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

	private:

      int              flags;
		int              indent;
		char             ic_[2];

		EState           state;

		static int const stack_max = 200;
		char const *     stack[stack_max];
		int              stackIdx;
	};
}

#endif