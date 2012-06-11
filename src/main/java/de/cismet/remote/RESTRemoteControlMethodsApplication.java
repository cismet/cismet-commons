/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.remote;

import com.sun.jersey.api.core.ResourceConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

/**
 * DOCUMENT ME!
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
@ApplicationPath("/")
public class RESTRemoteControlMethodsApplication extends Application {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_PORT = "de.cismet.remote.port";

    //~ Instance fields --------------------------------------------------------

    @Context
    ResourceConfig rc;

    private final HashSet<Class<?>> clazzes;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CustomizedClassesApplication object.
     */
    public RESTRemoteControlMethodsApplication() {
        this.clazzes = new HashSet<Class<?>>();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  portAsString  DOCUMENT ME!
     */
    private void collectServiceClasses(final String portAsString) {
        if (this.clazzes.isEmpty()) {
            final int port = Integer.parseInt(portAsString);
            final List<RESTRemoteControlMethod> methods = RESTRemoteControlMethodRegistry.getMethodsForPort(port);

            for (final RESTRemoteControlMethod m : methods) {
                this.clazzes.add(m.getClass());
            }
        }
    }

    @Override
    public synchronized Set<Class<?>> getClasses() {
        this.collectServiceClasses((String)rc.getProperty(PROP_PORT));
        return this.clazzes;
    }
}
