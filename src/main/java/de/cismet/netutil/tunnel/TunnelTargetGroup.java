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
package de.cismet.netutil.tunnel;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class TunnelTargetGroup {

    //~ Instance fields --------------------------------------------------------

    String targetGroupkey;
    String[] targetExpressions;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TunnelTargetGroup object.
     */
    public TunnelTargetGroup() {
    }

    /**
     * Creates a new TunnelTargetGroup object.
     *
     * @param  targetGroupkey     DOCUMENT ME!
     * @param  targetExpressions  DOCUMENT ME!
     */
    public TunnelTargetGroup(final String targetGroupkey, final String[] targetExpressions) {
        this.targetGroupkey = targetGroupkey;
        this.targetExpressions = targetExpressions;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String[] getTargetExpressions() {
        return targetExpressions;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  targetExpressions  DOCUMENT ME!
     */
    public void setTargetExpressions(final String[] targetExpressions) {
        this.targetExpressions = targetExpressions;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getTargetGroupkey() {
        return targetGroupkey;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  targetGroupkey  DOCUMENT ME!
     */
    public void setTargetGroupkey(final String targetGroupkey) {
        this.targetGroupkey = targetGroupkey;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   candidate  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean matches(final String candidate) {
        for (final String regex : targetExpressions) {
            if (candidate.matches(regex)) {
                return true;
            }
        }
        return false;
    }
}
