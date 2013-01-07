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
package de.cismet.tools;

/**
 * CurrentStackTrace
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class CurrentStackTrace extends Throwable {

    //~ Methods ----------------------------------------------------------------

    @Override
    public StackTraceElement[] getStackTrace() {
        return Thread.currentThread().getStackTrace();
    }
}
