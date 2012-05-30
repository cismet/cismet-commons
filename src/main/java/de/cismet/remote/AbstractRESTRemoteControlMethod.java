/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.remote;

/**
 * DOCUMENT ME!
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
     * @param   port  DOCUMENT ME!
     * @param   path  DOCUMENT ME!
     *
     * @throws  NullPointerException      DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
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

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " path: " + this.path + " port: " + this.port;
    }
}
