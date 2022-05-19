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
package de.cismet.tools;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class NotSerializableExceptionException extends Exception {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NotSerializableExceptionException object.
     */
    public NotSerializableExceptionException() {
        super();
    }

    /**
     * Creates a new NotSerializableExceptionException object.
     *
     * @param  message    DOCUMENT ME!
     * @param  throwable  DOCUMENT ME!
     */
    public NotSerializableExceptionException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
