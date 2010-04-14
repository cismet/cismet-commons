/*
 * StringTools.java
 *
 * Created on 4. Mai 2004, 14:05
 */

package de.cismet.tools;

/**
 *
 * @author  schlob
 */
public final class StringTools {
    
    /** Creates a new instance of StringTools */
   private StringTools() {
    }
    
    public static final String deleteWhitespaces(String string)
    {
        if(string==null)
            return string;
        
        String clean = "";  // NOI18N
        
        char[] chars = string.toCharArray();
        
        for(int i=0;i<chars.length;i++)
        {
            if(!Character.isWhitespace(chars[i]))
                clean+=chars[i];
                
        }
        
        
    return clean;
    
    
    }
    
    
    
}
