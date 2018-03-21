/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.connectioncontext;

import java.io.Serializable;

import java.util.HashMap;

import de.cismet.tools.StaticDebuggingTools;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public abstract class AbstractConnectionContext implements Serializable {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient boolean LOG_FULL_STACKTRACE = StaticDebuggingTools.checkHomeForFile(
            "fullStackTraceConnectionContext");

    public static String FIELD__CONTEXT_NAME = "contextName";
    public static final String FIELD__CLIENT_IP = "ClientIp";
    public static final String FIELD__STACKTRACE_EXCEPTION = "EXCEPTION";

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum Category {

        //~ Enum constants -----------------------------------------------------

        EDITOR, RENDERER, CATALOGUE, OPTIONS, ACTION, SEARCH, LEGACY, STARTUP, OTHER, STATIC, INSTANCE, DUMMY,
        DEPRECATED
    }

    //~ Instance fields --------------------------------------------------------

    private final Category category;
    private final HashMap<String, Object> infoFields = new HashMap<>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractConnectionContext object.
     *
     * @param  category  DOCUMENT ME!
     */
    public AbstractConnectionContext(final Category category) {
        this.category = category;
        if (LOG_FULL_STACKTRACE) {
            getInfoFields().put(FIELD__STACKTRACE_EXCEPTION, new Exception());
        }
    }

    /**
     * Creates a new AbstractConnectionContext object.
     *
     * @param  category     DOCUMENT ME!
     * @param  contextName  DOCUMENT ME!
     */
    public AbstractConnectionContext(final Category category, final String contextName) {
        this(category);
        getInfoFields().put(FIELD__CONTEXT_NAME, contextName);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Category getCategory() {
        return category;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public final HashMap<String, Object> getInfoFields() {
        return infoFields;
    }
}
