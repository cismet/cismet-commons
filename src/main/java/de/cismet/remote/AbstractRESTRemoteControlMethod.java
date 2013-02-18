/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.remote;

/**
 * AbstractRestRemoteControlMethod Class.
 *
 * @author   Benjamin Friedrich (benjamin.friedrich@cismet.de)
 * @version  $Revision$, $Date$
 */
public abstract class AbstractRESTRemoteControlMethod implements RESTRemoteControlMethod {

    //~ Instance fields --------------------------------------------------------

    private final int port;
    private final String path;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractRestRemoteControlMethod object.
     *
     * @param   port  port
     * @param   path  path
     *
     * @throws  NullPointerException      "path must not be null"
     * @throws  IllegalArgumentException  "path must not be empty" or "path has to start with '/'"
     */
    public AbstractRESTRemoteControlMethod(final int port, final String path) {
        if (path == null) {
            throw new NullPointerException("path must not be null");
        }

        if (path.trim().isEmpty()) {
            throw new IllegalArgumentException("path must not be empty");
        }

        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("path has to start with '/'");
        }

        this.port = port;
        this.path = path;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Getter for Port.
     *
     * @return  <code>port</code>
     */
    @Override
    public int getPort() {
        return this.port;
    }

    /**
     * Getter for Path.
     *
     * @return  <code>path</code>
     */
    @Override
    public String getPath() {
        return this.path;
    }

    /**
     * return as String.
     *
     * @return  <code>Class</code> + <code>name</code> + " path: " + <code>path</code> + " port: " + <code>port</code>
     */
    @Override
    public String toString() {
        return this.getClass().getName() + " path: " + this.path + " port: " + this.port;
    }
}
