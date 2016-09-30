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
package de.cismet.commons.security.handler;

import java.io.InputStream;

import java.net.URL;

import de.cismet.commons.security.AccessHandler;

/**
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public interface ExtendedAccessHandler extends AccessHandler {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   url  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    InputStream doRequest(final URL url) throws Exception;

    /**
     * DOCUMENT ME!
     *
     * @param   url  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean checkIfURLaccessible(final URL url);
}
