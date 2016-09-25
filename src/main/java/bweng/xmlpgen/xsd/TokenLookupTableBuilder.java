/* Copyright (c) 2015 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */
package bweng.xmlpgen.xsd;

import bweng.xmlpgen.tools.Logger;
import bweng.xmlpgen.xsd.TokenLookupTable;
import bweng.xmlpgen.xsd.TokenLookupEntry;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Builder to create a Token-Lookup-Table from a number of token.
 * Usage:
 *   Add as many token as you like via
 *     builder.addToken( "hello", 1 );
 *     ...
 *     builder.addToken( "world", 2 );
 *   Then finalize the table:
 *     TokenLookupTable table = builder.finalizeLookupTable();
 */
public class TokenLookupTableBuilder 
{
   public TokenLookupTableBuilder()
   {
      this.root = new TokenChar();
   }
   
   public boolean hasToken( String token)
   {
      return tokenMap.containsKey(token);
   }
   
   public void addToken( String token, int id)
   {
      tokenMap.put(token, id);
      root.append( token, id );
   }

   /**
    * Finalize the lookup and create a table.
    * After this call this builder is resetted and can be re-used.
    */
   public TokenLookupTable finalizeLookup( )
   {
      TokenLookupTable table = new TokenLookupTable();
      table.token = tokenMap;

      root.addToLookup( table );

      // Calculate statistics
      for ( TokenLookupEntry c : table.chars )
      {
         if ( c.next > table.next_max ) table.next_max = c.next;
         if ( c.next < table.next_min ) table.next_min = c.next;
         
         if ( c.id != 0 )
         {
            if ( c.id > table.id_max ) table.id_max = c.id;
            if ( c.id < table.id_min ) table.id_min = c.id;
         }
      }         
      
      // Reset tree
      root =  new TokenChar();
      tokenMap = new HashMap<>();
      
      table.setGeneratorNames();
      return table;
   }

   private HashMap<String,Integer> tokenMap = new HashMap<>();
   private TokenChar root;
}


/**
 * Tree node that represents a character.
 * If a token ends at this character, the ID is added to "ids" member.
 * "weight" gives the number of tokens that pass throuth this node and
 * is used for sorting of sub-nodes - to optimize parsing.
 * 
 * @author Bernd Wengenroth
 */
class TokenChar implements Comparable
{
   /**
    * Appends the remainiung part of a remainingChars to this sub-tree
    * 
    * @param remainingChars Contains the remaining characters that follow this node.
    * @param id             ID of this token.
    */
   void append( String remainingChars, int id )
   {
      ++weight;
      if ( remainingChars.isEmpty() )
      {
         if (this.id != 0 )
            Logger.getLogger().log("Token-Id reassigned (old "+this.id+" new "+id+")" );
         this.id =id;
      }
      else
      { 
         TokenChar nextTc = null;
         char c = remainingChars.charAt(0);
         for (TokenChar tc:subs)
            if ( tc.c == c )
            {
               nextTc = tc;
               break;
            }
         if ( nextTc == null)
         {
            nextTc = new TokenChar();
            nextTc.c = c;
            subs.add( nextTc );
         }
         nextTc.append(remainingChars.substring(1), id);
      }
   }

   /**
    * After all tokens to parse are added to the tree, a lookup-map is created.
    * This method adds the sub-tree to the map
    */
   void addToLookup( TokenLookupTable lookup )
   {       
      java.util.Collections.sort(subs);
      
      int startIndex = lookup.chars.size();
      for ( TokenChar tc : subs )
      {
         TokenLookupEntry tle = new TokenLookupEntry();
         tle.c = tc.c;
         tle.next = 0;
         tle.id = tc.id;
         lookup.chars.add( tle );
      }
      // Endmarker
      lookup.chars.add( new TokenLookupEntry() );
      
      // Add sub-trees and adjust "next"
      for ( TokenChar tc : subs )
      {
         if ( tc.subs.size() > 0)
         {
            lookup.chars.get(startIndex).next = lookup.chars.size();
            tc.addToLookup(lookup);
         }
         startIndex++;
      }
   }
   
  
   // Character represented by this node.
   char c;
   // IDs of any token that ends at this character.
   int id;  
   ArrayList<TokenChar> subs;
   
   int weight;

   TokenChar()
   {
      id = 0;
      this.subs = new ArrayList<>();
      this.weight = 0;
   }

   /**
    * Results in a order "hight weight first"
    * "Returns a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object."
    */
   @Override
   public int compareTo(Object t)
   {
      return ((TokenChar)t).weight - weight;
   }
}
