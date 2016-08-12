/*
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#include "../ToString.h"
#include <stdio.h>

namespace XmlPGen 
{
   template< > ::std::string toString(  double const & v )
   {
        char buffer[20];
        ::snprintf( buffer, 20, "%lf", v );
        buffer[20] = 0;
        return ::std::string(buffer);
   }

   template< > ::std::string toString(  float const & v)
   {
        char buffer[20];
        ::snprintf( buffer, 20, "%f", v );
        buffer[20] = 0;
        return ::std::string(buffer);
   }
   template< > ::std::string toString(  uint32_t const & v)
   {
        char buffer[20];
        ::snprintf( buffer, 20, "%u", v );
        buffer[20] = 0;
        return ::std::string(buffer);
   }
   template< > ::std::string toString(  uint64_t const & v)
   {
        char buffer[20];
        ::snprintf( buffer, 20, "%llu", v );
        buffer[20] = 0;
        return ::std::string(buffer);
   }
   template< > ::std::string toString(  int64_t  const & v)
   {
        char buffer[20];
        ::snprintf( buffer, 20, "%lld", v );
        buffer[20] = 0;
        return ::std::string(buffer);
   }
   template< > ::std::string toString(  int32_t const & v)
   {
        char buffer[20];
        ::snprintf( buffer, 20, "%d", v );
        buffer[20] = 0;
        return ::std::string(buffer);
   }
   template< > ::std::string toString(  int16_t const & v)
   {
        char buffer[20];
        ::snprintf( buffer, 20, "%d", v );
        buffer[20] = 0;
        return ::std::string(buffer);
   }
   template< > ::std::string toString(  uint16_t const & v)
   {
        char buffer[20];
        ::snprintf( buffer, 20, "%u", v );
        buffer[20] = 0;
        return ::std::string(buffer);
   }
   template< > ::std::string toString(  int8_t const & v)
   {
        char buffer[20];
        ::snprintf( buffer, 20, "%d", v );
        buffer[20] = 0;
        return ::std::string(buffer);
   }
   template< > ::std::string toString(  uint8_t const & v)
   {
        char buffer[20];
        ::snprintf( buffer, 20, "%u", v );
        buffer[20] = 0;
        return ::std::string(buffer);
   }

}
