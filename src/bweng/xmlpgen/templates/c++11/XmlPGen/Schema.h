/* Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#ifndef XmlPGen_Schema_h_INCLUDED
#define XmlPGen_Schema_h_INCLUDED
#include "TypeHandler.h"
 
namespace XmlPGen
{
   class Schema
   {
   public:
      char const * const uri;

      virtual ::XmlPGen::TypeHandler * getRootHandler() const = 0;
      virtual ::XmlPGen::TokenChar const * getTokenMap() const = 0;

      virtual ~Schema() = default;

   protected:
      explicit Schema( char const * const aUri )
      : uri{ aUri }
      {}

      Schema(const Schema &) = delete;
   };

}

#endif