/**
 * *************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 *
 *              ... and it just works.
 *
 ***************************************************
 */
package de.cismet.remote.test;

import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import de.cismet.commons.security.WebDavClient;
import de.cismet.remote.RESTRemoteControlMethod;
import de.cismet.remote.RESTRemoteControlMethodRegistry;
import de.cismet.remote.RESTRemoteControlStarter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import jakarta.ws.rs.core.MediaType;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openide.util.Lookup;

/**
 * DOCUMENT ME!
 *
 * @author pascal.dihe@cismet.de
 * @version $Revision$, $Date$
 */
public class RESTRemoteControlTest {
    static {
        Logger.getRootLogger().setLevel(Level.INFO);
        BasicConfigurator.configure();
    }

    //~ Constructors -----------------------------------------------------------
    /**
     * Creates a new ProxyTest object.
     */
    public RESTRemoteControlTest() {}

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println(
            ">>> javax.xml.parsers.DocumentBuilderFactory: " +
            System.getProperty("javax.xml.parsers.DocumentBuilderFactory")
        );

        System.out.println("RESTRemoteControlTest ===================================");
        RESTRemoteControlStarter.initRestRemoteControlMethods(31337);
        assertFalse(
            "at least one RESTRemoteControlMethod in Lookup registered",
            Lookup.getDefault().lookupResult(RESTRemoteControlMethod.class).allItems().isEmpty()
        );
        assertFalse(
            "RESTRemoteControlMethodRegistry hasMethodsInformation",
            RESTRemoteControlMethodRegistry.getMethodPorts().isEmpty()
        );
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    @AfterClass
    public static void tearDownClass() throws Exception {}

    /**
     * DOCUMENT ME!
     *
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {}

    /**
     * DOCUMENT ME!
     */
    @After
    public void tearDown() {}

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private String getCurrentMethodName() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }

    @Test
    public void test010WebDavClient() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        final WebDavClient webDavClient = new WebDavClient(null, null, null, false);

        assertEquals(200, webDavClient.getStatusCode("http://127.0.0.1:31337/simpleJsonRemoteMethod"));
        assertEquals(200, webDavClient.getStatusCode("http://127.0.0.1:31338/jsonRemoteMethod"));
        assertEquals(200, webDavClient.getStatusCode("http://127.0.0.1:31338/xmlRemoteMethod"));
        assertEquals(404, webDavClient.getStatusCode("http://127.0.0.1:31338/notFound"));
    }

    @Test
    public void test020SimpleJsonRemoteMethod() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        final Client client = ClientBuilder.newClient();

        final WebTarget target = client.target("http://127.0.0.1:31337/simpleJsonRemoteMethod");

        final Response response = target.request(MediaType.APPLICATION_JSON).get();

        assertEquals(200, response.getStatus());

        String output = response.readEntity(String.class);

        System.out.println("Output from Server .... \n");
        System.out.println(output);

        assertEquals("[1,2,3,4,5,6,7,8,9]", output);

        final ObjectMapper mapper = new ObjectMapper();
        final JavaType type = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Integer.class);

        final Object returnObject = mapper.readValue(output, type);
        assertTrue(returnObject.getClass().isAssignableFrom(ArrayList.class));

        assertEquals(5, ((ArrayList<Integer>) returnObject).get(4).intValue());
    }

    @Test
    public void test030JsonRemoteMethod() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        final Client client = ClientBuilder.newClient();

        final WebTarget target = client.target("http://127.0.0.1:31338/jsonRemoteMethod");

        final Response response = target.request(MediaType.APPLICATION_JSON).get();

        assertEquals(200, response.getStatus());

        final RemoteMethodBean output = response.readEntity(RemoteMethodBean.class);

        assertEquals("String", output.getString());
        assertEquals(31.337d, output.getDbl(), 0.0d);
        assertEquals(true, output.isBoolValue());
    }

    @Test
    public void test040XmlRemoteMethod() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        final Client client = ClientBuilder.newClient();
        
        final WebTarget target = client.target("http://127.0.0.1:31338/xmlRemoteMethod");
        
        Response response = target.request(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatus());

        String outputString = response.readEntity(String.class);
        System.out.println(outputString);
        
        assertEquals(
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><remoteMethodBean><boolValue>true</boolValue><dbl>31.337</dbl><string>String</string></remoteMethodBean>",
            outputString
        );

        response = target.request(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatus());
        RemoteMethodBean outputBean = response.readEntity(RemoteMethodBean.class);
        assertEquals("String", outputBean.getString());
        assertEquals(31.337d, outputBean.getDbl(), 0.0d);
        assertEquals(true, outputBean.isBoolValue());
    }

    public static void main(final String[] args) {
        try {
            BasicConfigurator.configure();

            assertFalse(Lookup.getDefault().lookupResult(RESTRemoteControlMethod.class).allItems().isEmpty());
            RESTRemoteControlStarter.initRestRemoteControlMethods(31337);

            System.out.println("Press return to continue.");
            final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            in.readLine();
        } catch (Exception ex) {
            System.exit(1);
        }
    }
}
