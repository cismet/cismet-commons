/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.tools;

import java.util.Set;
import org.openide.util.WeakSet;

/**
 *
 * @author srichter
 */
public final class BlacklistClassloading {

    private static final Set<String> blacklist = new WeakSet<String>();

    private BlacklistClassloading() {
        throw new AssertionError();
    }

    /**
     *
     * @param canonical classname to load
     * @return the class to load or null if class is not found.
     */
    public static final Class<?> forName(String classname) {
        final StringBuilder classNameWithLoaderBuiler = new StringBuilder(classname);
        classNameWithLoaderBuiler.append(Thread.currentThread().getContextClassLoader());
        if (!blacklist.contains(classNameWithLoaderBuiler.toString())) {
            try {
                return Class.forName(classname);
            } catch (ClassNotFoundException ex) {
                blacklist.add(classNameWithLoaderBuiler.toString());
            }
        }
        return null;
    }
}
