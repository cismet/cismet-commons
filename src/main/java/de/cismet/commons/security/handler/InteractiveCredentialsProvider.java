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

import org.apache.hc.client5.http.auth.CredentialsProvider;

import de.cismet.commons.security.exceptions.CredentialsNotAvailableException;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public interface InteractiveCredentialsProvider {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   authenticationHeader  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  CredentialsNotAvailableException  DOCUMENT ME!
     */
    CredentialsProvider askForCredentials(final String authenticationHeader) throws CredentialsNotAvailableException;
}
