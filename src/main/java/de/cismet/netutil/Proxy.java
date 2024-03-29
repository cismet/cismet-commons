/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.netutil;

import java.io.Serializable;

import java.net.URL;

import java.util.regex.Pattern;

import de.cismet.tools.WildcardUtils;

/**
 * Class that provides Proxy Usage.
 *
 * @author   spuhl
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class Proxy implements Serializable {

    //~ Instance fields --------------------------------------------------------

    private transient boolean enabled;
    private transient String host;
    private transient int port;
    private transient String excludedHosts;
    private transient String username;
    private transient String password;
    private transient String domain;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a Default Proxy object.
     */
    public Proxy() {
        this(false, null, 0, null, null, null, null);
    }

    /**
     * Creates a new Proxy object.
     *
     * @param  enabled        DOCUMENT ME!
     * @param  host           DOCUMENT ME!
     * @param  port           DOCUMENT ME!
     * @param  excludedHosts  DOCUMENT ME!
     * @param  username       DOCUMENT ME!
     * @param  password       DOCUMENT ME!
     * @param  domain         DOCUMENT ME!
     */
    public Proxy(
            final boolean enabled,
            final String host,
            final int port,
            final String excludedHosts,
            final String username,
            final String password,
            final String domain) {
        setEnabled(enabled);
        setHost(host);
        setPort(port);
        setExcludedHosts(excludedHosts);
        setUsername(username);
        setPassword(password);
        setDomain(domain);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  enabled  DOCUMENT ME!
     */
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEnabled() {
        return enabled;
    }

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
        return String.format(""
                        + "Enabled: %s\n"
                        + "Host: %s\n"
                        + "Port: %d\n"
                        + "ExcludedHosts: %s\n"
                        + "Username: %s\n"
                        + "Password: %s\n"
                        + "Domain: %s\n",
                enabled,
                host,
                port,
                excludedHosts,
                username,
                ((password == null) ? null : "<invisible>"),
                domain);
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
    public boolean isValid() {
        return (getHost() != null) && (getPort() > 0) && (getPort() <= 65535);
    }

    /**
     * Loads a <code>Proxy</code> instance from previously stored user preferences. If there are no host and port proxy
     * information <code>null</code> will be returned. If the return value is non-null at least the host and the port is
     * initialised. Username, Password and Domain may be null.
     *
     * @param  excludedHosts  DOCUMENT ME!
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
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isFullCredentials() {
        return (getUsername() != null) && !getUsername().trim().isEmpty()
                    && (getPassword() != null) && !getPassword().trim().isEmpty()
                    && (getDomain() != null) && !getDomain().trim().isEmpty();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   hostOrUrl  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEnabledFor(final String hostOrUrl) {
        if (!isEnabled() || !isValid()) {
            return false;
        }
        if (hostOrUrl == null) {
            return true; // not false for backwards compability
        }
        String host = hostOrUrl.trim();
        try {
            host = new URL(host).getHost();
        } catch (final Exception ex) {
        }
        if (excludedHosts != null) {
            for (final String excludedHost : excludedHosts.split(Pattern.quote("|"))) {
                if (WildcardUtils.testForMatch(host, excludedHost.trim())) {
                    return false;
                }
            }
        }
        return true;
    }
}
