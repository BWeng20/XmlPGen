/* Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#ifndef XmlPGen_Schema_h_INCLUDED
#define XmlPGen_Schema_h_INCLUDED
#include "TypeHandler.h"
#include "TokenMap.h"
 
namespace XmlPGen
{
   class Schema
   {
   public:
      char const * const uri;
      int const uri_token;
      int const root_token;

      virtual ::XmlPGen::TypeHandler * createRootHandler() const = 0;
      virtual ::XmlPGen::TokenChar const * getTokenMap() const = 0;

      virtual ~Schema() = default;

   protected:
      explicit Schema( char const * const aUri, int aUriToken, int aRootToken )
      : uri{ aUri }
      , uri_token{aUriToken }
      , root_token{ aRootToken }
      {}

      Schema(const Schema &) = delete;
   };

}

#endif