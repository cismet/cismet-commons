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
public interface ServerConnectionContextProvider {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    ServerConnectionContext getServerConnectionContext();

    /**
     * DOCUMENT ME!
     *
     * @param  serverConnectionContext  DOCUMENT ME!
     */
    void setServerConnectionContext(ServerConnectionContext serverConnectionContext);
}
