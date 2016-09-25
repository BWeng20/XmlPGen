/* Part of XmlPGen
 * Copyright (c) 2013-2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#include "../Log.h"
#include <cstdio>
#include <cstdarg>
#include <time.h>
#include <iomanip>

namespace XmlPGen 
{
   ::std::string Log::currentTime()
   {
      ::std::string s;
      char buffer[31];

      ::time_t t;
      ::tm ttm;

      ::time(&t);
#ifdef _MSC_VER
      ::localtime_s(&ttm, &t);
#else
      ::localtime_s(&t, &ttm);
#endif

      ::asctime_s(buffer, 30, &ttm);
      buffer[30] = 0;

      s.append(buffer);
      return s;
   }

    void ConsoleLog::info( char const *format, ... )
    {
       printf( "%s INF ", currentTime().c_str() );
       va_list args;
       va_start (args, format);
       ::vprintf(format,args);
       va_end (args);
       printf( "\n" );
    }

    void ConsoleLog::warning( char const *format, ... ) 
    {
       printf("%s WRN ", currentTime().c_str());
       va_list args;
       va_start (args, format);
       ::vprintf(format,args);
       va_end (args);
       printf( "\n" );
    }

    void ConsoleLog::error  ( char const *format, ... )
    {
       printf("%s ERR ", currentTime().c_str());
       va_list args;
       va_start (args, format);
       ::vprintf(format,args);
       va_end (args);
       printf( "\n" );
    }

    StreamLog::StreamLog( ::std::shared_ptr< ::std::ostream > stream )
    : logstream(stream)
    {
    }


    void StreamLog::info( char const *format, ... )
    { 
        if ( logstream )
        {
            va_list args;
            va_start (args, format);        
            ::vsnprintf(buffer,1024,format,args);
            va_end (args);
            (*logstream) << ::std::right << ::std::setw(5) << ::time(nullptr) << " INF " << buffer << ::std::endl ;
        }
    }

    void StreamLog::warning( char const *format, ... )
    { 
        if ( logstream )
        {
            va_list args;
            va_start (args, format);        
            ::vsnprintf(buffer,1024,format,args);
            va_end (args);
            (*logstream) << ::std::right << ::std::setw(5) << ::time(nullptr) << " WRN " << buffer << ::std::endl ;
        }
    }

    void StreamLog::error  ( char const *format, ... ) 
    { 
        if ( logstream )
        {
            va_list args;
            va_start (args, format);        
            ::vsnprintf(buffer,1024,format,args);
            va_end (args);
            (*logstream) << ::std::right << ::std::setw(5) << ::time(nullptr) << " ERR " << buffer << ::std::endl ;
        }
    }

}
