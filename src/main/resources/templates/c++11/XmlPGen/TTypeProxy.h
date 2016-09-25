/* Part of XmlPGen
 * Copyright (c) 2013-2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#ifndef XmlPGen_TElementProxy_h_INCLUDED
#define XmlPGen_TElementProxy_h_INCLUDED

#include "TypeHandler.h"

namespace XmlPGen
{
   template <class X>
   struct TTypeProxy
   {
      static TypeHandler * createHandler(X* o);
      static int const Token;
   };
}

#endif