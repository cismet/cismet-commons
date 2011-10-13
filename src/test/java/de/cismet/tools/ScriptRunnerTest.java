/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import java.util.Properties;

import de.cismet.remotetesthelper.RemoteTestHelperService;

import de.cismet.remotetesthelper.ws.rest.RemoteTestHelperClient;

import static org.junit.Assert.*;
/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public class ScriptRunnerTest {

    //~ Static fields/initializers ---------------------------------------------

    private static final String TEST_DB_NAME = "scriptrunner_test_db";
    private static final RemoteTestHelperService SERVICE = new RemoteTestHelperClient();

    private static Connection con;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @throws  Throwable              DOCUMENT ME!
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    @BeforeClass
    public static void setUpClass() throws Throwable {
        final Properties p = new Properties();
        p.put("log4j.appender.Remote", "org.apache.log4j.net.SocketAppender");
        p.put("log4j.appender.Remote.remoteHost", "localhost");
        p.put("log4j.appender.Remote.port", "4445");
        p.put("log4j.appender.Remote.locationInfo", "true");
        p.put("log4j.rootLogger", "ALL,Remote");
        org.apache.log4j.PropertyConfigurator.configure(p);

        if (!Boolean.valueOf(SERVICE.initCidsSystem(TEST_DB_NAME))) {
            throw new IllegalStateException("cannot initilise test db");
        }

        Class.forName("org.postgresql.Driver");

        con = DriverManager.getConnection("jdbc:postgresql://kif:5432/" + TEST_DB_NAME, "postgres", "x");
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Throwable              Exception DOCUMENT ME!
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    @AfterClass
    public static void tearDownClass() throws Throwable {
        if (con != null) {
            con.close();
        }

        if (!Boolean.valueOf(SERVICE.dropDatabase(TEST_DB_NAME))) {
            throw new IllegalStateException("could not drop test db");
        }
    }

    /**
     * DOCUMENT ME!
     */
    @Before
    public void setUp() {
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
     * @return  DOCUMENT ME!
     */
    private String getCurrentMethodName() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test
    public void testRunScriptMultiLineQuoteWithDelim() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        final ScriptRunner sr = new ScriptRunner(con, true, true);

        sr.runScript(new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("ScriptRunnerTest_multilineQuote_delim.sql"))));

        final ResultSet set = con.createStatement()
                    .executeQuery("SELECT * FROM pg_proc WHERE proname = 'calculate_points_gewaesserumfeld_func'");

        assertTrue("function was not properly inserted", set.next());
    }
}
