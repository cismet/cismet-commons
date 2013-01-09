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
     * Getter for the UserConfig value with specified key.
     *
     * @param   key  key, which value is searched
     *
     * @return  value as String
     */
    String getUserConfigAttr(final String key);

    /**
     * Getter for the GroupConfig value with specified key.
     *
     * @param   key  key, which value is searched
     *
     * @return  value as String
     */
    String getGroupConfigAttr(final String key);

    /**
     * Getter for the DomainConfig value with specified key.
     *
     * @param   key  key, which value is searched
     *
     * @return  value as String
     */
    String getDomainConfigAttr(final String key);
}
