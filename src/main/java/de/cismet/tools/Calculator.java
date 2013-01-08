/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools;

/**
 * Calculator.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public interface Calculator<INPUT extends Object, OUTPUT extends Object> {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   input  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    OUTPUT calculate(INPUT input) throws Exception;
}
