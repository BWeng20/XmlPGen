/* Part of XmlPGen
* Copyright (c) 2013-2016 Bernd Wengenroth
* Licensed under the MIT License.
* See LICENSE file for details.
*/
#include "../TTypeProxy.h"
#include "../Any.h"

/*
* Some type-proxies for base types.
*/

namespace XmlPGen
{

   class std_string_Handler : public ::XmlPGen::TypeHandler
   {
   public:
      explicit std_string_Handler(::std::string * obj)
         : obj_{ obj }
      {}

      ::XmlPGen::TypeHandler * addAttribute(int token) override
      {
         return nullptr;
      }

      ::XmlPGen::TypeHandler * addElement(int namespace_token, int element_token) override
      {
         return nullptr;
      }

      void addContent(char const * text) override
      {
         if ( obj_ ) *obj_ = text;
      }

      ::std::string * obj_;
   };

   class Any_Handler : public ::XmlPGen::TypeHandler
   {
   public:
      explicit Any_Handler(Any * obj)
         : obj_{ obj }
      {}

      ::XmlPGen::TypeHandler * addAttribute(int token) override
      {
         return nullptr;
      }

      ::XmlPGen::TypeHandler * addElement(int namespace_token, int element_token) override
      {
         // TODO
         return nullptr;
      }

      void addContent(char const * text) override
      {
      }

      Any* obj_;
   };


   template<>
   TypeHandler * TTypeProxy< ::std::string >::createHandler(::std::string * o)
   {
      return o ? new std_string_Handler(o) : nullptr;
   }

   template<>
   TypeHandler * TTypeProxy< Any >::createHandler(Any * o)
   {
      return o ? new Any_Handler(o) : nullptr;
   }

}
