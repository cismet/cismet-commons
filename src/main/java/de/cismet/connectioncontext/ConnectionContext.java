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

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class ConnectionContext extends AbstractConnectionContext<String> {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ClientConnectionContext object.
     *
     * @param  category  DOCUMENT ME!
     * @param  content   DOCUMENT ME!
     */
    protected ConnectionContext(final Category category, final String content) {
        super(category, content);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ConnectionContext createDeprecated() {
        final StackTraceElement[] elements = new Exception().getStackTrace();
        final String context = elements[1].toString();
        return create(Category.DEPRECATED, context);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ConnectionContext createDummy() {
        final StackTraceElement[] elements = new Exception().getStackTrace();
        final String context = elements[1].toString();
        return create(Category.DUMMY, context);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   category  DOCUMENT ME!
     * @param   context   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ConnectionContext create(final Category category, final String context) {
        return new ConnectionContext(category, context);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Origin getOrigin() {
        return Origin.CLIENT;
    }
}
