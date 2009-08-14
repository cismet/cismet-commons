/*
 * StaticDebuggingTools.java
 *
 * Created on 7. November 2007, 14:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.tools;

import java.io.File;

/**
 *
 *
 * @author cschmidt
 */
public class StaticDebuggingTools {
    
    /**
     * Creates a new instance of StaticDebuggingTools
     */
    public StaticDebuggingTools() {
        
    }
    
    /*
     *�berpr�ft ob eine Datei mit dem �bergebenen Dateiname im Home-Verzeichnis des Users existiert.
     *@param filename Name der gesuchten Datei.
     */
    public static boolean checkHomeForFile(String filename){
        try {
            //Merke dir den Pfad zum Homeverzeichnis
            String home = System.getProperty("user.home");
//            System.out.println(home);
            //Merke dir das Trennzeichen zwischen Pfad und Dateinamen
            String fileSep = System.getProperty("file.separator");
//            System.out.println(fileSep);
            
            File f = new File(home+fileSep+filename);
            
            if(f.exists()&& f.isFile()){
                return true;
            }
            else{
                return false;
            }
        } 
        catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        
        
    }
    
}
