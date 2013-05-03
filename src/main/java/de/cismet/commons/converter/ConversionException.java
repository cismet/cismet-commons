/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.commons.converter;

/**
 * Exception to be thrown if a conversion related exception occurs.
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public final class ConversionException extends Exception {

    //~ Instance fields --------------------------------------------------------

    private final Converter originatingConverter;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of <code>ConversionException</code> without detail message.
     */
    public ConversionException() {
        this(null, null, null);
    }

    /**
     * Constructs an instance of <code>ConversionException</code> with the specified detail message.
     *
     * @param  msg  the detail message.
     */
    public ConversionException(final String msg) {
        this(msg, null, null);
    }

    /**
     * Constructs an instance of <code>ConversionException</code> with the specified detail message and the specified
     * cause.
     *
     * @param  msg    the detail message.
     * @param  cause  the exception cause
     */
    public ConversionException(final String msg, final Throwable cause) {
        this(msg, cause, null);
    }

    /**
     * Creates a new ConversionException object.
     *
     * @param  message               the detail message.
     * @param  cause                 the exception cause
     * @param  originatingConverter  the converter instance where this exception occurred
     */
    public ConversionException(final String message, final Throwable cause, final Converter originatingConverter) {
        super(message, cause);

        this.originatingConverter = originatingConverter;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Getter for the originating converter instance.
     *
     * @return  the originating converter instance or <code>null</code> if not specified
     */
    public Converter getOriginatingConverter() {
        return originatingConverter;
    }
}
