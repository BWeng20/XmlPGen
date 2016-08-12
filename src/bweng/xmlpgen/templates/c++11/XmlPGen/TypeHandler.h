/* Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#ifndef XmlPGen_TypeHandler_h_INCLUDED
#define XmlPGen_TypeHandler_h_INCLUDED

#include "Log.h"
#include "Namespace.h"
#include <functional>
 
namespace XmlPGen
{
   class TypeHandler
   {
   public:
      virtual void addContent(char const *) = 0;
      virtual TypeHandler * addAttribute(int token) = 0;
      virtual TypeHandler * addElement(int namespace_token, int element_token) = 0;

      virtual ~TypeHandler()
   };

   class FunctionContentHandler : public TypeHandler
   {
   public:
      FunctionContentHandler( ::std::function< char const *> * f );
      ~FunctionContentHandler();

      virtual void addContent(char const *) override;

      // does nothing
      virtual TypeHandler * addAttribute(int token) override;
      // does nothing
      virtual TypeHandler * addElement(int token) override;
   private:
      ::std::function< char const *> * func_;
   };

}

#endif