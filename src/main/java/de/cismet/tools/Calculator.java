/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools;

/**
 * Interface Calculator.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public interface Calculator<INPUT extends Object, OUTPUT extends Object> {

    //~ Methods ----------------------------------------------------------------

    /**
     * Interface Calculator Calculates with the given <code>INPUT</code> and returns an <code>OUTPUT</code>.
     *
     * @param   input  given <code>INPUT</code>
     *
     * @return  calculated <code>OUTPUT</code>
     *
     * @throws  Exception  throws Exeption if anything went wrong
     */
    OUTPUT calculate(INPUT input) throws Exception;
}
