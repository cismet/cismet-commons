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

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import de.cismet.remote.RESTRemoteControlMethod;
import de.cismet.remote.RESTRemoteControlStarter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.Ignore;
import org.openide.util.Lookup;

/**
 * DOCUMENT ME!
 *
 * @author pascal.dihe@cismet.de
 * @version $Revision$, $Date$
 */
public class RESTRemoteControlTest {

    //~ Constructors -----------------------------------------------------------
    private boolean initialized = false;

    /**
     * Creates a new ProxyTest object.
     */
    public RESTRemoteControlTest() {
    }
        //~ Methods ----------------------------------------------------------------
    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
        BasicConfigurator.configure();
        Class.forName("de.cismet.remote.test.SimpleJsonRemoteMethod");
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * DOCUMENT ME!
     */
    @Before
    public void setUp() throws Exception {
        if (!initialized) {
            RESTRemoteControlStarter.initRestRemoteControlMethods(31337);
            initialized = true;
        }
    }

    /**
     * DOCUMENT ME!
     */
    @After
    public void tearDown() {
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private String getCurrentMethodName() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }

    @Test
    @Ignore
    public void testSimpleJsonRemoteMethod() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());
        
        assertFalse("at least one RESTRemoteControlMethod in Lookup registered", 
                Lookup.getDefault().lookupResult(RESTRemoteControlMethod.class).allItems().isEmpty());

        Client client = Client.create();

        WebResource webResource = client
                .resource("http://127.0.0.1:31337/simpleJsonRemoteMethod");

        ClientResponse response = webResource.accept("application/json")
                .get(ClientResponse.class);

        assertEquals(200, response.getStatus());

        String output = response.getEntity(String.class);

        System.out.println("Output from Server .... \n");
        System.out.println(output);
        
        assertEquals("[1,2,3,4,5,6,7,8,9]", output);
        
        final ObjectMapper mapper = new ObjectMapper();
        JavaType type = mapper.getTypeFactory().
            constructCollectionType(
                ArrayList.class, 
               Integer.class);
        
        Object returnObject = mapper.readValue(output, type);
        assertTrue(returnObject.getClass().isAssignableFrom(ArrayList.class));
        
        assertEquals(5, ((ArrayList<Integer>)returnObject).get(4).intValue());

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
