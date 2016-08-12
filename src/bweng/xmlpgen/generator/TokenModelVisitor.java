/**
 * Copyright (c) 2016 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.generator;

import bweng.xmlpgen.xsd.TokenLookupTableBuilder;
import bweng.xmlpgen.xsd.*;

public class TokenModelVisitor implements ModelVisitor
{
   int nextTokenId = 0;
   public TokenLookupTableBuilder builder = new TokenLookupTableBuilder();
   
   void addToken( String token )
   {
      if( !builder.hasToken(token))
      {
         builder.addToken( token, ++nextTokenId);
      }
   }
   
	@Override
	public void visitMember(int level, Member e)
	{
      addToken( e.getName() );
	}

	@Override
	public void visitAttribute(int level, Attribute a)
	{
      addToken( a.getName() );
	}

	@Override
	public void visitType(int level, Type t)
	{
	}

	@Override
	public void visitNamespace(int level, Namespace ns)
	{
      addToken( ns.getUri() );
	}

}
