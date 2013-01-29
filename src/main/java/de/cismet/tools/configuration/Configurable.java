/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools.configuration;

import org.jdom.Element;

/**
 * Configurable Interface.
 *
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
public interface Configurable {

    //~ Methods ----------------------------------------------------------------

    /**
     * Configures the Local configuration.xml Always used after masterConfigure.
     *
     * @param  parent  JDOM root
     */
    void configure(Element parent);
    /**
     * Configures the Server configuration.xml.
     *
     * @param  parent  JDOM root
     */
    void masterConfigure(Element parent);
    /**
     * Gets the JDOM Root for this Configurable.
     *
     * @return  Configuration
     *
     * @throws  NoWriteError  throws NoWriteError if anything went wrong
     */
    Element getConfiguration() throws NoWriteError;
}
