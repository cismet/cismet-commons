/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * FileUtils Class
 *
 * @author   mscholl
 * @version  1.3
 */
public final class FileUtils {

    //~ Static fields/initializers ---------------------------------------------

    public static final int MAC_META = 1;

    public static final int UNIX_META = 2;

    public static final int WINDOWS_META = 3;

    public static final int ALL_META = 4;

    private static final String[] MAC_META_ENTRIES = { ".DS_Store" }; // NOI18N
    private static final String[] UNIX_META_ENTRIES = {};
    private static final String[] WINDOWS_META_ENTRIES = {};

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of FileUtils.
     */
    private FileUtils() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Tests whether the specified <code>File</code> is a <code>MetaFiles</code> or not
     *
     * @param   check  <code>File</code> to be tested
     *
     * @return  true if the tested <code>File</code> is a <code>MetaFile</code>
     * 
     * @see #isMetaFile(java.io.File, int)
     */
    public static boolean isMetaFile(final File check) {
        return isMetaFile(check, getMode());
    }

    /**
     * Tests whether the specified <code>File</code> is a <code>MetaFile</code> of the tested <code>OS</code> or not
     *
     * @param   check  <code>File</code> to be tested
     * @param   mode   Number of <code>OS</code> to be tested
     *
     * @return  true if the tested <code>File</code> is a <code>MetaFile</code> of the tested <code>OS</code>
     */
    public static boolean isMetaFile(final File check, final int mode) {
        return checkMeta(check.getName(), getMetaEntries(mode));
    }

    /**
     * Getter for <code>MetaEntries</code> of the specified <code>Meta</code>
     *
     * @param   mode  <code>Metanumber</code>
     *
     * @return  the <code>MetaEntries</code>;By default returns <code>All_Meta_Entries</code>
     */
    private static String[] getMetaEntries(final int mode) {
        switch (mode) {
            case MAC_META: {
                return MAC_META_ENTRIES;
            }
            case UNIX_META: {
                return UNIX_META_ENTRIES;
            }
            case WINDOWS_META: {
                return WINDOWS_META_ENTRIES;
            }
            case ALL_META:
            // ALL_META is default case
            default: {
                final String[] allMetaEntries =
                    new String[MAC_META_ENTRIES.length
                                + UNIX_META_ENTRIES.length
                                + WINDOWS_META_ENTRIES.length];
                int i = -1;
                for (final String s : MAC_META_ENTRIES) {
                    allMetaEntries[++i] = s;
                }
                for (final String s : UNIX_META_ENTRIES) {
                    allMetaEntries[++i] = s;
                }
                for (final String s : WINDOWS_META_ENTRIES) {
                    allMetaEntries[++i] = s;
                }
                return allMetaEntries;
            }
        }
    }

