/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.tools;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author srichter
 */
public final class BlacklistClassloading {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(BlacklistClassloading.class);
    private static final Set<String> blacklist = new HashSet<String>();

    private BlacklistClassloading() {
        throw new AssertionError();
    }

    /**
     *
     * @param canonical classname to load
     * @return the class to load or null if class is not found.
     */
    public static final Class<?> forName(final String classname) {
        if (classname != null) {
            final StringBuilder classNameWithLoaderBuiler = new StringBuilder(classname);
            classNameWithLoaderBuiler.append(Thread.currentThread().getContextClassLoader());
            if (!blacklist.contains(classNameWithLoaderBuiler.toString())) {
                try {
                    return Class.forName(classname);
                } catch (ClassNotFoundException ex) {
                    blacklist.add(classNameWithLoaderBuiler.toString());
                    if (log.isDebugEnabled()) {
                        log.debug("Could not load class " + classNameWithLoaderBuiler + "! Added classname to blacklist", ex);
                    }
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Did not load Class " + classname + " as it is on the blacklist!");
                }
            }
        } else {
            log.debug("Classname to load was null!");
        }
        return null;
    }
}
