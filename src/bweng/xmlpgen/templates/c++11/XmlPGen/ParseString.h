/*
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#ifndef XmlPGen_ParseString_h_INCLUDED
#define XmlPGen_ParseString_h_INCLUDED

#include <sstream>

namespace XmlPGen 
{
   template< typename E >
   void ParseString( char const * value, E & m)
   {
      ::std::stringstream s(value);
      s >> m;  
   }     

   template< >
   inline void ParseString( char const * value, ::std::string const & str )
   { 
      str = value;
   }

/* todo
   template< > void ParseString( char const *, double const & );
   template< > void ParseString( char const *, float const &);
   template< > void ParseString( char const *, uint32_t const & );
   template< > void ParseString( char const *, uint64_t const & );
   template< > void ParseString( char const *, int64_t  const & );
   template< > void ParseString( char const *, int32_t const & );
   template< > void ParseString( char const *, int16_t const &);
   template< > void ParseString( char const *, uint16_t const &);
   template< > void ParseString( char const *, int8_t const &);
   template< > void ParseString( char const *, uint8_t const &);
*/
}

#endif