/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.veto;

/**
 * Veto Exception Class.
 *
 * @author   spuhl
 * @version  $Revision$, $Date$
 */
public class VetoException extends Exception {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VetoException object.
     */
    public VetoException() {
    }

    /**
     * Creates a new VetoException object.
     *
     * @param  message  the detail message
     */
    public VetoException(final String message) {
        super(message);
    }
}
