/* Copyright (c) 2015 Bernd Wengenroth
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package bweng.xmlpgen.xsd;

public class TokenLookupEntry 
{
   public char getChar()
   {
      return c;
   }

   public int getNext()
   {
      return next;
   }
   
   public int getId()
   {
      return id;
   }

   /**
    * Get the generated name of this token.
    * This name can be used for symbols. 
    * Spaces and all illegal characters are removed.
    * @return Token or null for entries without token.
    */
   public String getTokenName()
   {
      return name;
   }
   
   char c;
   int next;
   int id;
   String name;

   public TokenLookupEntry()
   {
      c = 0;
      next = 0;
      id = 0;
      name = null;
   }

}
