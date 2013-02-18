/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.remote;

import com.sun.grizzly.http.embed.GrizzlyWebServer;
import com.sun.grizzly.http.servlet.ServletAdapter;
import com.sun.jersey.spi.container.servlet.ServletContainer;

import org.apache.log4j.Logger;

import java.net.URL;

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

        GrizzlyWebServer webServer;
        ServletAdapter jerseyAdapter;

        final Set<Integer> methodPorts = RESTRemoteControlMethodRegistry.getMethodPorts();
        for (final Integer port : methodPorts) {
            webServer = new GrizzlyWebServer(port);

            jerseyAdapter = new ServletAdapter();
            jerseyAdapter.setServletInstance(new ServletContainer());
            jerseyAdapter.setContextPath("/");
            jerseyAdapter.addInitParameter(
                "javax.ws.rs.Application",
                RESTRemoteControlMethodsApplication.class.getName());

            jerseyAdapter.addInitParameter(RESTRemoteControlMethodsApplication.PROP_PORT, String.valueOf(port));

            webServer.addGrizzlyAdapter(jerseyAdapter, new String[] { "/" });

            webServer.start();
        }
    }
}
