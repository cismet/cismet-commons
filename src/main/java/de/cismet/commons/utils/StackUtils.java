/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.commons.utils;

/**
 * Useful operations concerning stacktraces, mainly for debugging purposes.
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public final class StackUtils {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new StackUtils object.
     */
    private StackUtils() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Gets the name of the method that calls this method. Equal to calling
     * {@link #getMethodName(boolean, boolean, java.lang.Object[])} with <code>false</code> as value for the <code>
     * canonicalName</code> and <code>pringValues</code> parameter and <code>null</code> as value for the <code>
     * paramInstances</code> parameter.
     *
     * @return  the name of the method that calls this method
     *
     * @see     #getMethodName(boolean, boolean, java.lang.Object[])
     */
    public static String getMethodName() {
        return doGetMethodName(false, false, (Object[])null);
    }

    /**
     * Gets the name of the method that calls this method. Equal to calling
     * {@link #getMethodName(boolean, boolean, java.lang.Object[])} with <code>false</code> as value for the <code>
     * canonicalName</code> and <code>pringValues</code> parameter.
     *
     * @param   paramInstances  the parameters that shall be printed with this method name
     *
     * @return  the name of the method that calls this method
     *
     * @see     #getMethodName(boolean, boolean, java.lang.Object[])
     */
    public static String getMethodName(final Object... paramInstances) {
        return doGetMethodName(false, false, paramInstances);
    }

    /**
     * Gets the name of the method that calls this method. Equal to calling
     * {@link #getMethodName(boolean, boolean, java.lang.Object[])} with <code>false</code> as value for the <code>
     * pringValues</code> parameter.
     *
     * @param   canonicalName   whether the class names of the <code>paramInstances</code> shall be canonical or simple
     * @param   paramInstances  the parameters that shall be printed with this method name
     *
     * @return  the name of the method that calls this method
     *
     * @see     #getMethodName(boolean, boolean, java.lang.Object[])
     */
    public static String getMethodName(final boolean canonicalName, final Object... paramInstances) {
        return doGetMethodName(canonicalName, false, paramInstances);
    }

    /**
     * Gets the name of the method that calls this method. If optionally provided with a list of parameters it prints
     * their type ({@link Class}), too, depending on the <code>canonicalName</code> flag, e.g.<br/>
     * <br/>
     * <code>myShinyMethod(String,boolean)</code> if <code>canonicalName</code> is <code>false</code>.<br/>
     * <br/>
     * If the <code>printValues</code> is <code>true</code> it additionally appends the results of the <code>
     * toString()</code> operations of the single methods like so:<br/>
     * <br/>
     * <code>myShinyMethod(String{myString})</code>.<br/>
     * <br/>
     * <b>NOTE:</b>This should mainly be used for debugging purposes as it is a rather slow implementation.
     *
     * @param   canonicalName   whether the class names of the <code>paramInstances</code> shall be canonical or simple
     * @param   printValues     whether the actual values of the <code>paramInstances</code> shall be printed
     * @param   paramInstances  the parameters that shall be printed with this method name
     *
     * @return  the name of the method that calls this method
     */
    public static String getMethodName(final boolean canonicalName,
            final boolean printValues,
            final Object... paramInstances) {
        return doGetMethodName(canonicalName, printValues, paramInstances);
    }

    /**
     * Actually does the build of the method name according to description at
     * {@link #getMethodName(boolean, boolean, java.lang.Object[]) }.
     *
     * @param   canonicalName   DOCUMENT ME!
     * @param   printValues     DOCUMENT ME!
     * @param   paramInstances  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String doGetMethodName(final boolean canonicalName,
            final boolean printValues,
            final Object... paramInstances) {
        final StringBuilder sb = new StringBuilder(Thread.currentThread().getStackTrace()[3].getMethodName());

        sb.append('(');
        if (paramInstances != null) {
            for (final Object o : paramInstances) {
                final String cName = o.getClass().getCanonicalName();
                if (canonicalName) {
                    sb.append(cName);
                } else {
                    sb.append(cName.substring(cName.lastIndexOf('.') + 1));
                }

                if (printValues) {
                    sb.append('{');
                    sb.append(o.toString());
                    sb.append('}');
                }

                sb.append(',');
            }
        }

        if (',' == sb.charAt(sb.length() - 1)) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(')');

        return sb.toString();
    }

    /**
     * Creates a <code>Throwable</code> whose first (topmost) element is the line of the calling method. As the name
     * suggests this operation should only be used for debugging purposes.
     *
     * @return  a <code>Throwable</code> whose first (topmost) element is the line of the calling method
     */
    public static Throwable getDebuggingThrowable() {
        final Throwable t = new Throwable("DebuggingThrowable", null); // NOI18N
        final StackTraceElement[] ste = t.getStackTrace();
        final StackTraceElement[] ret = new StackTraceElement[ste.length - 1];

        System.arraycopy(ste, 1, ret, 0, ret.length);

        t.setStackTrace(ret);

        return t;
    }

    /**
     * Tests the equality of two stacktraces. Internally makes use of
     * {@link #equals(java.lang.StackTraceElement, java.lang.StackTraceElement, boolean) }
     *
     * @param   ste1         first stacktrace
     * @param   ste2         second stacktrace
     * @param   lineNumbers  if line numbers shall be included in the equality check
     *
     * @return  <code>true</code> if the stacktraces are equal, <code>false</code> otherwise
     *
     * @throws  IllegalArgumentException  if at least one argument is <code>null</code>
     */
    public static boolean equals(
            final StackTraceElement[] ste1,
            final StackTraceElement[] ste2,
            final boolean lineNumbers) {
        if ((ste1 == null) || (ste2 == null)) {
            throw new IllegalArgumentException(
                "equality check only allowed for non-null objects: ste1=" // NOI18N
                        + ste1
                        + "||"                                            // NOI18N
                        + "ste2="                                         // NOI18N
                        + ste2);
        }
        if (ste1.length != ste2.length) {
            return false;
        }
        // though stacktrace "begins" at the last element of the array, this
        // approuch will be way faster than beginning at the "root" element
        for (int i = 0; i < ste1.length; ++i) {
            if (!equals(ste1[i], ste2[i], lineNumbers)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Tests the equality of two <code>StackTraceElement</code>s. Two elements are considered equal if their method,
     * classname and filename are equal. Optionally the equality check is done also for their line numbers.
     *
     * @param   ste1         first <code>StackTraceElement</code>
     * @param   ste2         seconed <code>StackTraceElement</code>
     * @param   lineNumbers  if <code>true</code> line numbers are included in the equality check
     *
     * @return  <code>true</code> if the <code>StackTraceElement</code>s are equal, <code>false</code> otherwise
     *
     * @throws  IllegalArgumentException  if any of the arguments is <code>null</code>
     */
    public static boolean equals(
            final StackTraceElement ste1,
            final StackTraceElement ste2,
            final boolean lineNumbers) {
        if ((ste1 == null) || (ste2 == null)) {
            throw new IllegalArgumentException(
                "equality check only allowed for non-null objects: ste1=" // NOI18N
                        + ste1
                        + "||"                                            // NOI18N
                        + "ste2="                                         // NOI18N
                        + ste2);
        }
        if (lineNumbers) {
            return ste1.equals(ste2);
        }
        if (ste1.getMethodName().equals(ste2.getMethodName())
                    && ste1.getClassName().equals(ste2.getClassName())) {
            final String fn1 = ste1.getFileName();
            final String fn2 = ste2.getFileName();
            if (((fn1 == null) && (fn2 != null)) || ((fn1 != null) && (fn2 == null))) {
                return false;
            }
            if (((fn1 == null) && (fn2 == null)) || fn1.equals(fn2)) {
                return true;
            }
        }
        return false;
    }
}
