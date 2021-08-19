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

import java.util.ArrayList;
import java.util.Collection;
import java.util.prefs.Preferences;

import de.cismet.tools.PasswordEncrypter;

import static de.cismet.netutil.Proxy.PROXY_DOMAIN;
import static de.cismet.netutil.Proxy.PROXY_ENABLED;
import static de.cismet.netutil.Proxy.PROXY_EXCLUDEDHOSTS;
import static de.cismet.netutil.Proxy.PROXY_HOST;
import static de.cismet.netutil.Proxy.PROXY_PASSWORD;
import static de.cismet.netutil.Proxy.PROXY_PORT;
import static de.cismet.netutil.Proxy.PROXY_USERNAME;
import static de.cismet.netutil.Proxy.clear;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class ProxyHandler {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(ProxyHandler.class);

    //~ Instance fields --------------------------------------------------------

    private Proxy proxy;

    private final Collection<Listener> listeners = new ArrayList<>();

    //~ Constructors -----------------------------------------------------------

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

    //~ Methods ----------------------------------------------------------------

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

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
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
