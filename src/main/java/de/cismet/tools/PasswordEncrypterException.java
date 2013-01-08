/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools;

/**
 * Password Encrypter Exception.
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class PasswordEncrypterException extends RuntimeException {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of <code>PasswordEncrypterException</code> without detail message.
     */
    public PasswordEncrypterException() {
    }

    /**
     * Constructs an instance of <code>PasswordEncrypterException</code> with the specified detail message.
     *
     * @param  msg  the detail message.
     */
    public PasswordEncrypterException(final String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>PasswordEncrypterException</code> with the specified detail message and the
     * specified cause.
     *
     * @param  msg    the detail message.
     * @param  cause  the exception cause
     */
    public PasswordEncrypterException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
