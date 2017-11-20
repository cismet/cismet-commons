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

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.security.SslSocketConnector;
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
    public static void initSecureRestRemoteControlMethods(final int defaultPort,
            final String keystore,
            final String storePasswd,
            final String keyPasswd) throws Exception {
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
    private static void initRestRemoteControlMethods(final int defaultPort,
            final boolean secure,
            final String keystore,
            final String storePasswd,
            final String keyPasswd) throws Exception {
        RESTRemoteControlMethodRegistry.gatherRemoteMethods(defaultPort);

        int count = 0;
        final Set<Integer> methodPorts = RESTRemoteControlMethodRegistry.getMethodPorts();
        for (final Integer port : methodPorts) {
            Connector con = new SocketConnector(); // unverschluesselte Verbindung!!!
            final Server server = new Server();

            if (secure && (keystore != null) && (storePasswd != null) && (keyPasswd != null)) {
                try {
                    final SslSocketConnector ssl = new SslSocketConnector();
                    ssl.setMaxIdleTime(30000);
                    ssl.setKeystore(keystore);
                    ssl.setPassword(storePasswd);
                    ssl.setKeyPassword(keyPasswd);
                    con = ssl;
                } catch (final Exception e) {
                    final String message = "cannot initialise SSL connector"; // NOI18N
                    LOG.error(message, e);
                    throw new RuntimeException(message, e);
                }
            }
            con.setPort(port);

            server.addConnector(con);
            final ServletHolder jerseyServlet = new ServletHolder(ServletContainer.class);
            jerseyServlet.setInitOrder(0);
            jerseyServlet.setInitParameter(
                "javax.ws.rs.Application",
                RESTRemoteControlMethodsApplication.class.getName());

            jerseyServlet.setInitParameter(RESTRemoteControlMethodsApplication.PROP_PORT, String.valueOf(port));

            final Context context = new Context(server, "/", Context.SESSIONS);
            context.addServlet(jerseyServlet, "/*");

            server.start();
            // jettyServer.join();
            LOG.info("JETTY Server startet at port " + port);

            count++;
        }

        if (count == 0) {
            LOG.warn("JETTY Server not started: no RestRemoteControlMethods found in RESTRemoteControlMethodRegistry");
        }
    }
}
