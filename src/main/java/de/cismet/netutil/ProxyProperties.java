/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.netutil;

import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class ProxyProperties extends Properties {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(ProxyProperties.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getProxyHost() {
        return getProperty("proxy.host");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getProxyPort() {
        final Integer port = integerValueOf(getProperty("proxy.port"));
        return ((port != null) && (port >= 0) && (port <= 65535)) ? port : 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getProxyUsername() {
        return getProperty("proxy.username");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getProxyPassword() {
        return getProperty("proxy.password");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getProxyDomain() {
        return getProperty("proxy.domain");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getProxyExcludedHosts() {
        return getProperty("proxy.excludedHosts");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean getProxyEnabled() {
        return booleanValueOf(getProperty("proxy.enabled"));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   value  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static Integer integerValueOf(final String value) {
        try {
            return value != null ? Integer.parseInt(value) : null;
        } catch (final Exception exp) {
            LOG.warn(String.format("%s could not be parsed to Integer", value), exp); // NOI18N
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   value  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static boolean booleanValueOf(final String value) {
        return (value != null) && (value.equalsIgnoreCase(Boolean.TRUE.toString()) || value.equals("1"));
    }
}
