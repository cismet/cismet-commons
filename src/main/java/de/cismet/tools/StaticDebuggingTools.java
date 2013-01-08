/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
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
 * StaticDebuggingTools.
 *
 * @author   cschmidt
 * @version  $Revision$, $Date$
 */
public class StaticDebuggingTools {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of StaticDebuggingTools.
     */
    public StaticDebuggingTools() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Tests whether the specified Filename exists in the User's Home Directory or not.
     *
     * @param   filename  Filename of the searched File
     *
     * @return  True, if the Filename is contained in the Home Directory
     */
    public static boolean checkHomeForFile(final String filename) {
        try {
            // Merke dir den Pfad zum Homeverzeichnis
            final String home = System.getProperty("user.home"); // NOI18N
//            System.out.println(home);
            // Merke dir das Trennzeichen zwischen Pfad und Dateinamen
            final String fileSep = System.getProperty("file.separator"); // NOI18N
// System.out.println(fileSep);

            final File f = new File(home + fileSep + filename);

            if (f.exists() && f.isFile()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
