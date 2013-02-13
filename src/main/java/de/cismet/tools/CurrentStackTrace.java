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
 * Class for the Stack Trace of the currently executing thread.
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class CurrentStackTrace extends Throwable {

    //~ Methods ----------------------------------------------------------------

    /**
     * Returns an array of stack trace elements representing the stack dump of currently executing thread.
     *
     * @return  array of stack trace elements
     */
    @Override
    public StackTraceElement[] getStackTrace() {
        return Thread.currentThread().getStackTrace();
    }
}
