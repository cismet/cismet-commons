/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Equals helper operations.
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class Equals {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Equals object.
     */
    private Equals() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Compares the bean properties of given objects. This operation is equivalent to calling
     * {@link #beanDeepEqual(java.lang.Object, java.lang.Object, java.lang.String[])} with a <code>null String[]</code>.
     *
     * @param   o   object one
     * @param   o2  object two
     *
     * @return  true, if obeject one and object two deliver the same result for every bean getter
     *
     * @see     #beanDeepEqual(java.lang.Object, java.lang.Object, java.lang.String[])
     */
    public static boolean beanDeepEqual(final Object o, final Object o2) {
        return beanDeepEqual(o, o2, (String[])null);
    }

    /**
     * Tests if the given objects 'o' and 'o2' are of the same type and if all bean getters of 'o' return the same
     * values as the bean getters of 'o2'. If both objects are <code>null</code> they're also considered equal
     * {@link #nullEqual(java.lang.Object, java.lang.Object)}. This is also true for all the bean getter return values
     * that are checked. There is also the possibility to provide an ignore list that can contain names of getter
     * operations that shall be ignored when checking for equality.
     *
     * @param   o          object one
     * @param   o2         object two
     * @param   ignoreOps  all operation names, that shall be ignored during the equality check, may be <code>
     *                     null</code>
     *
     * @return  true, if 'o' and 'o2' deliver the same result for every bean getter
     *
     * @see     #nullEqual(java.lang.Object, java.lang.Object)
     * @see     #isBeanGetter(java.lang.reflect.Method)
     */
    public static boolean beanDeepEqual(final Object o, final Object o2, final String... ignoreOps) {
        if (o == o2) {
            return true;
        } else if (xor(o, o2)) {
            return false;
        } else if (o.getClass().equals(o2.getClass())) {
            final Method[] methods = o.getClass().getMethods();
            for (final Method m : methods) {
                if (isBeanGetter(m) && ((ignoreOps == null) || !contains(m.getName(), ignoreOps))) {
                    try {
                        if (!nullEqual(m.invoke(o, (Object[])null), m.invoke(o2, (Object[])null))) {
                            return false;
                        }
                    } catch (Exception ex) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Tests if the given array contains the given name.
     *
     * @param   name   searched name
     * @param   array  given array
     *
     * @return  true, if the array contains the name, false otherwise, also false if the array is <code>null</code>
     */
    private static boolean contains(final String name, final String... array) {
        if (array == null) {
            return false;
        } else {
            for (final String s : array) {
                if (s.equals(name)) {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * Tests if a <code>Method</code> is a bean getter operation. A bean getter operation has different properties:<br/>
     *
     * <ul>
     *   <li>public</li>
     *   <li>parameter count is 0</li>
     *   <li>is not native</li>
     *   <li>is not static</li>
     *   <li>name starts with 'get' (or 'is' for <code>boolean</code> getters)</li>
     *   <li>there exists a field with a corresponding name</li>
     *   <li></li>
     * </ul>
     *
     * @param   m  given method
     *
     * @return  <code>true</code> if all the requirements listed above are met, <code>false</code> otherwise
     */
    // TODO: may be not the right place for this kind of method
    public static boolean isBeanGetter(final Method m) {
        final int modifiers = m.getModifiers();
        final String name = m.getName();

        try {
            //J-
            return Modifier.isPublic(modifiers)
                    && m.getParameterTypes().length == 0
                    && !Modifier.isNative(modifiers)
                    && !Modifier.isStatic(modifiers)
                    && ((name.startsWith("get")
                            && m.getDeclaringClass().getDeclaredField(Character.toLowerCase(name.charAt(3)) + name.substring(4)) != null)
                        || (m.getReturnType().equals(Boolean.class) || m.getReturnType().equals(boolean.class))
                            && name.startsWith("is")
                            && m.getDeclaringClass().getDeclaredField(Character.toLowerCase(name.charAt(2)) + name.substring(3)) != null);
            //J+
        } catch (final NoSuchFieldException e) {
            return false;
        }
    }

    /**
     * Tests if both objects are equals whereas they are considered equal if they are both <code>null</code>, too.
     *
     * @param   o1  object one
     * @param   o2  object two
     *
     * @return  <code>true</code> if o1 == null and o2 == null or if o1.equals(o2), <code>false</code> otherwise
     */
    public static boolean nullEqual(final Object o1, final Object o2) {
        if (xor(o1, o2)) {
            return false;
        } else if (allNull(o1, o2)) {
            return true;
        } else {
            return o1.equals(o2);
        }
    }

    /**
     * Tests if one of the objects is <code>null</code> and the other is not <code>null</code>.
     *
     * @param   o1  object one
     * @param   o2  object two
     *
     * @return  <code>true</code> if object one is <code>null</code> and object two is not <code>null</code> and vice
     *          versa, <code>false</code> if both objects are <code>null</code> or both objects are not <code>
     *          null</code>.
     */
    public static boolean xor(final Object o1, final Object o2) {
        return ((o1 == null) && (o2 != null)) || ((o1 != null) && (o2 == null));
    }

    /**
     * Tests if all given objects are <code>null</code>. If no objects are provided at all, <code>true</code> is
     * returned, too.
     *
     * @param   obs  the objects
     *
     * @return  <code>true</code> if all given objects are null, <code>false</code> otherwise
     */
    public static boolean allNull(final Object... obs) {
        if (obs == null) {
            return true;
        }

        for (final Object o : obs) {
            if (o != null) {
                return false;
            }
        }

        return true;
    }

    /**
     * Test if non of the given objects is <code>null</code>. If no objects are provided at all, <code>false</code> is
     * returned, too.
     *
     * @param   obs  the objects
     *
     * @return  <code>true</code> if all given objects are non-null, <code>false</code> otherwise
     */
    public static boolean nonNull(final Object... obs) {
        if (obs == null) {
            return false;
        }

        for (final Object o : obs) {
            if (o == null) {
                return false;
            }
        }

        return true;
    }
}
