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

import java.util.HashMap;

/**
 * DOCUMENT ME!
 *
 * @param    <C>
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public abstract class AbstractConnectionContext<C extends Object> implements ConnectionContext<C> {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient boolean LOG_DEPRECATED_FULL_STACKTRACE = true;

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
        if (Category.DEPRECATED.equals(category) && LOG_DEPRECATED_FULL_STACKTRACE) {
            getAdditionalFields().put("EXCEPTION", new Exception());
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Category getCategory() {
        return category;
    }

    @Override
    public C getContent() {
        return content;
    }

    @Override
    public HashMap<String, Object> getAdditionalFields() {
        return additionalFields;
    }
}
