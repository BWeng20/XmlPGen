/*
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#ifndef XmlPGen_IsSet_h_INCLUDED
#define XmlPGen_IsSet_h_INCLUDED

#include <type_traits>
#include <string>
#include <vector>
#include <memory>

namespace XmlPGen 
{
   template< typename E >
   class IsSet
   {
      public:
         static bool check( E )
         {
            // To get an human readable error instead of 200x "candidate..."
            static_assert(false, "Missing XmlPGen::isSet implementation");
         }
   };

   template <>
   class IsSet<::std::string const &>{ public: inline static bool check(::std::string const & v) { return !v.empty(); } };

   template <>
   class IsSet<::std::string> { public: inline static bool check(::std::string v) { return !v.empty(); } };

   template < typename T >
   class IsSet<::std::vector<T> const &> { public: static bool check(::std::vector<T> const & v) { return v.size() != 0; } };

   template < typename T >
   class IsSet<::std::shared_ptr<T> const &> { public: static bool check(::std::shared_ptr<T> const & v) { return 0 != v.get(); } };

   template < > class IsSet<double>   { public: static bool check(double  ) { return true; } };
   template < > class IsSet<uint32_t> { public: static bool check(uint32_t) { return true; } };
   template < > class IsSet<uint64_t> { public: static bool check(uint64_t) { return true; } };
   template < > class IsSet< int64_t> { public: static bool check( int64_t) { return true; } };
   template < > class IsSet<int32_t > { public: static bool check(int32_t ) { return true; } };

   template < > class IsSet<char> { public: static bool check(char v) { return true; }  };

   template < typename E > bool isSet(E v) { return IsSet<E>::check(v);  }

}
#endif