/***************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 *
 *              ... and it just works.
 *
 ****************************************************/
package de.cismet.remote;


import java.util.*;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.jersey.servlet.ServletContainer;

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
    private RESTRemoteControlStarter() {}

    //~ Methods ----------------------------------------------------------------

    /**
     * Starts all RESTRemoteControlMethod implementations available in the classpath.
     *
     * @param   defaultPort  port which is used, if -1 is specified by an implementation
     *
     * @throws  Exception  throws Exeption if anything went wrong
     */
    public static void initRestRemoteControlMethods(final int defaultPort) throws Exception {
        initRestRemoteControlMethods(defaultPort, false, null, null, null);
    }

    /**
     * Starts all RESTRemoteControlMethod implementations available in the classpath.
     *
     * @param   defaultPort  port which is used, if -1 is specified by an implementation
     * @param   keystore     DOCUMENT ME!
     * @param   storePasswd  DOCUMENT ME!
     * @param   keyPasswd    DOCUMENT ME!
     *
     * @throws  Exception  throws Exeption if anything went wrong
     */
    public static void initSecureRestRemoteControlMethods(
        final int defaultPort,
        final String keystore,
        final String storePasswd,
        final String keyPasswd
    ) throws Exception {
        initRestRemoteControlMethods(defaultPort, true, keystore, storePasswd, keyPasswd);
    }

    /**
     * Starts all RESTRemoteControlMethod implementations available in the classpath.
     *
     * @param   defaultPort  port which is used, if -1 is specified by an implementation
     * @param   secure       DOCUMENT ME!
     * @param   keystore     DOCUMENT ME!
     * @param   storePasswd  DOCUMENT ME!
     * @param   keyPasswd    DOCUMENT ME!
     *
     * @throws  Exception         throws Exeption if anything went wrong
     * @throws  RuntimeException  DOCUMENT ME!
     */
    private static void initRestRemoteControlMethods(
            final int defaultPort,
            final boolean secure,
            final String keystore,
            final String storePasswd,
            final String keyPasswd
    ) throws Exception {
        RESTRemoteControlMethodRegistry.gatherRemoteMethods(defaultPort);

        int count = 0;
        final Set<Integer> methodPorts = RESTRemoteControlMethodRegistry.getMethodPorts();

        for (final Integer port : methodPorts) {
            final Server server = new Server();
            ServerConnector con;

            if (secure && (keystore != null) && (storePasswd != null) && (keyPasswd != null)) {
                try {
                    final SslContextFactory.Server ssl = new SslContextFactory.Server();
                    ssl.setKeyStorePath(keystore);
                    ssl.setKeyStorePassword(storePasswd);
                    ssl.setKeyManagerPassword(keyPasswd);

                    con = new ServerConnector(server, ssl);
                } catch (Exception e) {
                    final String msg = "cannot initialise SSL connector";
                    LOG.error(msg, e);
                    throw new RuntimeException(msg, e);
                }
            } else {
                con = new ServerConnector(server);
            }

            con.setPort(port);
            server.addConnector(con);

            // Jersey Servlet
            final ServletHolder jerseyServlet = new ServletHolder(new ServletContainer());
            jerseyServlet.setInitOrder(0);

            RESTRemoteControlMethodsApplication.setPort(port);
            
            jerseyServlet.setInitParameter(
                    "jakarta.ws.rs.Application",
                    RESTRemoteControlMethodsApplication.class.getName()
            );

            jerseyServlet.setInitParameter(RESTRemoteControlMethodsApplication.PROP_PORT, String.valueOf(port));

            final ServletContextHandler context = new ServletContextHandler(
                    server, 
                    "/", 
                    ServletContextHandler.SESSIONS
            );
            context.addServlet(jerseyServlet, "/*");

            server.start();
            LOG.info("JETTY Server startet at port " + port);

            count++;
        }

        if (count == 0) {
            LOG.warn("JETTY Server not started: no RestRemoteControlMethods found in RESTRemoteControlMethodRegistry");
        }
    }
}
