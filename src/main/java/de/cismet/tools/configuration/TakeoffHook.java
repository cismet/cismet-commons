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
package de.cismet.tools.configuration;

/**
 * TakeoffHook.
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public interface TakeoffHook {

    //~ Methods ----------------------------------------------------------------

    /**
     * This operation is one of the first operations tha application will call.
     */
    void applicationTakeoff();
}
