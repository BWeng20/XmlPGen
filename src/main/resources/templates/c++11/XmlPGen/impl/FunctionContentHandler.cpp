/* Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

#include "../TypeHandler.h"

namespace XmlPGen
{
    TypeHandler::~TypeHandler()
    {}

    FunctionContentHandler::FunctionContentHandler( ::std::function< void(char const *) > * f )
    : func_(f)
    {
    }

    FunctionContentHandler::~FunctionContentHandler()
    {
      delete func_;
    }

    void FunctionContentHandler::addContent(char const *value) 
    {
      if ( func_ && (*func_) ) (*func_)( value );
    }

    TypeHandler * FunctionContentHandler::addAttribute(int )  
    {
      return nullptr;
    }

    TypeHandler * FunctionContentHandler::addElement(int , int ) 
    {
      return nullptr;
    }
}
