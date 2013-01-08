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

import org.codehaus.jackson.map.ObjectMapper;

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
     * @param  targetGroupkey     target Group Key
     * @param  targetExpressions  target Expressions
     */
    public TunnelTargetGroup(final String targetGroupkey, final String[] targetExpressions) {
        this.targetGroupkey = targetGroupkey;
        this.targetExpressions = targetExpressions;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Getter for <code>targetExpressions.</code>
     *
     * @return  <code>targetExpressions</code>
     */
    public String[] getTargetExpressions() {
        return targetExpressions;
    }

    /**
     * Setter for <code>targetExpressions.</code>
     *
     * @param  targetExpressions  <code>targetExpressions</code>
     */
    public void setTargetExpressions(final String[] targetExpressions) {
        this.targetExpressions = targetExpressions;
    }

    /**
     * Getter for <code>targetGroupkey.</code>
     *
     * @return  <code>targetGroupkey</code>
     */
    public String getTargetGroupkey() {
        return targetGroupkey;
    }

    /**
     * Setter for <code>targetGroupkey.</code>
     *
     * @param  targetGroupkey  <code>targetGroupkey</code>
     */
    public void setTargetGroupkey(final String targetGroupkey) {
        this.targetGroupkey = targetGroupkey;
    }

    /**
     * Tests whether the <code>String</code> matches or not.
     *
     * @param   candidate  given <code>String</code>
     *
     * @return  <code>true</code> if it matches
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
