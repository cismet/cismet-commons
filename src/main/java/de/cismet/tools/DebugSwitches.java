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
 * debug Switches.
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class DebugSwitches {

    //~ Static fields/initializers ---------------------------------------------

    private static DebugSwitches instance = null;

    //~ Instance fields --------------------------------------------------------

    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DebugSwitches object.
     */
    private DebugSwitches() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Getter for Instance.
     *
     * @return  {@link #instance}
     */
    public static DebugSwitches getInstance() {
        if (instance == null) {
            instance = new DebugSwitches();
        }
        return instance;
    }
}
