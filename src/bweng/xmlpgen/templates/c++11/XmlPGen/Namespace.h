/* Part of XmlPGen
 * Copyright (c) 2013-2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#ifndef XmlPGen_Namespace_h_INCLUDED
#define XmlPGen_Namespace_h_INCLUDED

#include <string>
#include <set>

namespace XmlPGen
{
    class Namespace
    {
    public:
        /** Namespace name. E.g. set by xmlns */
        ::std::string schema;
        /** List of assigned aliases */
        ::std::set< ::std::string > aliases;
    };
}

#endif