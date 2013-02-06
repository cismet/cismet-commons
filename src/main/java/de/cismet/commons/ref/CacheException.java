/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.commons.ref;

/**
 * This exception shall be used to indicate errors during operations related to caching.
 *
 * @author   mscholl
 * @version  1.0
 */
public class CacheException extends RuntimeException {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CacheException object.
     *
     * @param  message  a message indicating the reason of the error
     */
    public CacheException(final String message) {
        super(message);
    }

    /**
     * Creates a new CacheException object.
     *
     * @param  message  a message indicating the reason of the error
     * @param  cause    the cause of the exception
     */
    public CacheException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
