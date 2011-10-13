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
    private boolean fullLineDelimiter = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Default constructor.
     *
     * @param  connection   DOCUMENT ME!
     * @param  autoCommit   DOCUMENT ME!
     * @param  stopOnError  DOCUMENT ME!
     */
    public ScriptRunner(final Connection connection, final boolean autoCommit, final boolean stopOnError) {
        this.connection = connection;
        this.autoCommit = autoCommit;
        this.stopOnError = stopOnError;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  delimiter          DOCUMENT ME!
     * @param  fullLineDelimiter  DOCUMENT ME!
     */
    public void setDelimiter(final String delimiter, final boolean fullLineDelimiter) {
        this.delimiter = delimiter;
        this.fullLineDelimiter = fullLineDelimiter;
    }

    /**
     * Setter for logWriter property.
     *
     * @param  logWriter  - the new value of the logWriter property
     */
    public void setLogWriter(final PrintWriter logWriter) {
        this.logWriter = logWriter;
    }

    /**
     * Setter for errorLogWriter property.
     *
     * @param  errorLogWriter  - the new value of the errorLogWriter property
     */
    public void setErrorLogWriter(final PrintWriter errorLogWriter) {
        this.errorLogWriter = errorLogWriter;
    }

    /**
     * Runs an SQL script (read in using the Reader parameter).
     *
     * @param   reader  - the source of the script
     *
     * @throws  IOException       DOCUMENT ME!
     * @throws  SQLException      DOCUMENT ME!
     * @throws  RuntimeException  DOCUMENT ME!
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
        StringBuffer command = null;
        try {
            final LineNumberReader lineReader = new LineNumberReader(reader);
            String line = null;
            boolean quoted = false;
            while ((line = lineReader.readLine()) != null) {
                if (command == null) {
                    command = new StringBuffer();
                }
                final String trimmedLine = line.trim();
                quoted = quoted == evenQuotes(trimmedLine);

                if (!quoted && trimmedLine.startsWith("--")) {      // NOI18N
                    println(trimmedLine);
                } else if (!quoted && ((trimmedLine.isEmpty())
                                || trimmedLine.startsWith("//"))) { // NOI18N
                    // Do nothing
                } else if (!quoted && ((trimmedLine.length() < 1)
                                || trimmedLine.startsWith("--"))) { // NOI18N
                    // Do nothing
                } else if (!quoted
                            && ((!fullLineDelimiter
                                    && trimmedLine.endsWith(getDelimiter()))
                                || (fullLineDelimiter
                                    && trimmedLine.equals(getDelimiter())))) {
                    command.append(line);
                    command.append(" "); // NOI18N
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
                            printlnError("Error executing: " + command); // NOI18N
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
                        for (int i = 0; i < cols; i++) {
                            final String name = md.getColumnLabel(i);
                            print(name + "\t");      // NOI18N
                        }
                        println("");
                        while (rs.next()) {
                            for (int i = 0; i < cols; i++) {
                                final String value = rs.getString(i);
                                print(value + "\t"); // NOI18N
                            }
                            println("");
                        }
                    }

                    command = null;
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
            if (!autoCommit) {
                conn.commit();
            }
        } catch (final SQLException e) {
            e.fillInStackTrace();
            printlnError("Error executing: " + command); // NOI18N
            printlnError(e);
            throw e;
        } catch (final IOException e) {
            e.fillInStackTrace();
            printlnError("Error executing: " + command); // NOI18N
            printlnError(e);
            throw e;
        } finally {
            conn.rollback();
            flush();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   s  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
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
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getDelimiter() {
        return delimiter;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  o  DOCUMENT ME!
     */
    private void print(final Object o) {
        if (logWriter != null) {
            System.out.print(o);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  o  DOCUMENT ME!
     */
    private void println(final Object o) {
        if (logWriter != null) {
            logWriter.println(o);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  o  DOCUMENT ME!
     */
    private void printlnError(final Object o) {
        if (errorLogWriter != null) {
            errorLogWriter.println(o);
        }
    }

    /**
     * DOCUMENT ME!
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
