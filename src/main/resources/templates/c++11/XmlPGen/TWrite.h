/* Part of XmlPGen
 * Copyright (c) 2013-2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#ifndef XmlPGen_TWrite_h_INCLUDED
#define XmlPGen_TWrite_h_INCLUDED

#include <type_traits>
#include "AbstractWriter.h"

namespace XmlPGen 
{
   /**
    * Template interface for element writers.
    * The functions shall write only the content, not the enclosing tag.
    * So writers can be re-used for any occurens of there type.
    */
   template <class E>
	void write( E const &, AbstractWriter & writer )
   {
      // To get an human readable error instead of 200x "candidate..."
      ::std::static_assert( false, "Missing XmlPGen::TWrite implementation" );
   }

}

#endif
