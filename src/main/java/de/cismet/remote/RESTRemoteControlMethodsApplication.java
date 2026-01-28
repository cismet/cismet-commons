/***************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 *
 *              ... and it just works.
 *
 ****************************************************/
package de.cismet.remote;

import java.util.List;
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

/**
 * RESTRemoteControlMethodsApplication.
 *
 * @author   bfriedrich
 * @version  $Revision$, $Date$
 */
@ApplicationPath("/")
public class RESTRemoteControlMethodsApplication extends ResourceConfig {

    //~ Static fields/initializers ---------------------------------------------

    public static final String PROP_PORT = "de.cismet.remote.port";
    public static Integer PORT = null;

    public static void setPort(final int port) {
        PORT = port;
    }

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CustomizedClassesApplication object.
     */
    public RESTRemoteControlMethodsApplication() {
        collectServiceClasses();
        
        //register json
        register(JacksonFeature.class);
        
        //register xml will be registered automatically
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * collects Service Classes. Collects all available classes for this port.
     *
     * @param  portAsString  portasString
     */
    private void collectServiceClasses() {
        final String portAsString = PORT.toString();
        final int port = Integer.parseInt(portAsString);
        final List<RESTRemoteControlMethod> methods = RESTRemoteControlMethodRegistry.getMethodsForPort(port);

        for (final RESTRemoteControlMethod m : methods) {
            this.register(m.getClass());
        }
    }

    /**
     * Getter for Classes.
     *
     * @return  class
     */
//    @Override
//    public synchronized Set<Class<?>> getClasses() {
//        this.collectServiceClasses((String) rc.getProperty(PROP_PORT));
//        return this.clazzes;
//    }
}