    /**
     * Tests whether the specified <code>File</code> has the specified <code>Metaentries</code> or not
     *
     * @param   filename  name of the tested <code>File</code>
     * @param   meta      <code>Metaentries</code> to be tested
     *
     * @return  true if the <code>File</code> has the tested <code>Metaentries</code>;false if it's not
     */
    private static boolean checkMeta(final String filename, final String[] meta) {
        for (final String s : meta) {
            if (filename.equals(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests which OS the System is running.
     *
     * @return  <code>Meta</code>
     */
    private static int getMode() {
        final String os = System.getProperty("os.name"); // NOI18N
        if (os.startsWith("Mac"))                        // NOI18N
        {
            return MAC_META;
        } else if (os.startsWith("Win"))                 // NOI18N
        {
            return WINDOWS_META;
        } else {
            return UNIX_META;
        }
    }

    /**
     * Getter for the name of the specified <code>File</code>
     *
     * @param   file given File
     *
     * @return  <code>FileName</code>
     */
    public static String getName(final File file) {
        final String nameExt = file.getName();
        final int index = nameExt.lastIndexOf('.'); // NOI18N
        if (index == -1) {
            return nameExt;
        } else {
            return nameExt.substring(0, index);
        }
    }

    /**
     * Getter for the Filestype of the specified <code>File</code>
     *
     * @param   file  given file
     *
     * @return  Filetype as <code>String</code>
     */
    public static String getExt(final File file) {
        final String nameExt = file.getName();
        final int index = nameExt.lastIndexOf('.'); // NOI18N
        if (index == -1) {
            return "";                              // NOI18N
        } else {
            return nameExt.substring(index + 1, nameExt.length());
        }
    }

    /**
     * Tests whether the specified <code>File</code> only contains <code>MetaFiles</code> of one type or not
     *
     * @param   check  <code>File</code> to be tested
     *
     * @return  true, if it only contains <code>MetaFiles</code>
     * 
     * @see {@link #containsOnlyMetaFiles(java.io.File, int)}
     */
    public static boolean containsOnlyMetaFiles(final File check) {
        return containsOnlyMetaFiles(check, getMode());
    }

    /**
     * Tests whether the specified <code>Directory</code> only contains <code>MetaFiles</code> of the specified <code>OS</code>
     * 
     * @param   check  <code>Directory/code> to be tested
     * @param   mode   <code>Os</code>
     *
     * @return  true, if it only contains <code>MetaFiles</code> of the tested <code>Os</code>
     *
     * @throws  IllegalArgumentException  throws <code>IllegalArgumentException</code> if the <code>File</code> isn't a <code>Directory</code>
     * 
     * @see #isMetaFile(java.io.File, int) 
     */
    public static boolean containsOnlyMetaFiles(final File check, final int mode) {
        if (!check.isDirectory()) {
            throw new IllegalArgumentException("only directories can contain files"); // NOI18N
        }
        final String[] metaEntries = getMetaEntries(mode);
        final FilesFilter filter = new FilesFilter();
        for (final File f : check.listFiles(filter)) {
            if (!checkMeta(f.getName(), metaEntries)) {
                return false;
            }
        }
        return true;
    }
    /**
     * Copies the specified <code>File</code>
     * Note: exception thrown and not handled to avoid logger call if it is necessary to use logger in this class return
     * boolean and handle exceptions.
     *
     * @param   inFile   input <code>File</code>
     * @param   outFile  target <code>File</code>
     *
     * @throws  FileNotFoundException  <code>FileNotFoundException</code>
     * @throws  IOException            <code>IOException</code>
     * 
     */
    public static void copyFile(final File inFile, final File outFile) throws FileNotFoundException, IOException {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(inFile);
            fos = new FileOutputStream(outFile);
            final byte[] buffer = new byte[1024];
            int i;
            while ((i = fis.read()) != -1) {
                fos.write(buffer, 0, i);
            }
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    /**
     * Copies the Content of the specified <code>Directory</code>. It uses {@link FilesFilter}. If it is a <code>File</code>, it copies the File and if it is a <code>Directory</code>, the methode start recursive again with the found <code>Directory</code
     *
     * @param   srcDir     Source Directory
     * @param   destDir    Target Directory
     * @param   filter     Filter
     * @param   recursive  recursive
     *
     * @throws  FileNotFoundException  throws FileNotFoundException if:
     * 
     * <ul>
     *  <li>the Source <code>File</code> is not a <code>Directory</code></li>
     *  <li>the Target <code>File</code> is not a <code>Directory</code></li>
     * </ul>
     * 
     * @throws  IOException            <code>IOException</code> 
     * 
     */
    public static void copyContent(
            final File srcDir,
            final File destDir,
            final FileFilter filter,
            final boolean recursive) throws FileNotFoundException, IOException {
        if (!srcDir.isDirectory()) {
            throw new FileNotFoundException(
                "you can only copy the content of a directory, for copying files use copy(File, File) instead"); // NOI18N
        }
        if (destDir.exists()) {
            if (!destDir.isDirectory()) {
                throw new FileNotFoundException("you can only copy to a directory");                             // NOI18N
            }
        } else {
            destDir.mkdirs();
        }
        for (final File f : srcDir.listFiles(filter)) {
            if (f.isFile()) {
                copyFile(f, new File(destDir, f.getName()));
            }
            if (f.isDirectory()) {
                final File dir = new File(destDir, f.getName());
                dir.mkdir();
                if (recursive) {
                    copyContent(f, dir, filter, recursive);
                }
            }
        }
    }

    /**
     * Deletes the Content of the specified <code>Directory</code>.
     *
     * @param   srcDir     <code>Directory</code>
     * @param   recursive  recursive
     *
     * @throws  IOException  <code>IOException</code>
     * <ul>
     * <li>source dir is not a directory</li>
     * <li>a File couldn't be deleted</li>
     * <li>a Folder couldn't be deleted</li>
     * </ul>
     */
    public static void deleteContent(final File srcDir, final boolean recursive) throws IOException {
        if (!srcDir.isDirectory()) {
            throw new IOException("source dir is not a directory"); // NOI18N
        }
        for (final File f : srcDir.listFiles()) {
            if (f.isFile()) {
                if (!f.delete()) {
                    throw new IOException(
                        "could not delete file: "
                                + f.getAbsolutePath()
                                + File.pathSeparator
                                + f.getName());                     // NOI18N
                }
            } else if (f.isDirectory() && recursive) {
                deleteContent(f, recursive);
                if (!f.delete()) {
                    throw new IOException(
                        "could not delete folder: "
                                + f.getAbsolutePath()
                                + File.pathSeparator
                                + f.getName());                     // NOI18N
                }
            }
        }
    }

    /**
     * Deletes the Given Folder
     *
     * @param   srcDir  given <code>Directory</code>
     *
     * @throws  IOException  <code>IOException</code>
     * <ul>
     * <li>Source Directory is not a Directory</li>
     * <li>Could not delete File</li>
     * </ul>
     */
    public static void deleteDir(final File srcDir) throws IOException {
        if (!srcDir.isDirectory()) {
            throw new IOException("source dir is not a directory"); // NOI18N
        }
        deleteContent(srcDir, true);
        if (!srcDir.delete()) {
            throw new IOException(
                "could not delete file: "
                        + srcDir.getAbsolutePath()
                        + File.pathSeparator
                        + srcDir.getName());                        // NOI18N
        }
    }

    /**
     * Extracts given Jar File to given Folder
     *
     * @param   jar     Jar File
     * @param   dest    Directory
     * @param   filter  filter
     *
     * @throws  IOException  <code>IOException</code>
     * <ul>
     * <li>dest dir does not exist</li>
     * <li>jar file does not exist</li>
     * <li>dest dir is not a directory</li>
     * <li>cannot write to dest directory</li>
     * <li>cannot read jar file</li>
     * <li>culd not create dir</li>
     * </ul>
     */
    public static void extractJar(final File jar, final File dest, final FileFilter filter) throws IOException {
        if (!dest.exists()) {
            throw new IOException("dest dir does not exist: " + dest);        // NOI18N
        }
        if (!jar.exists()) {
            throw new IOException("jar file does not exist: " + jar);         // NOI18N
        }
        if (!dest.isDirectory()) {
            throw new IOException("dest dir is not a directory: " + dest);    // NOI18N
        }
        if (!dest.canWrite()) {
            throw new IOException("cannot write to dest directory: " + dest); // NOI18N
        }
        if (!jar.canRead()) {
            throw new IOException("cannot read jar file: " + jar);            // NOI18N
        }
        final JarFile jarFile = new JarFile(jar);
        final Enumeration<JarEntry> e = jarFile.entries();
        while (e.hasMoreElements()) {
            // every entry is a file, dirs are not "recognised"
            final JarEntry entry = e.nextElement();
            final File f = new File(dest, entry.getName());
            if (!filter.accept(f)) {
                continue;
            }
            if (entry.isDirectory()) {
                if (!f.mkdirs()) {
                    throw new IOException("could not create dir: " + f); // NOI18N
                }
                continue;
            }
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                bis = new BufferedInputStream(jarFile.getInputStream(entry));
                bos = new BufferedOutputStream(new FileOutputStream(f));
                while (bis.available() > 0) {
                    bos.write(bis.read());
                }
            } finally {
                try {
                    if (bis != null) {
                        bis.close();
                    }
                } finally {
                    if (bos != null) {
                        bos.close();
                    }
                }
            }
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * Filter for searching for .jar Files
     *
     * @version  $Revision$, $Date$
     */
    public static final class JarFilter implements FileFilter {

        //~ Methods ------------------------------------------------------------

        /**
         * Tests whether the given <code>File</code> is <code>.jar File</code> or not
         * 
         * @param file given <code>File</code>
         * 
         * @return true, if the <code>File</code> is a <code>.jar File</code>
         */
        @Override
        public boolean accept(final File file) {
            return getExt(file).equalsIgnoreCase("jar"); // NOI18N
        }
    }

    /**
     * Filter for searching for .jar Files and Directories
     *
     * @version  $Revision$, $Date$
     */
    public static final class DirAndJarFilter implements FileFilter {

        //~ Methods ------------------------------------------------------------

        /**
         * Tests whether the <code>File</code> is a <code>Directory</code> or a <code>.jar File</code>
         * 
         * @param file given <code>File</code>
         * 
         * @return true, if the <code>File</code> is a <code>.jar File</code> or a <code>Directory</code>
         */
        @Override
        public boolean accept(final File file) {
            return file.isDirectory() || getExt(file).equalsIgnoreCase("jar"); // NOI18N
        }
    }

    /**
     * Filter for searching for Directories
     *
     * @version  $Revision$, $Date$
     */
    public static final class DirectoryFilter implements FileFilter {

        //~ Methods ------------------------------------------------------------

        /**
         * Tests whether the <code>File</code> is <code>Directory</code> or not.
         * 
         * @param file given <code>File</code>
         * 
         * @return true, if the <code>File</code> is a <code>Directory</code>
         */
        
        @Override
        public boolean accept(final File file) {
            return file.isDirectory();
        }
    }

    /**
     * Filter for searching for Files
     *
     * @version  $Revision$, $Date$
     */
    public static final class FilesFilter implements FileFilter {

        //~ Methods ------------------------------------------------------------

        /**
         * Tests whether the <code>File</code> is <code>Directory</code> or not.
         * 
         * @param file given <code>File</code>
         * 
         * @return true, if the <code>File</code> is Not! a <code>Directory</code>, false if it is a <code>Directory</code>
         */
        
        @Override
        public boolean accept(final File file) {
            return !file.isDirectory();
        }
    }

    /**
     * Filter for searching for MetaFiles
     *
     * @version  $Revision$, $Date$
     */
    public static final class MetaInfFilter implements FileFilter {

        //~ Methods ------------------------------------------------------------
       
        /**
         * Tests whether the <code>File</code> is <code>Meta File</code> or not.
         * 
         * @param file given <code>File</code>
         * 
         * @return true, if the <code>File</code> is a <code>Meta File</code>
         */
        @Override
        public boolean accept(final File file) {
            return !file.getAbsolutePath().contains(File.separator + "META-INF"); // NOI18N
        }
    }
}
