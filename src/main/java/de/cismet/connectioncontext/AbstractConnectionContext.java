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

/**
 * DOCUMENT ME!
 *
 * @param    <C>
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public abstract class AbstractConnectionContext<C extends Object> implements Serializable {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient boolean LOG_DEPRECATED_FULL_STACKTRACE = true;

    public static final String ADDITIONAL_FIELD__CLIENT_IP = "ClientIp";
    public static final String ADDITIONAL_FIELD__STACKTRACE_EXCEPTION = "EXCEPTION";

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum Origin {

        //~ Enum constants -----------------------------------------------------

        SERVER, CLIENT, UNKNOWN
    }

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
    private final C content;
    private final HashMap<String, Object> additionalFields = new HashMap<>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractConnectionContext object.
     *
     * @param  category  DOCUMENT ME!
     * @param  content   DOCUMENT ME!
     */
    public AbstractConnectionContext(final Category category, final C content) {
        this.category = category;
        this.content = content;
        if (LOG_DEPRECATED_FULL_STACKTRACE) {
            getAdditionalFields().put(ADDITIONAL_FIELD__STACKTRACE_EXCEPTION, new Exception());
        }
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
    public C getContent() {
        return content;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public HashMap<String, Object> getAdditionalFields() {
        return additionalFields;
    }
}
