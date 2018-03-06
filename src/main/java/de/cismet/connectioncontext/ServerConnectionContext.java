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
public class ServerConnectionContext extends AbstractConnectionContext<String> {

    //~ Instance fields --------------------------------------------------------

    private final Origin origin;
    private final String clientAddress;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ServerConnectionContext object.
     *
     * @param  origin      DOCUMENT ME!
     * @param  category    DOCUMENT ME!
     * @param  content     DOCUMENT ME!
     * @param  clientHost  DOCUMENT ME!
     */
    private ServerConnectionContext(final Origin origin,
            final Category category,
            final String content,
            final String clientHost) {
        super(category, content);
        this.origin = origin;
        this.clientAddress = clientHost;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ServerConnectionContext createDeprecated() {
        final StackTraceElement[] elements = new Exception().getStackTrace();
        final String context = elements[1].toString();
        return create(context);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   context  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ServerConnectionContext create(final String context) {
        return create(Category.DEPRECATED, context);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   category  DOCUMENT ME!
     * @param   context   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ServerConnectionContext create(final Category category, final String context) {
        return new ServerConnectionContext(Origin.SERVER, category, context, null);
    }
    /**
     * DOCUMENT ME!
     *
     * @param   connectionContext  DOCUMENT ME!
     * @param   localAddress       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ServerConnectionContext createFromClientContext(final ClientConnectionContext connectionContext,
            final String localAddress) {
        final ServerConnectionContext serverConnectionContext = new ServerConnectionContext(
                Origin.CLIENT,
                connectionContext.getCategory(),
                connectionContext.getContent(),
                localAddress);
        serverConnectionContext.getAdditionalFields().putAll(connectionContext.getAdditionalFields());
        return serverConnectionContext;
    }

    @Override
    public Origin getOrigin() {
        return origin;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getClientAddress() {
        return clientAddress;
    }
}
