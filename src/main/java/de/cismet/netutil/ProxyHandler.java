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

import org.openide.util.Lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

    private final ProxyPropertiesHandler propertiesHandler;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ProxyHandler object.
     */
    private ProxyHandler() {
        final ProxyPropertiesHandler propertiesHandler = Lookup.getDefault().lookup(ProxyPropertiesHandler.class);
        this.propertiesHandler = (propertiesHandler != null) ? propertiesHandler : new DummyProxyPropertiesHandler();
        if (propertiesHandler != null) {
            initProperties(propertiesHandler);
        }
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
        final Proxy proxy = getProxy();
        hostCrendentials.put(getHostCredentialsKey(host, port), getHostCredentialsValue(user, password, domain));
        fireProxyChanged(getMode(), getMode(), proxy, getProxy());
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
                    ProxyHandler.getInstance().clear();
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
    private void clear() {
        try {
            getPropertiesHandler().saveProperties(new ProxyProperties());
        } catch (final Exception ex) {
            LOG.warn("could not clear proxy preferences", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mode         DOCUMENT ME!
     * @param  manualProxy  DOCUMENT ME!
     */
    private void saveProperties(final Mode mode, final Proxy manualProxy) {
        try {
            final ProxyProperties properties = new ProxyProperties();
            properties.setProxyMode(mode);
            properties.setProxyEnabled(manualProxy.isEnabled());
            properties.setProxyHost(manualProxy.getHost());
            properties.setProxyPort(manualProxy.getPort());
            properties.setProxyDomain(manualProxy.getDomain());
            properties.setProxyUsername(manualProxy.getUsername());
            properties.setProxyPassword(manualProxy.getPassword());
            properties.setProxyExcludedHosts(manualProxy.getExcludedHosts());
            getPropertiesHandler().saveProperties(properties);
        } catch (final Exception ex) {
            LOG.warn("could not write proxy properties", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  propertiesHandler  DOCUMENT ME!
     */
    private void initProperties(final ProxyPropertiesHandler propertiesHandler) {
        final ProxyProperties properties = propertiesHandler.loadProperties();
        if (properties != null) {
            final ProxyHandler.Mode mode = properties.getProxyMode();
            final Boolean enabled = properties.getProxyEnabled();
            final String host = properties.getProxyHost();
            final int port = properties.getProxyPort();
            final String username = properties.getProxyUsername();
            final String password = properties.getProxyPassword();
            final String domain = properties.getProxyDomain();
            final String excludedHosts = properties.getProxyExcludedHosts();

            final Proxy proxy = new Proxy((enabled != null) ? enabled : false,
                    host,
                    port,
                    excludedHosts,
                    username,
                    (password != null) ? PasswordEncrypter.decryptString(password) : null,
                    domain);
            if (proxy.isValid()) {
                setManualProxy(proxy);
            }
            setMode(mode);
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
     * @return  DOCUMENT ME!
     */
    private ProxyPropertiesHandler getPropertiesHandler() {
        return propertiesHandler;
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
     * @param  oldMode   DOCUMENT ME!
     * @param  oldProxy  DOCUMENT ME!
     */
    public final void proxyChanged(final ProxyHandler.Mode oldMode, final Proxy oldProxy) {
        final Proxy newProxy = getProxy();
        final ProxyHandler.Mode newMode = getMode();
        saveProperties(newMode, getManualProxy());
        fireProxyChanged(oldMode, newMode, oldProxy, newProxy);
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
    private class DummyProxyPropertiesHandler implements ProxyPropertiesHandler {

        //~ Methods ------------------------------------------------------------

        @Override
        public ProxyProperties loadProperties() {
            if (LOG.isDebugEnabled()) {
                LOG.debug("dummyProxyPropertiesHandler.loadProperties()");
            }
            return new ProxyProperties();
        }

        @Override
        public void saveProperties(final ProxyProperties properties) throws Exception {
            if (LOG.isDebugEnabled()) {
                LOG.debug("dummyProxyPropertiesHandler.saveProperties()");
            }
        }
    }

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
