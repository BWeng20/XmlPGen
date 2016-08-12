/* Part of XmlPGen
 * Copyright (c) 2013-2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#ifndef XmlPGen_StringWriter_h_INCLUDED
#define XmlPGen_StringWriter_h_INCLUDED

#include "AbstractWriter.h"
#include <sstream> 

namespace XmlPGen 
{
   /**
    * Writer with a string-buffer as backend.
    */
   class StringWriter : public AbstractWriter
   {
    public:
        explicit StringWriter( int aFlags );
        virtual void write(char const * , size_t ) override;

        inline ::std::string getString() const
        {
           return sink_.str();
        }

    private:        
        ::std::ostringstream sink_;

   };

}

#endif