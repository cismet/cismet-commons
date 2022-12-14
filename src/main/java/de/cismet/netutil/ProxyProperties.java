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

    private static final String PROXY_MODE = "proxy.mode";
    private static final String PROXY_HOST = "proxy.host";
    private static final String PROXY_PORT = "proxy.port";
    private static final String PROXY_USERNAME = "proxy.username";
    private static final String PROXY_PASSWORD = "proxy.password";
    private static final String PROXY_DOMAIN = "proxy.domain";
    private static final String PROXY_EXCLUDEDHOSTS = "proxy.excludedHosts";
    private static final String PROXY_ENABLED = "proxy.enabled";

    private static final Logger LOG = Logger.getLogger(ProxyProperties.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getProxyHost() {
        return getProperty(PROXY_HOST);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   proxyHost  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object setProxyHost(final String proxyHost) {
        if (proxyHost != null) {
            return setProperty(PROXY_HOST, proxyHost);
        } else {
            return remove(PROXY_HOST);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getProxyPort() {
        final Integer port = integerValueOf(getProperty(PROXY_PORT));
        return ((port != null) && (port >= 0) && (port <= 65535)) ? port : 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   proxyPort  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object setProxyPort(final Integer proxyPort) {
        if (proxyPort != null) {
            return setProperty(PROXY_PORT, (proxyPort != null) ? Integer.toString(proxyPort) : null);
        } else {
            return remove(PROXY_PORT);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getProxyUsername() {
        return getProperty(PROXY_USERNAME);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   username  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object setProxyUsername(final String username) {
        if (username != null) {
            return setProperty(PROXY_USERNAME, username);
        } else {
            return remove(PROXY_USERNAME);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getProxyPassword() {
        return getProperty(PROXY_PASSWORD);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   password  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object setProxyPassword(final String password) {
        if (password != null) {
            return setProperty(PROXY_PASSWORD, password);
        } else {
            return remove(PROXY_PASSWORD);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getProxyDomain() {
        return getProperty(PROXY_DOMAIN);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   domain  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object setProxyDomain(final String domain) {
        if (domain != null) {
            return setProperty(PROXY_DOMAIN, domain);
        } else {
            return remove(PROXY_DOMAIN);
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getProxyExcludedHosts() {
        return getProperty(PROXY_EXCLUDEDHOSTS);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   excludedhosts  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object setProxyExcludedHosts(final String excludedhosts) {
        if (excludedhosts != null) {
            return setProperty(PROXY_EXCLUDEDHOSTS, excludedhosts);
        } else {
            return remove(PROXY_EXCLUDEDHOSTS);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean getProxyEnabled() {
        return booleanValueOf(getProperty(PROXY_ENABLED));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   enabled  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object setProxyEnabled(final Boolean enabled) {
        if (enabled != null) {
            return setProperty(PROXY_ENABLED, (enabled != null) ? Boolean.toString(enabled) : null);
        } else {
            return remove(PROXY_ENABLED);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ProxyHandler.Mode getProxyMode() {
        return ProxyHandler.Mode.valueOf(getProperty(PROXY_MODE));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   mode  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object setProxyMode(final ProxyHandler.Mode mode) {
        if (mode != null) {
            return setProperty(PROXY_MODE, (mode != null) ? mode.name() : null);
        } else {
            return remove(PROXY_MODE);
        }
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
            return (value != null) ? Integer.parseInt(value) : null;
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
