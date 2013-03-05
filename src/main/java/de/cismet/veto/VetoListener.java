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
 * Veto Listener interface.
 *
 * @author   spuhl
 * @version  $Revision$, $Date$
 */
public interface VetoListener {

    //~ Methods ----------------------------------------------------------------

    /**
     * throws Veto Exception.
     *
     * @throws  VetoException  Veto Exception.
     */
    void veto() throws VetoException;
}
