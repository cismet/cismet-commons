/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * BlacklistClassloading optimises the 
 * <code>Class.forName(String)</code> operation by caching already found class objects and blacklisting classes that 
 * have been requested to be loaded but could not be found. <b>IMPORTANT:</b> Do not use if your environment is dynamic
 * in a way that new classes are defined at runtime!
 *
 * @author   srichter
 * @author   mscholl
 * @version  1.1
 */
public final class BlacklistClassloading {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(BlacklistClassloading.class);
    // NOTE: optimisation could be to use the String.intern().hashcode() as key if space is needed
    private static final transient Map<String, Class<?>> BLACKLIST_CACHE = new HashMap<String, Class<?>>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Utility class. Constructor shall never be used.
     *
     * @throws  AssertionError  if used
     */
    private BlacklistClassloading() {
        throw new AssertionError();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Loads the class with the given class name using. If the class has not yet been loaded and loading has never been
     * tried before calling this operation is similar to a call to {@link Class#forName(java.lang.String)}. If loading
     * has already been tried but the class was not found 
     * <code>null</code> is returned directly. If loading has already been tried and the class was successfully loaded
     * before the 
     * <code>Class</code> object is returned directly.
     *
     * @param   classname  canonical name of the class that shall be loaded
     *
     * @return  the class instance to be loaded or null if the class does not exist
     */
    public static Class<?> forName(final String classname) {
        // we don't need sync here as the result of two calls to Class.forName() from the same classloader is identical 
        // (classA == classB)
        Class<?> clazz = null;
        
        if (classname == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Classname to load was null!");                                         // NOI18N
            }
        } else {
            final StringBuilder classNameWithLoaderBuilder = new StringBuilder(classname);
            classNameWithLoaderBuilder.append('@').append(Thread.currentThread().getContextClassLoader()); 
            final String classIdentity = classNameWithLoaderBuilder.toString();
            
            if (BLACKLIST_CACHE.containsKey(classIdentity)) {
                clazz = BLACKLIST_CACHE.get(classIdentity);
                if (LOG.isDebugEnabled()) {
                    if(clazz == null) {
                        LOG.debug("did not load class as it is on the blacklist: " + classname); // NOI18N
                    } else {
                        LOG.debug("class retrieved from cache: " + clazz); // NOI18N
                    }
                }
            } else {
                try {
                    clazz = Class.forName(classname);
                } catch (final ClassNotFoundException ex) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("could not load class, added classname to blacklist: " + classIdentity, ex); // NOI18N
                    }
                }
                
                BLACKLIST_CACHE.put(classIdentity, clazz);
            }
        }

        return clazz;
    }
}
