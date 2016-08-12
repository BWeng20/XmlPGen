/* Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#ifndef XmlPGen_Any_h_INCLUDED
#define XmlPGen_Any_h_INCLUDED

#include <memory>
#include <string>
#include "Namespace.h"

namespace XmlPGen
{
    class Any
    {
    public:
        ::std::shared_ptr<Namespace> nameSpace;
        ::std::string                elementName;

        ::std::shared_ptr<void>      element;
    };

    class AbstractWriter;

    void write(Any const &, AbstractWriter &);
}

#endif