/* Part of XmlPGen
 * Copyright (c) 2013-2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#include "../AbstractWriter.h"

namespace XmlPGen 
{

    AbstractWriter::AbstractWriter(int aFlags)
       : flags{ aFlags }
       , indent{1}
       , state{EState::CONTENT}
       , aliasCount{0}
       , stackIdx{ 0 }
    {
      setIndentChar( 3, ' ' );
    }

    AbstractWriter::~AbstractWriter() 
    {
    }

	 void AbstractWriter::setIndentChar(int const aIndent, char const aIC)
	 {
      indent = ( aIndent > indent_max ) ? indent_max : aIndent ;
      int i{indent};
      while( i>0 ) ic_[--i] = aIC;
    }


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
          write(" ",1);
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
          case EState::ELEMENT: 
             write(">",1); 
             nl();
             break;
          case EState::END_ELEMENT:
             nl();
             state = EState::ELEMENT;
             break;
          case EState::CONTENT: 
             nl();
             state = EState::ELEMENT;
             break;
          default: return;
          }
          write("<",1);
          if (!currentAlias.empty())
             write(currentAlias.c_str(), currentAlias.size());
          write(name);
          stack[stackIdx++].element = name;			
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
       case EState::ELEMENT: 
          write(">",1); 
          state = EState::CONTENT; 
          break;
       case EState::END_ELEMENT:
          state = EState::CONTENT;
          break;
       case EState::CONTENT: 
          break;
       default: return;
       }
       write_expanded(text);
    }

    void AbstractWriter::cdata(char const * c)
    {
       switch (state)
       {
       case EState::ELEMENT:
          write(">",1); 
          state = EState::CONTENT; 
          break;
       case EState::END_ELEMENT:
          nl();
          state = EState::CONTENT;
          break;
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
          WriterStackEntry & stackEntry =  stack[stackIdx];

          switch (state)
          {
          case EState::ELEMENT:
             write("/>", 2); 
             state = EState::END_ELEMENT;
             break;
          case EState::END_ELEMENT:
             nl();
             // Fallthrough
          case EState::CONTENT:
             write("</", 2); 
             write(stackEntry.previousAlias);
             write(stackEntry.element); 
             write(">", 1); 
             state = EState::END_ELEMENT;
             break;
          default: break;
          }

          for (auto const & schema : stackEntry.addedSchemata)
             schema2alias.erase(schema);
          stackEntry.addedSchemata.clear();
          currentSchema = stackEntry.previousSchema;
          currentAlias = stackEntry.previousAlias;

          stackEntry.previousSchema.reset();
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
        int id(stackIdx);
        while ((--id) >= 0) write(ic_, indent);
       }
    }

    void AbstractWriter::write_expanded(char const * c)
    {
       char ch;
       while (0 != (ch = *(c++)))
       {
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

    void AbstractWriter::header()
    {
       write( "<?xml version='1.0' encoding='UTF-8' standalone='no'?>");
    }

    void AbstractWriter::start(char const * name, ::std::vector<::std::shared_ptr<Schema> > const & schemata, ::std::shared_ptr<Schema> const & schema)
    {
       if (stack_max > stackIdx)
       {
          switch (state)
          {
          case EState::ELEMENT: 
             write(">", 1); 
             nl(); 
             break;
          case EState::CONTENT: 
             state = EState::ELEMENT; 
             break;
          case EState::END_ELEMENT:
             nl();
             state = EState::ELEMENT;
             break;
          default: return;
          }
          nl();
          write("<", 1);

          WriterStackEntry & stackEntry = stack[stackIdx];
          stackEntry.previousSchema = currentSchema;
          stackEntry.previousAlias  = currentAlias;
          stackEntry.element = name;
          stackEntry.addedSchemata.clear();

          if (schema && schema != currentSchema)
          {
             auto it = schema2alias.find(schema);
             if (it == schema2alias.end())
             {
                if (schema2alias.empty())
                {
                   // First (root) schema gets default.
                   currentAlias.clear();
                }
                else
                {
                   currentAlias = "n";
                   currentAlias.append(::std::to_string(++aliasCount));
                   currentAlias.append(":");
                }
                stackEntry.addedSchemata.push_back( schema );
                schema2alias.insert(::std::pair<::std::shared_ptr<Schema>, ::std::string>(schema, currentAlias));
             }
             else
             {
                currentAlias = it->second;
             }
             currentSchema = schema;
          }

          write(currentAlias);
          write(name);

          for (auto const & s : schemata)
          {
             if (s != schema)
             {
                auto it = schema2alias.find(schema);
                if (it == schema2alias.end())
                {
                   ::std::string alias( "n" );
                   alias.append( ::std::to_string(++aliasCount) );
                   alias.append(":");

                   schema2alias.insert(::std::pair<::std::shared_ptr<Schema>, ::std::string>(schema, alias));
                   stackEntry.addedSchemata.push_back( s );
                }
             }
          }

          for ( auto & s : stackEntry.addedSchemata )
            write( s ); 

          ++stackIdx;
       }
       else
       {
          state = EState::ERROR;
       }
    }

    void AbstractWriter::write( ::std::shared_ptr<Schema> schema )
    {
        auto it = schema2alias.find( schema );
        if ( it != schema2alias.end() )
        {
           if ( it->second.empty() )
           {
             write(" xmlns='",8);
           }
           else
           {
             write(" xmlns:",7);
             write( it->second );
             write("='",2);
           }
           write( schema->uri );
           write("'",1);
        }
    }

    void AbstractWriter::start(char const * name, ::std::shared_ptr<Schema> const & schema)
    { 
       start( name, ::std::vector<::std::shared_ptr<Schema> >(), schema );
    }

}
