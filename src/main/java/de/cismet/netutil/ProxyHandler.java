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
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import de.cismet.tools.PasswordEncrypter;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class ProxyHandler {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(ProxyHandler.class);

    public static final String PROXY_ENABLED = "proxy.enabled";             // NOI18N
    public static final String PROXY_MODE = "proxy.mode";
    public static final String PROXY_HOST = "proxy.host";                   // NOI18N
    public static final String PROXY_PORT = "proxy.port";                   // NOI18N
    public static final String PROXY_USERNAME = "proxy.username";           // NOI18N
    public static final String PROXY_PASSWORD = "proxy.password";           // NOI18N
    public static final String PROXY_DOMAIN = "proxy.domain";               // NOI18N
    public static final String PROXY_EXCLUDEDHOSTS = "proxy.excludedHosts"; // NOI18N

    public static final String SYSTEM_PROXY_HOST = "http.proxyHost";              // NOI18N
    public static final String SYSTEM_PROXY_PORT = "http.proxyPort";              // NOI18N
    public static final String SYSTEM_PROXY_USERNAME = "http.proxyUsername";      // NOI18N
    public static final String SYSTEM_PROXY_PASSWORD = "http.proxyPassword";      // NOI18N
    public static final String SYSTEM_PROXY_DOMAIN = "http.proxyDomain";          // NOI18N
    public static final String SYSTEM_PROXY_EXCLUDEDHOSTS = "http.nonProxyHosts"; // NOI18N

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum Mode {

        //~ Enum constants -----------------------------------------------------

        MANUAL, PRECONFIGURED
    }

    //~ Instance fields --------------------------------------------------------

    private final transient Map<String, String> hostCrendentials = new HashMap<>();

    private Proxy manualProxy;
    private Proxy preconfiguredProxy;

    private final Collection<Listener> listeners = new ArrayList<>();

    private Mode mode;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ProxyHandler object.
     */
    private ProxyHandler() {
        initFromPreference();
        // do not use the system proxy. Only use the proxy that was configured by the application
        // If the system proxy is used, no one will know, what proxy is currently in use
// final Proxy systemProxy = fromSystem();
// if (systemProxy != null) {
// LOG.warn("Set proxy from system");
// useManualProxy(systemProxy);
// }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   host  DOCUMENT ME!
     * @param   port  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String getHostCredentialsKey(final String host, final int port) {
        return String.format("%s:%d", host, port);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   user      DOCUMENT ME!
     * @param   password  DOCUMENT ME!
     * @param   domain    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String getHostCredentialsValue(final String user, final String password, final String domain) {
        return String.format("%s\n%s\n%s", user, password, domain);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  host      DOCUMENT ME!
     * @param  port      DOCUMENT ME!
     * @param  user      DOCUMENT ME!
     * @param  password  DOCUMENT ME!
     * @param  domain    DOCUMENT ME!
     */
    public void addHostCredentials(final String host,
            final int port,
            final String user,
            final String password,
            final String domain) {
        hostCrendentials.put(getHostCredentialsKey(host, port), getHostCredentialsValue(user, password, domain));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  host  DOCUMENT ME!
     * @param  port  DOCUMENT ME!
     */
    public void clearHostCredentials(final String host, final int port) {
        hostCrendentials.remove(getHostCredentialsKey(host, port));
    }

    /**
     * DOCUMENT ME!
     */
    public void clearHostCredentials() {
        hostCrendentials.clear();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Proxy getManualProxy() {
        return (manualProxy != null) ? manualProxy : new Proxy();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Proxy getPreconfiguredProxy() {
        return (preconfiguredProxy != null) ? preconfiguredProxy : new Proxy();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  manualProxy  DOCUMENT ME!
     */
    public void setManualProxy(final Proxy manualProxy) {
        this.manualProxy = manualProxy;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  preconfiguredProxy  DOCUMENT ME!
     */
    public void setPreconfiguredProxy(final Proxy preconfiguredProxy) {
        this.preconfiguredProxy = preconfiguredProxy;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mode  DOCUMENT ME!
     */
    private void setMode(final Mode mode) {
        final ProxyHandler.Mode oldMode = this.mode;
        final Proxy oldProxy = getProxy();
        this.mode = mode;
        proxyChanged(oldMode, oldProxy);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   proxyProperties  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Proxy init(final ProxyProperties proxyProperties) {
        final Proxy preconfiguredProxy = (proxyProperties != null)
            ? new Proxy(
                proxyProperties.getProxyEnabled(),
                proxyProperties.getProxyHost(),
                proxyProperties.getProxyPort(),
                proxyProperties.getProxyExcludedHosts(),
                proxyProperties.getProxyUsername(),
                proxyProperties.getProxyPassword(),
                proxyProperties.getProxyDomain()) : null;
        setPreconfiguredProxy(preconfiguredProxy);
        if ((preconfiguredProxy != null) && preconfiguredProxy.isValid()) {
            if (!getManualProxy().isValid()) {
                setManualProxy(preconfiguredProxy);
            }
            if (getMode() == null) {
                usePreconfiguredProxy();
            } else {
                proxyChanged(getMode(), getProxy());
            }
        } else {
            useManualProxy();
        }
        return getProxy();
    }

    /**
     * Loads a <code>Proxy</code> instance from System preferences. If there are no host and port proxy information
     * <code>null</code> will be returned. If the return value is non-null at least the host and the port is
     * initialised. Username, Password and Domain may be null.
     *
     * <p>This method should not be used anymore. The Proxy that was configured by the application should be used</p>
     *
     * @return  the user's proxy settings or null if no settings present
     */
    @Deprecated
    private static Proxy fromSystem() {
        final String host = System.getProperty(SYSTEM_PROXY_HOST);
        final String portString = System.getProperty(SYSTEM_PROXY_PORT);
        if ((host != null) && (portString != null)) {
            try {
                final int port = Integer.valueOf(portString);
                final String username = System.getProperty(SYSTEM_PROXY_USERNAME);
                final String password = PasswordEncrypter.decryptString(System.getProperty(SYSTEM_PROXY_PASSWORD));
                final String domain = System.getProperty(SYSTEM_PROXY_DOMAIN);
                final String excludedHost = System.getProperty(SYSTEM_PROXY_EXCLUDEDHOSTS);

                return new Proxy(true, host, port, excludedHost, username, password, domain);
            } catch (final NumberFormatException e) {
                LOG.error("error during creation of proxy from system properties", e); // NOI18N
            }
        }

        return null;
    }

    /**
     * main program; used for testing. // NOTE: use cli library if there shall be more (complex) options
     *
     * @param  args  args
     */
    @SuppressWarnings("CallToThreadDumpStack")
    public static void main(final String[] args) {
        try {
            if (args.length == 1) {
                final String arg = args[0];
                if ("-c".equals(arg) || "--clear".equals(arg)) {        // NOI18N
                    clear();
                    showMessage("Proxy information cleared", false);
                } else if ("-p".equals(arg) || "--print".equals(arg)) { // NOI18N
                    final ProxyHandler proxyHandler = ProxyHandler.getInstance();
                    final Mode mode = proxyHandler.getMode();
                    final Proxy proxy = proxyHandler.getProxy();

                    showMessage(String.format("proxy is set to: %s", (mode != null) ? mode.name() : null), false);
                    showMessage(String.format("manual proxy: %s", (proxy != null) ? proxy.toString() : null), false);
                } else {
                    printUsage();
                    System.exit(1);
                }
            } else {
                printUsage();
                System.exit(1);
            }

            System.exit(0);
        } catch (final Exception e) {
            showMessage("Something went wrong: " + e.getMessage(), true); // NOI18N
            System.err.println("\n");
            e.printStackTrace();
            System.err.println();
            System.exit(2);
        }
    }

    /**
     * shows Message. If there is no System.console it writes the meassage on OptionPane.Else it writes the message on
     * Console as Error or Output.
     *
     * @param  message  the message
     * @param  error    true if it is a error message,false if not
     */
    private static void showMessage(final String message, final boolean error) {
        if (System.console() == null) {
            JOptionPane.showMessageDialog(
                null,
                message,
                error ? "Error" : "Information", // NOI18N
                error ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
        } else {
            if (error) {
                System.err.println("\n" + message + "\n"); // NOI18N
            } else {
                System.out.println("\n" + message + "\n"); // NOI18N
            }
        }
    }

    /**
     * print usage.
     */
    private static void printUsage() {
        showMessage("Supported parameters are:\n\n"                  // NOI18N
                    + "-c --clear\t\tremoves all proxy settings\n"   // NOI18N
                    + "-p --print\t\tprints out the proxy settings", // NOI18N
            true);
    }

    /**
     * Clears the Proxy Object.
     */
    public static void clear() {
        final Preferences prefs = Preferences.userNodeForPackage(Proxy.class);

        // won't use clear since we don't know if anybody else stored preferences for this package
        prefs.remove(PROXY_MODE);
        prefs.remove(PROXY_HOST);
        prefs.remove(PROXY_PORT);
        prefs.remove(PROXY_EXCLUDEDHOSTS);
        prefs.remove(PROXY_USERNAME);
        prefs.remove(PROXY_PASSWORD);
        prefs.remove(PROXY_DOMAIN);
    }

    /**
     * Stores the given proxy in the user's preferences. If the proxy or the host is <code>null</code> or empty or the
     * port is not greater than 0 all proxy entries will be removed.
     *
     * @param  mode         DOCUMENT ME!
     * @param  manualProxy  the proxy to store
     */
    private static void toPreferences(final Mode mode, final Proxy manualProxy) {
        final Preferences prefs = Preferences.userNodeForPackage(Proxy.class);

        if (mode == null) {
            prefs.remove(PROXY_MODE);
        } else {
            prefs.put(PROXY_MODE, mode.name());
        }
        prefs.put(PROXY_ENABLED, Boolean.toString(manualProxy.isEnabled()));
        if (manualProxy.getHost() == null) {
            prefs.remove(PROXY_HOST);
        } else {
            prefs.put(PROXY_HOST, manualProxy.getHost());
        }
        prefs.putInt(PROXY_PORT, manualProxy.getPort());
        if (manualProxy.getExcludedHosts() == null) {
            prefs.remove(PROXY_EXCLUDEDHOSTS);
        } else {
            prefs.put(PROXY_EXCLUDEDHOSTS, manualProxy.getExcludedHosts());
        }
        if (manualProxy.getUsername() == null) {
            prefs.remove(PROXY_USERNAME);
        } else {
            prefs.put(PROXY_USERNAME, manualProxy.getUsername());
        }
        if (manualProxy.getPassword() == null) {
            prefs.remove(PROXY_PASSWORD);
        } else {
            prefs.put(PROXY_PASSWORD, PasswordEncrypter.encryptString(manualProxy.getPassword()));
        }
        if (manualProxy.getDomain() == null) {
            prefs.remove(PROXY_DOMAIN);
        } else {
            prefs.put(PROXY_DOMAIN, manualProxy.getDomain());
        }
        try {
            prefs.sync();
        } catch (final BackingStoreException ex) {
            // do not throw an exception. Otherwise, the proxy settings will not be applied
            LOG.error("Cannot save proxy configuration", ex);
            // throw new RuntimeException("Proxy-Einstellungen konnten nicht abgespeichert werden.", ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initFromPreference() {
        final Preferences prefs = Preferences.userNodeForPackage(Proxy.class);
        if (prefs != null) {
            boolean enabled = false;
            try {
                enabled = Boolean.valueOf(prefs.get(PROXY_ENABLED, null));
            } catch (final Exception ex) {
            }
            final String host = prefs.get(PROXY_HOST, null); // NOI18N
            final int port = prefs.getInt(PROXY_PORT, 0);
            final String username = prefs.get(PROXY_USERNAME, null);
            final String password = PasswordEncrypter.decryptString(prefs.get(PROXY_PASSWORD, null));
            final String domain = prefs.get(PROXY_DOMAIN, null);
            final String excludedHosts = prefs.get(PROXY_EXCLUDEDHOSTS, null);

            final Proxy manualProxy = new Proxy(enabled, host, port, excludedHosts, username, password, domain);
            if (manualProxy.isValid()) {
                setManualProxy(manualProxy);
            }

            try {
                setMode(Mode.valueOf(prefs.get(PROXY_MODE, null)));
            } catch (final Exception ex) {
            }
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
     * @param  oldMode   DOCUMENT ME!
     * @param  newMode   DOCUMENT ME!
     * @param  oldProxy  DOCUMENT ME!
     * @param  newProxy  DOCUMENT ME!
     */
    protected void fireProxyChanged(final ProxyHandler.Mode oldMode,
            final ProxyHandler.Mode newMode,
            final Proxy oldProxy,
            final Proxy newProxy) {
        final Event event = new Event(Event.Type.PROXY_CHANGED);
        event.setOldMode(oldMode);
        event.setNewMode(newMode);
        event.setOldProxy(oldProxy);
        event.setNewProxy(newProxy);

        for (final Listener listener : listeners) {
            listener.proxyChanged(event);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void useNoProxy() {
        final Proxy manualProxy = getManualProxy();
        final Proxy noProxy = (manualProxy != null) ? manualProxy : new Proxy();
        noProxy.setEnabled(false);
        setManualProxy(noProxy);
    }

    /**
     * DOCUMENT ME!
     */
    public void usePreconfiguredProxy() {
        setMode(Mode.PRECONFIGURED);
    }

    /**
     * DOCUMENT ME!
     */
    public final void useManualProxy() {
        setMode(Mode.MANUAL);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  proxy  DOCUMENT ME!
     */
    public final void useManualProxy(final Proxy proxy) {
        if (proxy != null) {
            setManualProxy(proxy);
            useManualProxy();
        } else {
            useNoProxy();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Proxy getProxy() {
        final Mode mode = getMode();
        final Proxy proxy;
        if (mode != null) {
            switch (mode) {
                case MANUAL: {
                    proxy = manualProxy;
                }
                break;
                case PRECONFIGURED: {
                    proxy = preconfiguredProxy;
                }
                break;
                default: {
                    proxy = null;
                }
            }
        } else {
            proxy = null;
        }
        if ((proxy != null) && !proxy.isFullCredentials()) {
            final String credentials = hostCrendentials.get(getHostCredentialsKey(proxy.getHost(), proxy.getPort()));
            if (credentials != null) {
                final String[] split = credentials.split("\n");
                if (split.length == 3) {
                    final String username = split[0];
                    final String password = split[1];
                    final String domain = split[2];
                    return new Proxy(proxy.isEnabled(),
                            proxy.getHost(),
                            proxy.getPort(),
                            proxy.getExcludedHosts(),
                            username,
                            password,
                            domain);
                } else {
                    LOG.warn("credentials have not 3 parts");
                }
            }
        }
        return proxy;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  proxy  DOCUMENT ME!
     */
    private static void toSystem(final Proxy proxy) {
        if ((proxy != null) && proxy.isValid()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("set proxy in system-property: " + proxy); // NOI18N
            }
            System.setProperty(SYSTEM_PROXY_HOST, proxy.getHost());
            System.setProperty(SYSTEM_PROXY_PORT, String.valueOf(proxy.getPort()));
            if (proxy.getUsername() != null) {
                System.setProperty(SYSTEM_PROXY_USERNAME, proxy.getUsername());
            }
            if (proxy.getPassword() != null) {
                System.setProperty(
                    SYSTEM_PROXY_PASSWORD,
                    PasswordEncrypter.encryptString(proxy.getPassword()));
            }
            if (proxy.getDomain() != null) {
                System.setProperty(SYSTEM_PROXY_DOMAIN, proxy.getDomain());
            }
            if (proxy.getExcludedHosts() != null) {
                System.setProperty(SYSTEM_PROXY_EXCLUDEDHOSTS, proxy.getExcludedHosts());
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("clear proxy in system-property");         // NOI18N
            }
            System.clearProperty(SYSTEM_PROXY_HOST);
            System.clearProperty(SYSTEM_PROXY_PORT);
            System.clearProperty(SYSTEM_PROXY_USERNAME);
            System.clearProperty(SYSTEM_PROXY_PASSWORD);
            System.clearProperty(SYSTEM_PROXY_DOMAIN);
            System.clearProperty(SYSTEM_PROXY_EXCLUDEDHOSTS);
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @param  oldMode   DOCUMENT ME!
     * @param  oldProxy  DOCUMENT ME!
     */
    public final void proxyChanged(final ProxyHandler.Mode oldMode, final Proxy oldProxy) {
        final Proxy newProxy = getProxy();
        final ProxyHandler.Mode newMode = getMode();
        // if (!Objects.equals(oldProxy, newProxy)) {
        toPreferences(newMode, getManualProxy());
        // toSystem(newProxy); ?!?!?!
        fireProxyChanged(oldMode, newMode, oldProxy, newProxy);
        // }
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

        private Mode oldMode;
        private Mode newMode;
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

        /**
         * DOCUMENT ME!
         *
         * @param  oldMode  DOCUMENT ME!
         */
        public void setOldMode(final Mode oldMode) {
            this.oldMode = oldMode;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Mode getOldMode() {
            return oldMode;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  newMode  DOCUMENT ME!
         */
        public void setNewMode(final Mode newMode) {
            this.newMode = newMode;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Mode getNewMode() {
            return newMode;
        }
    }
}
