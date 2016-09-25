/*
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#include "../OStreamWriter.h"

namespace XmlPGen 
{
    OStreamWriter::OStreamWriter( ::std::ostream & aSink, int aFlags )
    : AbstractWriter( aFlags )
    , sink_( aSink )
    {
    }

    void OStreamWriter::write(char const * c, size_t len)
    {
      sink_.write( c, len );
    }
}
