/* Part of XmlPGen
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#ifndef XmlPGen_Log_h_INCLUDED
#define XmlPGen_Log_h_INCLUDED

#ifdef __GNUC__
    #define FORMAT_ATTRIBUTE(F,A) __attribute__ ((format (printf, F, A)))
#else
	#define FORMAT_ATTRIBUTE(F,A)
#endif

#include <ostream>
#include <memory>
#include <string>

namespace XmlPGen
{
    class Log 
    {
    public:
        virtual void info   ( char const *format, ... ) = 0 FORMAT_ATTRIBUTE( 1, 2 );
        virtual void warning( char const *format, ... ) = 0 FORMAT_ATTRIBUTE( 1, 2 );
        virtual void error  ( char const *format, ... ) = 0 FORMAT_ATTRIBUTE( 1, 2 );
    protected:
       ::std::string currentTime();
    };

    class ConsoleLog : public Log
    {
    public:
        virtual void info   ( char const *format, ... ) FORMAT_ATTRIBUTE( 1, 2 );
        virtual void warning( char const *format, ... ) FORMAT_ATTRIBUTE( 1, 2 );
        virtual void error  ( char const *format, ... ) FORMAT_ATTRIBUTE( 1, 2 );
    };

    class StreamLog : public Log
    {
    public:
        explicit StreamLog( ::std::shared_ptr< ::std::ostream > );
        virtual void info   ( char const *format, ... ) FORMAT_ATTRIBUTE( 1, 2 );
        virtual void warning( char const *format, ... ) FORMAT_ATTRIBUTE( 1, 2 );
        virtual void error  ( char const *format, ... ) FORMAT_ATTRIBUTE( 1, 2 );
    private:
        ::std::shared_ptr< std::ostream > logstream;
        char buffer[1024];
    };

}

#endif