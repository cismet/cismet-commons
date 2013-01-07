/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * Slightly modified version of the com.ibatis.common.jdbc.ScriptRunner class
 * from the iBATIS Apache project. Only removed dependency on Resource class
 * and a constructor
 */
/*
 *  Copyright 2004 Clinton Begin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.cismet.tools;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Reader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;

/**
 * Tool to run database scripts.
 *
 * @version  $Revision$, $Date$
 */
public class ScriptRunner {

    //~ Static fields/initializers ---------------------------------------------

    private static final String DEFAULT_DELIMITER = ";";

    //~ Instance fields --------------------------------------------------------

    private Connection connection;

    private boolean stopOnError;
    private boolean autoCommit;

    private PrintWriter logWriter = new PrintWriter(System.out);
    private PrintWriter errorLogWriter = new PrintWriter(System.err);

    private String delimiter = DEFAULT_DELIMITER;

    //~ Constructors -----------------------------------------------------------

    /**
     * Default constructor.
     *
     * @param  connection   <code>Connection</code>
     * @param  autoCommit   Enables/Disables  autoCommit
     * @param  stopOnError  Enables/Disables stopOnError
     */
    public ScriptRunner(final Connection connection, final boolean autoCommit, final boolean stopOnError) {
        this.connection = connection;
        this.autoCommit = autoCommit;
        this.stopOnError = stopOnError;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Setter for <code>delimiter</code> property
     *
     * @param  delimiter  new vslue of the <code>delimiter</code> property
     */
    public void setDelimiter(final String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Setter for <code>logWriter</code> property.
     *
     * @param  logWriter  - the new value of the <code>logWriter</code> property
     */
    public void setLogWriter(final PrintWriter logWriter) {
        this.logWriter = logWriter;
    }

    /**
     * Setter for <code>errorLogWriter</code> property.
     *
     * @param  errorLogWriter  - the new value of the <code>errorLogWriter</code> property
     */
    public void setErrorLogWriter(final PrintWriter errorLogWriter) {
        this.errorLogWriter = errorLogWriter;
    }

    /**
     * Runs an SQL script (read in using the Reader parameter).
     *
     * @param   reader  - the source of the script
     *
     * @throws  IOException       if there is an error reading from the Reader
     * @throws  SQLException      if any SQL errors occur
     * @throws  RuntimeException  "Error running script. Cause: "
     */
    public void runScript(final Reader reader) throws IOException, SQLException {
        try {
            final boolean originalAutoCommit = connection.getAutoCommit();
            try {
                if (originalAutoCommit != this.autoCommit) {
                    connection.setAutoCommit(this.autoCommit);
                }
                runScript(connection, reader);
            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }
        } catch (IOException e) {
            throw e;
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error running script.  Cause: " + e, e);
        }
    }

    /**
     * Runs an SQL script (read in using the Reader parameter) using the connection passed in.
     *
     * @param   conn    - the connection to use for the script
     * @param   reader  - the source of the script
     *
     * @throws  IOException   if there is an error reading from the Reader
     * @throws  SQLException  if any SQL errors occur
     */
    private void runScript(final Connection conn, final Reader reader) throws IOException, SQLException {
        StringBuilder command = new StringBuilder();

        try {
            final ArrayList<String> linesToBeConsidered = new ArrayList<String>();
            final LineNumberReader lineReader = new LineNumberReader(reader);

            boolean quoted = false;

            String line;
            while ((line = lineReader.readLine()) != null) {
                if (!linesToBeConsidered.isEmpty()) {
                    linesToBeConsidered.clear();
                }

                line = line.trim();

                // -- Prepare statements in read-in line for execution

                String split;
                final String[] splits = line.trim().split(this.delimiter);
                for (int i = 0; i < splits.length; i++) {
                    split = splits[i].trim();

                    if (!split.isEmpty()) {
                        if (split.startsWith("--")) {
                            // all following splits are ignored
                            // example: Stmt1; -- Stmt2; Stmt3\n
                            break;
                        } else {
                            if (line.endsWith(this.delimiter)) {
                                // so each stmt should have a delimiter (we have a syntax error otherwise)
                                // e.g. "stmt1; stmt2; stmt3;"
                                linesToBeConsidered.add(split + this.delimiter);
                            } else {
                                if (i == (splits.length - 1)) {
                                    // the stmt in the last split has not semicolon (so it might be a muli-line stmt)
                                    // e.g. "stmt1; stmt2; create table my_table ("
                                    linesToBeConsidered.add(split);
                                } else {
                                    // each stmt (if it is not the last one which might be a multi-line stmt)
                                    // should have a delimiter (we have a syntax error otherwise)
                                    // e.g. "stmt1; stmt2; create table my_table ("
                                    linesToBeConsidered.add(split + this.delimiter);
                                }
                            }
                        }
                    }
                }

                // -- end of preparation

                String cmdLine;
                final int numLines = linesToBeConsidered.size();
                for (int j = 0; j < numLines; j++) {
                    cmdLine = linesToBeConsidered.get(j);

                    quoted = quoted == evenQuotes(cmdLine);

                    if (!quoted && cmdLine.endsWith(this.delimiter)) {
                        command.append(cmdLine);
                        command.append(" ");
                        final Statement statement = conn.createStatement();

                        println(command);

                        boolean hasResults = false;
                        if (stopOnError) {
                            hasResults = statement.execute(command.toString());
                        } else {
                            try {
                                statement.execute(command.toString());
                            } catch (SQLException e) {
                                e.fillInStackTrace();
                                printlnError("Error executing: " + command);
                                printlnError(e);
                            }
                        }

                        if (autoCommit && !conn.getAutoCommit()) {
                            conn.commit();
                        }

                        final ResultSet rs = statement.getResultSet();
                        if (hasResults && (rs != null)) {
                            final ResultSetMetaData md = rs.getMetaData();
                            final int cols = md.getColumnCount();
                            String name;
                            for (int i = 1; i <= cols; i++) {
                                name = md.getColumnLabel(i);
                                print(name + '\t');
                            }
                            println("");

                            String value;
                            while (rs.next()) {
                                for (int i = 1; i <= cols; i++) {
                                    value = rs.getString(i);
                                    print(value + '\t');
                                }
                                println("");
                            }
                        }

                        command = new StringBuilder();
                        try {
                            statement.close();
                        } catch (Exception e) {
                            // Ignore to workaround a bug in Jakarta DBCP
                        }
                    } else {
                        command.append(line);
                        command.append('\n');
                    }
                }
            }

            if (command.length() != 0) {
                throw new SQLException("Command:\n"
                            + command
                            + "\nhas not been executed. Have you forgot to set a final " + this.delimiter);
            }

            if (!autoCommit) {
                conn.commit();
            }
        } catch (final SQLException e) {
            e.fillInStackTrace();
            printlnError("Error executing: " + command); // NOI18N
            printlnError(e);
            conn.rollback();
            throw e;
        } catch (final IOException e) {
            e.fillInStackTrace();
            printlnError("Error executing: " + command); // NOI18N
            printlnError(e);
            conn.rollback();
            throw e;
        } finally {
            flush();
        }
    }

    /**
     * Counts the "'" in the specified <code>String</code> and tests whether the number is even or not.
     *
     * @param   s  <code>String</code> which should be tested
     *
     * @return  True, if the Number is even.
     */
    private boolean evenQuotes(final String s) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if ('\'' == s.charAt(i)) {
                count++;
            }
        }

        return (count % 2) == 0;
    }

