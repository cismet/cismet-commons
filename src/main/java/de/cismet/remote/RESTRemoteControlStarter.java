/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.remote;

import com.sun.jersey.api.container.ContainerFactory;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.apache.log4j.Logger;

import org.openide.util.Lookup;

import java.net.InetSocketAddress;

import java.util.*;


/**
 * Utility class for starting all RESTRemoteControlMethod implementations available in the classpath.
 *
 * @author   Benjamin Friedrich (benjamin.friedrich@cismet.de)
 * @version  $Revision$, $Date$
 */
public class RESTRemoteControlStarter {

    //~ Static fields/initializers ---------------------------------------------

    private static final Integer DEFAULT_PORT_INDICATOR = Integer.valueOf(-1);

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
     * @throws  Exception  DOCUMENT ME!
     */
    public static void initRestRemoteControlMethods(final int defaultPort) throws Exception {
        final Lookup lookUp = Lookup.getDefault();
        final Lookup.Result<RESTRemoteControlMethod> result = lookUp.lookupResult(RESTRemoteControlMethod.class);
        final Collection<? extends RESTRemoteControlMethod> allRemoteMethods = result.allInstances();

        final HashMap<Integer, List<RESTRemoteControlMethod>> portMapping =
            new HashMap<Integer, List<RESTRemoteControlMethod>>();

        // group REST remote control methods by port

        List<RESTRemoteControlMethod> methods;
        Integer port;
        for (final RESTRemoteControlMethod m : allRemoteMethods) {
            port = Integer.valueOf(m.getPort());
            if (DEFAULT_PORT_INDICATOR.equals(port)) {
                port = Integer.valueOf(defaultPort);
            }

            methods = portMapping.get(port);

            if (methods == null) {
                methods = new ArrayList<RESTRemoteControlMethod>();
                portMapping.put(port, methods);
            }

            methods.add(m);
        }

        // start all REST remote control methods on their specified port

        HttpServer server;
        HttpHandler handler;

        final HashSet<Class<?>> clazzes = new HashSet<Class<?>>();

        for (final Map.Entry<Integer, List<RESTRemoteControlMethod>> entry : portMapping.entrySet()) {
            server = HttpServer.create(new InetSocketAddress(entry.getKey()), 0);

            for (final RESTRemoteControlMethod m : entry.getValue()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Starting REST remote method: " + m);
                }

                clazzes.add(m.getClass());
            }

            handler = ContainerFactory.createContainer(HttpHandler.class, clazzes);
            server.createContext("/", handler);

            server.setExecutor(null);
            server.start();
            if (LOG.isDebugEnabled()) {
                LOG.debug("All REST remote methods for port " + entry.getKey() + " have been started successfully");
            }

            clazzes.clear();
        }
    }
}
