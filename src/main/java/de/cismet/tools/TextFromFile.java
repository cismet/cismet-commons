/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools;
import java.io.*;

import java.util.*;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class TextFromFile {

    //~ Instance fields --------------------------------------------------------

    private final transient org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

    private byte[] data;

    //~ Constructors -----------------------------------------------------------

    /**
     * /////////////////////////////////////////
     */
    public TextFromFile() {
        data = new byte[0];
    }

    /**
     * ///////////////////////////////////////////
     *
     * @param  filepath  DOCUMENT ME!
     */
    public TextFromFile(final String filepath) {
        try {
            final File inFile;
            final InputStream stream;

            inFile = new File(filepath);

            data = new byte[(int)inFile.length()];
            stream = new FileInputStream(inFile);

            // read the file into data
            final int bytesRead = stream.read(data, 0, (int)inFile.length());

            if (bytesRead == -1) {                                  // error occured during readingprocess
                throw new Exception("read failed");                 // NOI18N
            } else if (bytesRead != (int)inFile.length()) {
                throw new Exception("Information probably faulty"); // NOI18N
            }

            stream.close();
        } catch (Exception e) {
            logger.error(e);
            data = new byte[0];
        }
    } // end constructor

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getText() {
        return new String(data);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Vector getWordVector() {
        final Vector words = new Vector(10, 10);

        final String txt = getText();

        final StringTokenizer split = new StringTokenizer(txt, ","); // NOI18N

        while (split.hasMoreTokens()) {
            final String word = split.nextToken();

            words.add(word);
        }

        return words;
    }

    // ---------------------------------------------------------------
}
