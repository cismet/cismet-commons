/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.commons.classloading;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class provides information about the packages, their resources and their origins that are currently on the
 * classpath (see {@link System#getProperty(java.lang.String)} with <code>java.class.path</code> property key).<br/>
 * <br/>
 * <b>NOTE:</b>The implementation is currently neither thread safe nor does it make use of sophisticated caching
 * mechanism.<br/>
 * <br/>
 * <b>IMPORTANT: This implementation may not work as expected since java may load classes from other resources than from
 * files (e.g. Java WebStart).</b>
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public final class ClassPathInfo {

    //~ Static fields/initializers ---------------------------------------------

    public static final String META_INF = "META-INF"; // NOI18N

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(ClassPathInfo.class);

    //~ Instance fields --------------------------------------------------------

    private final transient Map<String, List<File>> packageToOrigin;
    private final transient Map<String, List<String>> packageToResource;
    private final transient Map<File, List<String>> originToResource;

    private transient boolean initialised;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ClassPathInfo object.
     */
    public ClassPathInfo() {
        packageToOrigin = new HashMap<String, List<File>>();
        packageToResource = new HashMap<String, List<String>>();
        originToResource = new HashMap<File, List<String>>();

        initialised = false;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isInitialised() {
        return initialised;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  forceReinit  DOCUMENT ME!
     */
    public void scan(final boolean forceReinit) {
        if (!initialised || forceReinit) {
            packageToOrigin.clear();
            packageToResource.clear();
            originToResource.clear();

            init();
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void init() {
        final String cp = System.getProperty("java.class.path"); // NOI18N
        final String[] cpEntries = cp.split(File.pathSeparator);

        for (final String cpEntry : cpEntries) {
            final File f = new File(cpEntry);
            if (f.isDirectory()) {
                handleCPDir(f, f);
            } else if (f.isFile() && f.getName().endsWith(".jar")) {           // NOI18N
                handleCPJar(f);
            } else {
                LOG.warn("skipping unrecognised classpath entry: " + cpEntry); // NOI18N
            }
        }

        initialised = true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   baseDir     DOCUMENT ME!
     * @param   currentDir  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    private void handleCPDir(final File baseDir, final File currentDir) {
        if ((baseDir == null) || !baseDir.isDirectory()) {
            throw new IllegalArgumentException("basedir must not be null and must be a directory"); // NOI18N
        }

        if ((currentDir == null) || !currentDir.isDirectory()) {
            throw new IllegalArgumentException("currentdir must not be null and must be a directory"); // NOI18N
        }

        boolean ancestor = false;
        File cur = currentDir;
        while (cur != null) {
            if (cur.equals(baseDir)) {
                ancestor = true;
                break;
            } else {
                cur = cur.getParentFile();
            }
        }

        if (!ancestor) {
            throw new IllegalArgumentException("basedir is not ancestor of currentDir: [baseDir=" + baseDir // NOI18N
                        + "|currentDir=" + currentDir + "]"); // NOI18N
        }

        final File[] files = currentDir.listFiles();
        for (final File file : files) {
            if (file.isDirectory()) {
                // skip meta inf dir
                if (!META_INF.equals(file.getName())) {
                    handleCPDir(baseDir, file);
                }
            } else {
                final String bdPath = baseDir.getAbsolutePath();
                final String pakkage = fileToJavaPath(currentDir.getAbsolutePath().replace(bdPath, "")); // NOI18N
                final String resource = fileToJavaPath(file.getAbsolutePath().replace(bdPath, ""));      // NOI18N

                if (LOG.isDebugEnabled()) {
                    LOG.debug("found package '" + pakkage + "' for resource: " + file); // NOI18N
                }

                addOrigin(pakkage, baseDir);
                addResource(pakkage, resource);
                addResourceToOrigin(baseDir, resource);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   s  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String fileToJavaPath(final String s) {
        if ((s == null) || s.isEmpty()) {
            return s;
        }

        final String res = (s.charAt(0) == File.separatorChar) ? s.substring(1) : s;

        return res.replace(File.separator, "."); // NOI18N
    }

    /**
     * DOCUMENT ME!
     *
     * @param  origin    DOCUMENT ME!
     * @param  resource  DOCUMENT ME!
     */
    private void addResourceToOrigin(final File origin, final String resource) {
        synchronized (originToResource) {
            List<String> resources = originToResource.get(origin);
            if (resources == null) {
                resources = new ArrayList<String>();
                originToResource.put(origin, resources);
            }

            resources.add(resource);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  pakkage  DOCUMENT ME!
     * @param  origin   DOCUMENT ME!
     */
    private void addOrigin(final String pakkage, final File origin) {
        synchronized (packageToOrigin) {
            List<File> origins = packageToOrigin.get(pakkage);
            if (origins == null) {
                // we assume that everything is ok an there is only one origin for a every package, thus init with 1
                origins = new ArrayList<File>(1);
                packageToOrigin.put(pakkage, origins);
            }

            if (!origins.contains(origin)) {
                origins.add(origin);

                if (origins.size() > 1) {
                    LOG.warn("multiple origins for package: [package=" + pakkage + "|origins="
                                + origins.toString() // NOI18N
                                + "]");              // NOI18N
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  pakkage   DOCUMENT ME!
     * @param  resource  DOCUMENT ME!
     */
    private void addResource(final String pakkage, final String resource) {
        synchronized (packageToResource) {
            List<String> resources = packageToResource.get(pakkage);
            if (resources == null) {
                resources = new ArrayList<String>();
                packageToResource.put(pakkage, resources);
            }

            if (resources.contains(resource)) {
                LOG.warn("identical resources in package: [package=" + pakkage + "|resource=" + resource + "]"); // NOI18N
            }

            resources.add(resource);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   jar  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    private void handleCPJar(final File jar) {
        if ((jar == null) || !jar.isFile()) {
            throw new IllegalArgumentException("jar must not be null and must be of type file"); // NOI18N
        }
        try {
            final JarFile jarFile = new JarFile(jar, true, JarFile.OPEN_READ);

            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();

                if (!entry.isDirectory()) {
                    final String entryName = entry.getName();

                    // skip meta-info
                    if (!entryName.startsWith(META_INF)) {
                        final int indexof = entryName.lastIndexOf('/');
                        final String pakkage = (indexof > 0) ? entryName.substring(0, indexof).replace("/", ".") : ""; // NOI18N
                        final String resource = entryName.replace("/", ".");                                           // NOI18N

                        addOrigin(pakkage, jar);
                        addResource(pakkage, resource);
                        addResourceToOrigin(jar, resource);
                    }
                }
            }
        } catch (final IOException ex) {
            LOG.error("cannot handle jar file: " + jar, ex); // NOI18N
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<String> getAllPackages() {
        return packageToOrigin.keySet();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   pakkage  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<String> getResources(final String pakkage) {
        return packageToResource.get(pakkage);
    }
}
