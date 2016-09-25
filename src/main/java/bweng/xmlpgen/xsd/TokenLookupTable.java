/* Copyright (c) 2015 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package bweng.xmlpgen.xsd;

import bweng.xmlpgen.generator.Generator;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 */
public class TokenLookupTable 
{
   public ArrayList<TokenLookupEntry> getChars()
   {
      return chars;
   }

   public HashMap<String,Integer> getToken()
   {
      return token;
   }
   
   public TokenLookupEntry getTokenEntry(int token)
   {
      return token2Entry.get(token);
   }
   
   
   ArrayList<TokenLookupEntry> chars;
   HashMap<String,Integer> token;
   HashMap<Integer,TokenLookupEntry> token2Entry;

   // Statistics for values in this map, for possible choose of datatypes.
   public int next_max;
   public int next_min;
   public int id_min;
   public int id_max;

   public TokenLookupTable()
   {
      chars = new ArrayList<>();
      token = new HashMap<>();      
      next_max = Integer.MIN_VALUE;
      next_min = Integer.MAX_VALUE;
      id_max  = Integer.MIN_VALUE;
      id_min  = Integer.MAX_VALUE;
   }

   /**
    * Scans char map and sets resulting name for each leaf.
    * @param sb
    * @param idx 
    */
   private void scanToken( StringBuilder sb, int idx )
   {
      int currNameIdx = sb.length();
      sb.append(" ");
      TokenLookupEntry tle;      
      do
      {
         tle = chars.get(idx);
         sb.setCharAt(currNameIdx, tle.c );
         if ( tle.id != 0 )
            tle.name = sb.toString();
         if ( tle.next > 0 )
            scanToken( sb, tle.next );
         ++idx;
      }
      while( tle.c != 0 );
      sb.setLength( currNameIdx );
      
   }
   
   /**
    * Calculates the tokens for each path.
    */
   public void setGeneratorNames()
   {
      token2Entry = new HashMap<Integer,TokenLookupEntry>();
      
      StringBuilder sb = new StringBuilder();
      scanToken( sb, 0 );
      for (int idx =0 ; idx < chars.size() ; ++idx)
      {
         if ( chars.get(idx).name != null )
         {
            TokenLookupEntry token = chars.get(idx);
            token.name = Generator.asSymbol( chars.get(idx).name );
            
            token2Entry.put( token.getId(), token );
         }
      }
   }
}

