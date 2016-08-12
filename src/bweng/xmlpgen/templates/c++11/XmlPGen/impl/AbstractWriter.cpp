/* Part of XmlPGen
 * Copyright (c) 2013-2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#include "../AbstractWriter.h"

namespace XmlPGen 
{

    AbstractWriter::AbstractWriter(int aFlags)
       : state(EState::CONTENT)
       , stackIdx(0)
       , flags(aFlags)
       , indent(1)
    {
      ic_[0] = ' ';
      ic_[1] = 0;
    }

    AbstractWriter::~AbstractWriter() {}

	void AbstractWriter::write(char const *c)
	{
		const char * lc = c;
		while (*lc) ++lc;
		write(c, lc - c);
	}

    void AbstractWriter::attribute(char const * name, char const * value)
    {
       if ((EState::ELEMENT == state) && name && value )
       {
          write(name);
          write("=\"",2);
          write_expanded(value);
          write("\"",1);
       }
    }

    void AbstractWriter::attribute_none_empty(char const * name, char const * value)
    {
       if (value && *value != 0)
          attribute(name, value);
    }

    void AbstractWriter::start(char const * name)
    {
       if (stack_max > stackIdx)
       {
          switch (state)
          {
          case EState::ELEMENT: write(">",1); nl(); break;
          case EState::CONTENT: state = EState::ELEMENT; break;
          default: return;
          }
          write("<",1);
          write(name);
          stack[stackIdx++] = name;			
       }
       else
       {
          state = EState::ERROR;
       }
    }

    void AbstractWriter::content(char const * text)
    {
       switch (state)
       {
       case EState::ELEMENT: write(">",1); state = EState::CONTENT; break;
       case EState::CONTENT: break;
       default: return;
       }
       write_expanded(text);
    }

    void AbstractWriter::cdata(char const * c)
    {
       switch (state)
       {
       case EState::ELEMENT: write(">",1); state =  EState::CONTENT; break;
       case EState::CONTENT: break;
       default: return;
       }
       write( "<![CDATA[", 9);

	   char const * lc = c;
       while (*c)
       {
          if ((c[0] == ']') &&
              (c[1] == ']') &&
              (c[2] == '>')    )
          {
			  if (lc != c) write(lc, c - lc);
			  // Escape end marker
			  write("]]]]><![CDATA[>", 15);
              c += 3;
			  lc = c;
		  }
       }
	   if (lc != c) write(lc, c - lc);
       write("]]>", 3);
    }

    void AbstractWriter::end()
    {
       if (stackIdx > 0)
       {
          --stackIdx;
          switch (state)
          {
          case EState::ELEMENT: write("/>", 2); state = EState::CONTENT; break;
          case EState::CONTENT: write("</", 2); write(stack[stackIdx]); write("/>", 2); break;
          default: break;
          }
       }
    }

    void AbstractWriter::element(char const * name, char const * value)
    {
        start( name );
        content( value );
        end();
    }

    void AbstractWriter::element_none_empty(char const * name, char const * value)
    {
        if (value && *value != 0)
          element(name, value);
    }


    void AbstractWriter::end_all()
    {
       while (stackIdx > 0) end();
    }

    void AbstractWriter::nl()
    {
       if ( 0 != (flags & NEWLINES) )
          write("\n",1);
       if ( 0 != (flags & INDENT) )
       {
        int id(indent);
        while ((--id) >= 0) write(ic_,2);
       }
    }

    void AbstractWriter::write_expanded(char const * c)
    {
       char ch;
       while (true)
       {
          ch = *(c++);
          if (ch == '""') write("&quot;", 6);
          else if (ch == '<') write("&lt;", 4);
          else if (ch == '>') write("&gt;", 4);
          else if (ch == '&') write("&amp;", 5);
          else
          {
             write(&ch,1);
          }
       }
    }

}
