/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools.configuration;

/**
 * ConfigAttriProvider Interface.
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public interface ConfigAttrProvider {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getUserConfigAttr(final String key);

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getGroupConfigAttr(final String key);

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getDomainConfigAttr(final String key);
}
