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
package de.cismet.netutil;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public interface ProxyPropertiesHandler {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    ProxyProperties loadProperties();

    /**
     * DOCUMENT ME!
     *
     * @param   properties  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    void saveProperties(final ProxyProperties properties) throws Exception;
}
