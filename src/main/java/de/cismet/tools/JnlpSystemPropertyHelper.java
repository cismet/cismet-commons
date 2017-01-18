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
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class JnlpSystemPropertyHelper {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   propertyName  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getProperty(final String propertyName) {
        return getProperty(propertyName, null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   propertyName  DOCUMENT ME!
     * @param   defaultValue  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getProperty(String propertyName, final String defaultValue) {
        if (propertyName == null) {
            return null;
        }

        final String normalPropertyValue = System.getProperty(propertyName);

        if (normalPropertyValue == null) {
            propertyName = "jnlp." + propertyName;
        }

        return System.getProperty(propertyName, defaultValue);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        System.setProperty("testProp", "ohne jnlp");
        System.setProperty("jnlp.testProp", "mit jnlp");
        System.out.println(getProperty("testProp"));
    }
}