    /**
     * Gettter for delimiter
     *
     * @return  delimiter
     */
    private String getDelimiter() {
        return delimiter;
    }

    /**
     * If logWriter is Empty print the Object in System Console
     *
     * <p>>>>>>>> .r4704</p>
     *
     * @param  o  <code>Object</code> which is going to be printed
     */
    private void print(final Object o) {
        if (logWriter != null) {
            System.out.print(o);
        }
    }

    /**
     * If logWriter is Empty print the Object in logWriter.Does a Line Break and flushes.
     *
     * @param  o  <code>Object</code> which is going to be printed
     */
    private void println(final Object o) {
        if (logWriter != null) {
            logWriter.println(o);
            logWriter.flush();
        }
    }

    /**
     * If errorLogWriter is Empty print the Object in errorLogWriter.Does a Line Break and flushes.
     *
     * @param  o  <code>Object</code> which is going to be printed
     */
    private void printlnError(final Object o) {
        if (errorLogWriter != null) {
            errorLogWriter.println(o);
            errorLogWriter.flush();
        }
    }

    /**
     * Flushes the Streams
     */
    private void flush() {
        if (logWriter != null) {
            logWriter.flush();
        }
        if (errorLogWriter != null) {
            errorLogWriter.flush();
        }
    }
}
