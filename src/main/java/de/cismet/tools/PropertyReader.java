/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.tools;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

/**
 * Property Reader.
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class PropertyReader {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(PropertyReader.class);

    //~ Instance fields --------------------------------------------------------

    private final String filename;
    private final Properties properties;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PropertyReader object.
     *
     * @param   filename  filename
     *
     * @throws  IllegalArgumentException  If filename is <code>Null</code>
     */
    public PropertyReader(final String filename) {
        if ((filename == null) || (filename.length() < 1)) {
            throw new IllegalArgumentException();
        }
        this.filename = filename;
        properties = new Properties();
        InputStream is = null;
        try {
            is = new BufferedInputStream(getClass().getResourceAsStream(filename));
            properties.load(is);
        } catch (IOException ex) {
            log.error(ex, ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    log.warn(ex, ex);
                }
            }
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Gets the Property.
     *
     * @param   key  key
     *
     * @return  the <code>Property</code> of the <code>properties</code>
     */
    public final String getProperty(final String key) {
        return properties.getProperty(key);
    }

    /**
     * Gets the Filename.
     *
     * @return  <code>filename</code>
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Gets the Internal Propeties.
     *
     * @return  <code>properties</code>
     */
    public Properties getInternalProperties() {
        return properties;
    }
}
