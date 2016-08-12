/*
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#ifndef XmlPGen_ToString_h_INCLUDED
#define XmlPGen_ToString_h_INCLUDED

#include <sstream>

namespace XmlPGen 
{
   template< typename E >
   ::std::string toString( E const & val)
   {
      ::std::stringstream s;
      s << val;
      return s.str();
   }     

   template< >
   inline ::std::string toString( ::std::string const & v )
   { 
      return v;
   }

   template< > ::std::string toString(  double const & );
   template< > ::std::string toString(  float const &);
   template< > ::std::string toString(  uint32_t const & );
   template< > ::std::string toString(  uint64_t const & );
   template< > ::std::string toString(  int64_t  const & );
   template< > ::std::string toString(  int32_t const & );
   template< > ::std::string toString(  int16_t const &);
   template< > ::std::string toString(  uint16_t const &);
   template< > ::std::string toString(  int8_t const &);
   template< > ::std::string toString(  uint8_t const &);

}

#endif