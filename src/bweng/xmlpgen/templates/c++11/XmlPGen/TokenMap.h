/* Part of XmlPGen
 * Copyright (c) 2013-2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
#ifndef XmlPGen_TokenMap_h_INCLUDED
#define XmlPGen_TokenMap_h_INCLUDED

namespace XmlPGen
{
	struct TokenChar
	{
		char c;
		int  next;
		int  id;
	};

	typedef TokenChar TokenMap[];
}

#endif
