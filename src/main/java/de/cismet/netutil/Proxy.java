/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.netutil;

import org.apache.log4j.Logger;

import java.net.URL;

import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

import de.cismet.tools.PasswordEncrypter;
import de.cismet.tools.WildcardUtils;

/**
 * Class that provides Proxy Usage.
 *
 * @author   spuhl
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class Proxy {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(Proxy.class);

    public static final String PROXY_HOST = "proxy.host";                   // NOI18N
    public static final String PROXY_PORT = "proxy.port";                   // NOI18N
    public static final String PROXY_USERNAME = "proxy.username";           // NOI18N
    public static final String PROXY_PASSWORD = "proxy.password";           // NOI18N
    public static final String PROXY_DOMAIN = "proxy.domain";               // NOI18N
    public static final String PROXY_EXCLUDEDHOSTS = "proxy.excludedHosts"; // NOI18N
    public static final String PROXY_ENABLED = "proxy.enabled";             // NOI18N

    public static final String SYSTEM_PROXY_HOST = "http.proxyHost";              // NOI18N
    public static final String SYSTEM_PROXY_PORT = "http.proxyPort";              // NOI18N
    public static final String SYSTEM_PROXY_EXCLUDEDHOSTS = "http.nonProxyHosts"; // NOI18N
    public static final String SYSTEM_PROXY_USERNAME = "http.proxyUsername";      // NOI18N
    public static final String SYSTEM_PROXY_PASSWORD = "http.proxyPassword";      // NOI18N
    public static final String SYSTEM_PROXY_DOMAIN = "http.proxyDomain";          // NOI18N

    public static final String SYSTEM_PROXY_SET = "proxySet"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private transient String host;
    private transient int port;
    private transient String username;
    private transient String password;
    private transient String domain;
    private transient String excludedHosts;
    private transient boolean enabled;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a Default Proxy object.
     */
    public Proxy() {
        this(null, -1, null, null, null, null, false);
    }

    /**
     * Creates a new Proxy object with specified <code>host</code> and <code>port</code>.
     *
     * @param  host  proxyURL
     * @param  port  computerName
     */
    public Proxy(final String host, final int port) {
        this(host, port, null, null, null, null, true);
    }

    /**
     * Creates a new Proxy object with specified <code>host</code>, <code>port</code>, <code>username</code> and <code>
     * password</code>.
     *
     * @param  host      proxyURL
     * @param  port      computerName
     * @param  username  username
     * @param  password  password
     */
    public Proxy(final String host, final int port, final String username, final String password) {
        this(host, port, username, password, null, null, true);
    }

    /**
     * Creates a new Proxy object <code>host</code>, <code>port</code>, <code>username</code>, <code>password</code> and
     * <code>domain</code>. Additional it can be specified whether the Proxy is enabled or not
     *
     * @param  host      proxyURL
     * @param  port      computerName
     * @param  username  username
     * @param  password  password
     * @param  domain    domain
     * @param  enabled   enabled or disabled
     */
    public Proxy(final String host,
            final int port,
            final String username,
            final String password,
            final String domain,
            final boolean enabled) {
        this(host, port, username, password, domain, null, enabled);
    }

    /**
     * Creates a new Proxy object.
     *
     * @param  host           DOCUMENT ME!
     * @param  port           DOCUMENT ME!
     * @param  username       DOCUMENT ME!
     * @param  password       DOCUMENT ME!
     * @param  domain         DOCUMENT ME!
     * @param  excludedHosts  DOCUMENT ME!
     * @param  enabled        DOCUMENT ME!
     */
    public Proxy(final String host,
            final int port,
            final String username,
            final String password,
            final String domain,
            final String excludedHosts,
            final boolean enabled) {
        setHost(host);
        setPort(port);
        setEnabled(enabled);
        setUsername(username);
        setPassword(password);
        setDomain(domain);
        setExcludedHosts(excludedHosts);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Getter for <code>host</code>.
     *
     * @return  <code>host</code>
     */
    public String getHost() {
        return host;
    }

    /**
     * Setter for host.
     *
     * @param  host  <code>host</code>
     */
    public void setHost(final String host) {
        this.host = ((host == null) || host.isEmpty()) ? null : host;
    }

    /**
     * Getter for <code>port</code>.
     *
     * @return  <code>port</code>
     */
    public int getPort() {
        return port;
    }

    /**
     * Setter for <code>port</code>.
     *
     * @param  port  <code>port</code>
     */
    public void setPort(final int port) {
        this.port = port;
    }

    /**
     * Return the Proxy's Attributes as <code>String</code>.
     *
     * @return  "Proxy: " + <code>host</code> + ":" + <code>port</code> + " | username: " + <code>username</code> +" |
     *          password: " + ((<code>password</code> == <code>null</code>) ? <code>null</code> : "<invisible>") + " |
     *          domain: " + <code>domain</code>
     */
    @Override
    public String toString() {
        return "Proxy: " + host + ":" + port + " | username: " + username + " | password: " // NOI18N
                    + ((password == null) ? null : "<invisible>") + " | domain: " + domain + " | excludedHosts: "
                    + excludedHosts;                                                        // NOI18N
    }

    /**
     * Getter for <code>domain</code>.
     *
     * @return  <code>domain</code>
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Setter for <code>domain</code>.
     *
     * @param  domain  <code>domain</code>
     */
    public void setDomain(final String domain) {
        this.domain = ((domain == null) || domain.isEmpty()) ? null : domain;
    }

    /**
     * Getter for <code>password</code>.
     *
     * @return  <code>password</code>
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter for <code>password</code>.
     *
     * @param  password  <code>password</code>
     */
    public void setPassword(final String password) {
        this.password = ((password == null) || password.isEmpty()) ? null : password;
    }

    /**
     * Getter for <code>username</code>.
     *
     * @return  <code>username</code>
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter for <code>username</code>.
     *
     * @param  username  <code>username</code>
     */
    public void setUsername(final String username) {
        this.username = ((username == null) || username.isEmpty()) ? null : username;
    }

    /**
     * Tests whether <code>enabled</code> is true or false.
     *
     * @return  true, if it is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Enables or disables.
     *
     * @param  enabled  <code>true</code> or <code>false</code>
     */
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Clears the Proxy Object.
     */
    public static void clear() {
        final Preferences prefs = Preferences.userNodeForPackage(Proxy.class);

        // won't use clear since we don't know if anybody else stored preferences for this package
        prefs.remove(PROXY_HOST);
        prefs.remove(PROXY_PORT);
        prefs.remove(PROXY_USERNAME);
        prefs.remove(PROXY_PASSWORD);
        prefs.remove(PROXY_DOMAIN);
        prefs.remove(PROXY_EXCLUDEDHOSTS);
        prefs.remove(PROXY_ENABLED);
    }

    /**
     * Loads a <code>Proxy</code> instance from previously stored user preferences. If there are no host and port proxy
     * information <code>null</code> will be returned. If the return value is non-null at least the host and the port is
     * initialised. Username, Password and Domain may be null.
     *
     * @return  the user's proxy settings or null if no settings present
     */
   

    /**
     * DOCUMENT ME!
     *
     * @param  excludedHosts  DOCUMENT ME!
     */
    public void setExcludedHosts(final String excludedHosts) {
        this.excludedHosts = excludedHosts;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getExcludedHosts() {
        return excludedHosts;
    }

    /**
     * Loads a <code>Proxy</code> instance from System preferences. If there are no host and port proxy information
     * <code>null</code> will be returned. If the return value is non-null at least the host and the port is
     * initialised. Username, Password and Domain may be null.
     *
     * @return  the user's proxy settings or null if no settings present
     */
    public static Proxy fromSystem() {
        if (Boolean.getBoolean(System.getProperty(SYSTEM_PROXY_SET))) {
            final String host = System.getProperty(SYSTEM_PROXY_HOST);
            final String portString = System.getProperty(SYSTEM_PROXY_PORT);
            if ((host != null) && (portString != null)) {
                try {
                    final int port = Integer.valueOf(portString);
                    final String username = System.getProperty(SYSTEM_PROXY_USERNAME);
                    final String password = PasswordEncrypter.decryptString(System.getProperty(SYSTEM_PROXY_PASSWORD));
                    final String domain = System.getProperty(SYSTEM_PROXY_DOMAIN);
                    final String excludedHost = System.getProperty(SYSTEM_PROXY_EXCLUDEDHOSTS);

                    return new Proxy(host, port, username, password, domain, excludedHost, true);
                } catch (final NumberFormatException e) {
                    LOG.error("error during creation of proxy from system properties", e); // NOI18N
                }
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
                    final Proxy proxy = ProxyHandler.getInstance().getProxy();

                    if (proxy == null) {
                        showMessage("Proxy information not set", false); // NOI18N
                    } else {
                        showMessage(proxy.toString(), false);
                    }
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
     * DOCUMENT ME!
     *
     * @param   hostOrUrl  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEnabledFor(final String hostOrUrl) {
        if (hostOrUrl == null) {
            return true; // not false for backwards compability
        }
        String host = hostOrUrl.trim();
        try {
            host = new URL(host).getHost();
        } catch (final Exception ex) {
        }
        if (excludedHosts != null) {
            for (final String excludedHost : excludedHosts.split(",")) {
                if (WildcardUtils.testForMatch(host, excludedHost.trim())) {
                    return false;
                }
            }
        }
        return true;
    }
}
