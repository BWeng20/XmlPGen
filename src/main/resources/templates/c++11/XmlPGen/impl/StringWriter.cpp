/*
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#include "../StringWriter.h"

namespace XmlPGen 
{
    StringWriter::StringWriter( int aFlags )
    : AbstractWriter( aFlags )
    , sink_()
    {
    }

    void StringWriter::write(char const * c, size_t len)
    {
      sink_.write( c, len );
    }
}
