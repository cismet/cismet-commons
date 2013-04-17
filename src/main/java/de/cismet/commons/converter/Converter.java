/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.commons.converter;

/**
 * Converter interface which shall be used by classes that intend to implement bidirectional conversion of one type in
 * another and vice versa.
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public interface Converter<FROM, TO> {

    //~ Methods ----------------------------------------------------------------

    /**
     * Converts a given instance <code>FROM</code> to the target format <code>TO</code> using the (optional) parameters.
     *
     * @param   from    the originating object that shall be converted to an instance of <code>TO</code>
     * @param   params  optional parameters that may be passed to the converter that may influence conversion
     *
     * @return  an instance of <code>TO</code> which was create from the <code>FROM</code> instance
     *
     * @throws  ConversionException  if any error occurs during conversion
     */
    TO convertForward(FROM from, final String... params) throws ConversionException;

    /**
     * Converts a given instance <code>TO</code> back to the origin format <code>FROM</code> using the (optional)
     * parameters.
     *
     * @param   to      the converted object that shall be converted back to an instance of <code>FROM</code>
     * @param   params  optional parameters that may be passed to the converter that may influence conversion
     *
     * @return  an instance of <code>FROM</code> which was create from the <code>TO</code> instance
     *
     * @throws  ConversionException  if any error occurs during conversion
     */
    FROM convertBackward(TO to, final String... params) throws ConversionException;
}
