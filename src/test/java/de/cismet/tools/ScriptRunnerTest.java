/*
 * Copyright (C) 2011 cismet GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cismet.tools;

import de.cismet.remotetesthelper.ws.rest.RemoteTestHelperClient;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author bfriedrich
 */
public class ScriptRunnerTest {
    
    private ScriptRunner runner;
    
    private static Connection CON;
    private static Statement  STMT;
    
    private static final String TEST_DB_NAME = "simple_scriptrunner_test_db";
    private static final RemoteTestHelperClient SERVICE = new RemoteTestHelperClient();
    
    
    public ScriptRunnerTest() {
    }

   @BeforeClass
    public static void setUpClass() throws Exception 
    {
        final Properties p = new Properties();
        p.put("log4j.appender.Remote", "org.apache.log4j.net.SocketAppender");
        p.put("log4j.appender.Remote.remoteHost", "localhost");
        p.put("log4j.appender.Remote.port", "4445");
        p.put("log4j.appender.Remote.locationInfo", "true");
        p.put("log4j.rootLogger", "ALL,Remote");
        org.apache.log4j.PropertyConfigurator.configure(p);

        if (! Boolean.valueOf(SERVICE.initCidsSystem(TEST_DB_NAME))) 
        {
            throw new IllegalStateException("cannot initilise test db");
        }
        
        CON  = SERVICE.getConnection(TEST_DB_NAME);
        STMT = CON.createStatement();    
    }

    @AfterClass
    public static void tearDownClass() throws Exception 
    {
        STMT.close();
        CON.close();
        
        if (! Boolean.valueOf(SERVICE.dropDatabase(TEST_DB_NAME))) 
        {
            throw new IllegalStateException("could not drop test db");
        }
    }
    
    @Before
    public void setUp() 
    {
        this.runner = new ScriptRunner(CON, false, true);
    }
    
    @Test
    public void testSelect() throws Exception
    {
        final StringReader reader = new StringReader("SELECT * FROM cs_usr;");
        this.runner.runScript(reader);
    }

    @Test
    public void testMultiLineStmt() throws Exception
    {
        final StringReader reader = new StringReader("SELECT * \nFROM \ncs_usr;");
        this.runner.runScript(reader);
    }
    
    @Test(expected=SQLException.class)
    public void testSelectWithoutSemicolon() throws Exception
    {
        final StringReader reader = new StringReader("SELECT * FROM cs_usr");
        this.runner.runScript(reader);
    }
    
    
    @Test(expected=SQLException.class)
    public void testSelectWithNonExistingTable() throws Exception
    {
        final StringReader reader = new StringReader("SELECT * FROM non_existing_table;");
        this.runner.runScript(reader);
    }
    
    @Test
    public void testEmptySelect() throws Exception
    {
        final StringReader reader = new StringReader("SELECT * FROM cs_usr where login_name = 'script_runner_test';");
        this.runner.runScript(reader);
    }
    
    @Test
    public void testCreateTable() throws Exception
    {
        StringReader reader = new StringReader("CREATE TABLE sript_runner_test (key INTEGER);");
        this.runner.runScript(reader);
        
        // SQLException indicates if table exists or not
        reader = new StringReader("SELECT * FROM sript_runner_test;");
        this.runner.runScript(reader);
    }
    
    @Test(expected=SQLException.class)
    public void testInvalidStmt() throws Exception
    {
        StringReader reader = new StringReader("CREAT TABLE sript_runner_test (key INTEGER);");
        this.runner.runScript(reader);
    }
    
    @Test(expected=SQLException.class)
    public void testCommentedStmt() throws Exception
    {
        StringReader reader = new StringReader("-- CREATE TABLE sript_runner_test_xxx (key INTEGER);");
        this.runner.runScript(reader);
        
        // SQLException indicates if table exists or not
        reader = new StringReader("SELECT * FROM sript_runner_test_xxx;");
        this.runner.runScript(reader);
    }
    
    @Test
    public void testNoAutoCommitWithoutLineBreak() throws Exception
    {
        // no autocommit has already been set by method setUp()
        
        StringReader reader;
        
        try
        {
            // execute table creation of "sript_runner_test2" followed by an invalid stmt
            reader = new StringReader("CREATE TABLE sript_runner_test2 (key INTEGER);" +
                                      "CREAT TABLE sript_runner_test_xxx (key INTEGER);");
            this.runner.runScript(reader);      
            fail();
        }
        catch(SQLException e){}
        
        // as "no autocommit" is set, it is expected that table "sript_runner_test2" does not exist
        try
        {
            reader = new StringReader("SELECT * FROM sript_runner_test2;");
            this.runner.runScript(reader);
            fail();
        }
        catch(SQLException e){}
    }
    
    
    @Test
    public void testAutoCommitWithoutLineBreak() throws Exception
    {
        this.runner = new ScriptRunner(CON, true, true);
        
        StringReader reader;
        
        try
        {
            // execute table creation of "sript_runner_test2" followed by an invalid stmt
            reader = new StringReader("CREATE TABLE sript_runner_test2 (key INTEGER);" +
                                      "CREAT TABLE sript_runner_test_xxx (key INTEGER);");
            this.runner.runScript(reader);      
            fail();
        }
        catch(SQLException e){}
        
        // as "autocommit" is set, it is expected that table "sript_runner_test2" does exist
        // (so no exception should arise)
        reader = new StringReader("SELECT * FROM sript_runner_test2;");
        this.runner.runScript(reader);
    }
    
    
    @Test
    public void testNoAutoCommitWithLineBreak() throws Exception
    {
        // no autocommit has already been set by method setUp()
        
        StringReader reader;
        
        try
        {
            // execute table creation of "sript_runner_test2" followed by an invalid stmt
            reader = new StringReader("CREATE TABLE sript_runner_test3 (key INTEGER);\n" +
                                      "CREAT TABLE sript_runner_test_xxx (key INTEGER);");
            this.runner.runScript(reader);      
            fail();
        }
        catch(SQLException e){}
        
        // as "no autocommit" is set, it is expected that table "sript_runner_test2" does not exist
        try
        {
            reader = new StringReader("SELECT * FROM sript_runner_test3;");
            this.runner.runScript(reader);
            fail();
        }
        catch(SQLException e){}
    }
    
    
    @Test
    public void testAutoCommitWithLineBreak() throws Exception
    {
        this.runner = new ScriptRunner(CON, true, true);
        
        StringReader reader;
        
        try
        {
            // execute table creation of "sript_runner_test2" followed by an invalid stmt
            reader = new StringReader("CREATE TABLE sript_runner_test2 (key INTEGER);\n" +
                                      "CREAT TABLE sript_runner_test_xxx (key INTEGER);");
            this.runner.runScript(reader);      
            fail();
        }
        catch(SQLException e){}
        
        // as "autocommit" is set, it is expected that table "sript_runner_test2" does exist
        // (so no exception should arise)
        reader = new StringReader("SELECT * FROM sript_runner_test2;");
        this.runner.runScript(reader);
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

        final ScriptRunner sr = new ScriptRunner(CON, true, true);

        sr.runScript(new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("ScriptRunnerTest_multilineQuote_delim.sql"))));

        final ResultSet set = CON.createStatement()
                    .executeQuery("SELECT * FROM pg_proc WHERE proname = 'calculate_points_gewaesserumfeld_func'");

        assertTrue("function was not properly inserted", set.next());
    }
    @After
    public void tearDown() throws Exception
    {
    
    }
}