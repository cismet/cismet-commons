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

    /**
     * This method is used, if the component should not be configured, but merged with the given configuration. If this
     * method is not implemented, the default implementation will do nothing
     *
     * @param  parent  DOCUMENT ME!
     * @param  merge   DOCUMENT ME!
     */
    default void configure(final Element parent, final boolean merge) {
        // do nothing
    }

    /**
     * This method is used, if the component should not be configured, but merged with the given configuration. If this
     * method is not implemented, the default implementation will do nothing
     *
     * @param  parent  DOCUMENT ME!
     * @param  merge   DOCUMENT ME!
     */
    default void masterConfigure(final Element parent, final boolean merge) {
        // do nothing
    }
}
