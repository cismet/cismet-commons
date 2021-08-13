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

import static de.cismet.netutil.Proxy.PROXY_DOMAIN;
import static de.cismet.netutil.Proxy.PROXY_ENABLED;
import static de.cismet.netutil.Proxy.PROXY_EXCLUDEDHOSTS;
import static de.cismet.netutil.Proxy.PROXY_HOST;
import static de.cismet.netutil.Proxy.PROXY_PASSWORD;
import static de.cismet.netutil.Proxy.PROXY_PORT;
import static de.cismet.netutil.Proxy.PROXY_USERNAME;
import static de.cismet.netutil.Proxy.clear;
import de.cismet.tools.PasswordEncrypter;
import de.cismet.tools.configuration.Configurable;
import de.cismet.tools.configuration.NoWriteError;
import java.util.ArrayList;
import java.util.Collection;
import java.util.prefs.Preferences;
import org.apache.log4j.Logger;
import org.jdom.Element;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class ProxyHandler implements Configurable {

    //~ Instance fields --------------------------------------------------------
    
    private static final String CONFIGURATION = "ProxyOptionsPanel";  // NOI18N
    private static final String CONF_TYPE = "ProxyType";              // NOI18N
    private static final String CONF_HOST = "ProxyHost";              // NOI18N
    private static final String CONF_PORT = "ProxyPort";              // NOI18N
    private static final String CONF_USERNAME = "ProxyUsername";      // NOI18N
    private static final String CONF_PASSWORD = "ProxyPassword";      // NOI18N
    private static final String CONF_EXCLUDEDHOSTS = "ProxyPassword"; // NOI18N
    private static final String CONF_DOMAIN = "ProxyDomain";          // NOI18N

    private static final Logger LOG = Logger.getLogger(ProxyHandler.class);
    private Proxy proxy;

    private final Collection<Listener> listeners = new ArrayList<>();

    //~ Constructors -----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   parent  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private Proxy proxyFromElement(final Element parent) throws Exception {
        String elementProxyType = null;
        String elementProxyHost = null;
        String elementProxyPort = null;
        String elementProxyUsername = null;
        String elementProxyPassword = null;
        String elementProxyDomain = null;
        String elementProxyExcludedhosts = null;
        if (parent != null) {
            final Element conf = parent.getChild(CONFIGURATION);
            if (conf != null) {
                elementProxyType = conf.getChildText(CONF_TYPE);
                elementProxyHost = conf.getChildText(CONF_HOST);
                elementProxyPort = conf.getChildText(CONF_PORT);
                elementProxyUsername = conf.getChildText(CONF_USERNAME);
                elementProxyPassword = conf.getChildText(CONF_PASSWORD);
                elementProxyDomain = conf.getChildText(CONF_DOMAIN);
                elementProxyExcludedhosts = conf.getChildText(CONF_EXCLUDEDHOSTS);
            }
        }
        final boolean enabled = (elementProxyType != null) && elementProxyType.equals("MANUAL");

        int port;
        try {
            port = Integer.valueOf(elementProxyPort);
        } catch (final NumberFormatException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("cannot parse port from configuration element", ex); // NOI18N
            }
            port = 0;
        }

        return new Proxy(
                elementProxyHost,
                port,
                elementProxyUsername,
                PasswordEncrypter.decryptString(elementProxyPassword),
                elementProxyDomain,
                elementProxyExcludedhosts,
                enabled);
    }
    /**
     * Creates a new ProxyHandler object.
     */
    private ProxyHandler() {
        final Proxy systemProxy = Proxy.fromSystem();
        if (systemProxy != null) {
            setProxy(systemProxy);
        } else {
            setProxy(proxyFromPreferences());
        }
    }

    /**
     * Stores the given proxy in the user's preferences. If the proxy or the host is <code>null</code> or empty or the
     * port is not greater than 0 all proxy entries will be removed.
     *
     * @param  proxy  the proxy to store
     */
    public static void toPreferences(final Proxy proxy) {
        if ((proxy == null) || (proxy.getHost() == null) || proxy.getHost().isEmpty() || (proxy.getPort() < 1)) {
            clear();
        } else {
        final Preferences prefs = Preferences.userNodeForPackage(Proxy.class);

            prefs.put(PROXY_HOST, proxy.getHost());
            prefs.putInt(PROXY_PORT, proxy.getPort());
            prefs.put(PROXY_ENABLED, Boolean.toString(proxy.isEnabled()));
            if (proxy.getUsername() == null) {
                prefs.remove(PROXY_USERNAME);
            } else {
                prefs.put(PROXY_USERNAME, proxy.getUsername());
            }
            if (proxy.getPassword() == null) {
                prefs.remove(PROXY_PASSWORD);
            } else {
                prefs.put(PROXY_PASSWORD, PasswordEncrypter.encryptString(proxy.getPassword()));
            }
            if (proxy.getDomain() == null) {
                prefs.remove(PROXY_DOMAIN);
            } else {
                prefs.put(PROXY_DOMAIN, proxy.getDomain());
            }
            if (proxy.getExcludedHosts() == null) {
                prefs.remove(PROXY_EXCLUDEDHOSTS);
            } else {
                prefs.put(PROXY_EXCLUDEDHOSTS, proxy.getExcludedHosts());
            }
        }
    }

    public static Proxy proxyFromPreferences() {
        final Preferences prefs = Preferences.userNodeForPackage(Proxy.class);
        if (prefs != null) {
        final String host = prefs.get(PROXY_HOST, null); // NOI18N
        final int port = prefs.getInt(PROXY_PORT, -1);
        final String username = prefs.get(PROXY_USERNAME, null);
        final String password = PasswordEncrypter.decryptString(prefs.get(PROXY_PASSWORD, null));
        final String domain = prefs.get(PROXY_DOMAIN, null);
        final String excludedHosts = prefs.get(PROXY_EXCLUDEDHOSTS, null);
        final boolean enabled = prefs.getBoolean(PROXY_ENABLED, false);

        return ((host != null) && (port > 0))
            ? new Proxy(host, port, username, password, domain, excludedHosts, enabled) : null;
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  parent  DOCUMENT ME!
     */
    @Override
    public void configure(final Element parent) {
        try {
            final Proxy proxy = proxyFromElement(parent);
            if (proxy != null) {
                setProxy(proxy);
            }
        } catch (final Exception ex) {
            LOG.error("error during ProxyHandler configuration", ex); // NOI18N
        }
    }    
    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<Listener> getListeners() {
        return listeners;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  listener  DOCUMENT ME!
     */
    public void addListener(final Listener listener) {
        listeners.add(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  listener  DOCUMENT ME!
     */
    public void removeListener(final Listener listener) {
        listeners.remove(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  oldProxy  DOCUMENT ME!
     * @param  newProxy  DOCUMENT ME!
     */
    protected void fireProxyChanged(final Proxy oldProxy, final Proxy newProxy) {
        final Event event = new Event(Event.Type.PROXY_CHANGED);
        event.setOldProxy(oldProxy);
        event.setNewProxy(newProxy);

        for (final Listener listener : listeners) {
            listener.proxyChanged(event);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Proxy getProxy() {
        return proxy;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  newProxy  DOCUMENT ME!
     */
    public final void setProxy(final Proxy newProxy) {
        final Proxy oldProxy = this.proxy;
        this.proxy = newProxy;
        toPreferences(proxy);
        if ((proxy != null) && proxy.isEnabled()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("set proxy in system-property: " + proxy); // NOI18N
            }
            System.setProperty(Proxy.SYSTEM_PROXY_HOST, proxy.getHost());
            System.setProperty(Proxy.SYSTEM_PROXY_PORT, String.valueOf(proxy.getPort()));
            if (proxy.getUsername() != null) {
                System.setProperty(Proxy.SYSTEM_PROXY_USERNAME, proxy.getUsername());
            }
            if (proxy.getPassword() != null) {
                System.setProperty(
                    Proxy.SYSTEM_PROXY_PASSWORD,
                    PasswordEncrypter.encryptString(proxy.getPassword()));
            }
            if (proxy.getDomain() != null) {
                System.setProperty(Proxy.SYSTEM_PROXY_DOMAIN, proxy.getDomain());
            }
            if (proxy.getExcludedHosts() != null) {
                System.setProperty(Proxy.SYSTEM_PROXY_EXCLUDEDHOSTS, proxy.getExcludedHosts());
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("clear proxy in system-property");         // NOI18N
            }
            System.clearProperty(Proxy.SYSTEM_PROXY_HOST);
            System.clearProperty(Proxy.SYSTEM_PROXY_PORT);
            System.clearProperty(Proxy.SYSTEM_PROXY_USERNAME);
            System.clearProperty(Proxy.SYSTEM_PROXY_PASSWORD);
            System.clearProperty(Proxy.SYSTEM_PROXY_DOMAIN);
            System.clearProperty(Proxy.SYSTEM_PROXY_EXCLUDEDHOSTS);
        }
        
        fireProxyChanged(oldProxy, newProxy);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ProxyHandler getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    @Override
    public void masterConfigure(final Element parent) {
        configure(parent);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  NoWriteError  DOCUMENT ME!
     */
    @Override
    public Element getConfiguration() throws NoWriteError {
        final Proxy proxy = getProxy();

        final Element conf = new Element(CONFIGURATION);
        conf.addContent(new Element(CONF_TYPE).addContent(proxy.isEnabled() ? "MANUAL" : "NO"));
        conf.addContent(new Element(CONF_HOST).addContent(proxy.getHost()));
        conf.addContent(new Element(CONF_PORT).addContent(Integer.toString(proxy.getPort())));
        conf.addContent(new Element(CONF_USERNAME).addContent(proxy.getUsername()));
        conf.addContent(
            new Element(CONF_PASSWORD).addContent(
                (proxy.getPassword() != null) ? PasswordEncrypter.encryptString(proxy.getPassword()) : null));
        conf.addContent(new Element(CONF_DOMAIN).addContent(proxy.getDomain()));
        return conf;
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public interface Listener {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  event  DOCUMENT ME!
         */
        void proxyChanged(final Event event);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final ProxyHandler INSTANCE = new ProxyHandler();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class Event {

        //~ Enums --------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @version  $Revision$, $Date$
         */
        public enum Type {

            //~ Enum constants -------------------------------------------------

            PROXY_CHANGED
        }

        //~ Instance fields ----------------------------------------------------

        private Proxy oldProxy;
        private Proxy newProxy;

        private final Type type;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new Event object.
         *
         * @param  type  DOCUMENT ME!
         */
        protected Event(final Type type) {
            this.type = null;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Type getType() {
            return type;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Proxy getNewProxy() {
            return newProxy;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  newProxy  DOCUMENT ME!
         */
        public void setNewProxy(final Proxy newProxy) {
            this.newProxy = newProxy;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Proxy getOldProxy() {
            return oldProxy;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  oldProxy  DOCUMENT ME!
         */
        public void setOldProxy(final Proxy oldProxy) {
            this.oldProxy = oldProxy;
        }
    }
}
