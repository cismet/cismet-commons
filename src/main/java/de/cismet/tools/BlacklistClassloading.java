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
    public static Class<?> forName(final String classname) {
        if (classname != null) {
            final StringBuilder classNameWithLoaderBuilder = new StringBuilder(classname);
            classNameWithLoaderBuilder.append("@").append(Thread.currentThread().getContextClassLoader());
            String classIdentity = classNameWithLoaderBuilder.toString();
            if (!blacklist.contains(classIdentity)) {
                try {
                    return Class.forName(classname);
                } catch (ClassNotFoundException ex) {
                    blacklist.add(classIdentity);
                    if (log.isDebugEnabled()) {
                        log.debug("Could not load class " + classIdentity + "! Added classname to blacklist", ex);
                    }
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Did not load Class " + classname + " as it is on the blacklist!");  // NOI18N
                }
            }
        } else {
            log.debug("Classname to load was null!");  // NOI18N
        }
        return null;
    }
}
