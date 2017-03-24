/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.remote;

import com.sun.jersey.spi.container.servlet.ServletContainer;

import org.apache.log4j.Logger;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import java.util.*;

/**
 * Utility class for starting all RESTRemoteControlMethod implementations available in the classpath.
 *
 * @author   Benjamin Friedrich (benjamin.friedrich@cismet.de)
 * @version  $Revision$, $Date$
 */
public class RESTRemoteControlStarter {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(RESTRemoteControlStarter.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Avoids Creation of a new RestRemoteControlStarter object.
     */
    private RESTRemoteControlStarter() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Starts all RESTRemoteControlMethod implementations available in the classpath.
     *
     * @param   defaultPort  port which is used, if -1 is specified by an implementation
     *
     * @throws  Exception  throws Exeption if anything went wrong
     */
    public static void initRestRemoteControlMethods(final int defaultPort) throws Exception {
        RESTRemoteControlMethodRegistry.gatherRemoteMethods(defaultPort);

        int count = 0;
        final Set<Integer> methodPorts = RESTRemoteControlMethodRegistry.getMethodPorts();
        for (final Integer port : methodPorts) {
            final Server jettyServer = new Server(port);

            final ServletHolder jerseyServlet = new ServletHolder(ServletContainer.class);
            jerseyServlet.setInitOrder(0);
            jerseyServlet.setInitParameter(
                "javax.ws.rs.Application",
                RESTRemoteControlMethodsApplication.class.getName());

            jerseyServlet.setInitParameter(RESTRemoteControlMethodsApplication.PROP_PORT, String.valueOf(port));

            final Context context = new Context(jettyServer, "/", Context.SESSIONS);
            context.addServlet(jerseyServlet, "/*");

            jettyServer.start();
            // jettyServer.join();
            LOG.info("JETTY Server startet at port " + port);

            count++;
        }

        if (count == 0) {
            LOG.warn("JETTY Server not started: no RestRemoteControlMethods found in RESTRemoteControlMethodRegistry");
        }
    }
}
