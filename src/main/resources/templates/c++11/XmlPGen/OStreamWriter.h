/* Part of XmlPGen
 * Copyright (c) 2013-2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#ifndef XmlPGen_OStreamWriter_h_INCLUDED
#define XmlPGen_OStreamWriter_h_INCLUDED

#include "AbstractWriter.h"
#include <fstream> 

namespace XmlPGen 
{
   /**
    * Writer with a ostream as backend.
    */
   class OStreamWriter : public AbstractWriter
   {
    public:
        OStreamWriter( ::std::ostream & aSink, int aFlags );
        virtual void write(char const * , size_t ) override;

    private:
      ::std::ostream & sink_;

   };

}




#endif