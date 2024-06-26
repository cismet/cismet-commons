/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools;
//package edu.stanford.ejalbert;

import org.openide.util.Exceptions;

import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.io.File;
import java.io.IOException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.net.URI;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * BrowserLauncher is a class that provides one static method, openURL, which opens the default web browser for the
 * current user of the system to the given URL. It may support other protocols depending on the system -- mailto, ftp,
 * etc. -- but that has not been rigorously tested and is not guaranteed to work.
 *
 * <p>Yes, this is platform-specific code, and yes, it may rely on classes on certain platforms that are not part of the
 * standard JDK. What we're trying to do, though, is to take something that's frequently desirable but inherently
 * platform-specific -- opening a default browser -- and allow programmers (you, for example) to do so without worrying
 * about dropping into native code or doing anything else similarly evil.</p>
 *
 * <p>Anyway, this code is completely in Java and will run on all JDK 1.1-compliant systems without modification or a
 * need for additional libraries. All classes that are required on certain platforms to allow this to run are
 * dynamically loaded at runtime via reflection and, if not found, will not cause this to do anything other than
 * returning an error when opening the browser.</p>
 *
 * <p>There are certain system requirements for this class, as it's running through Runtime.exec(), which is Java's way
 * of making a native system call. Currently, this requires that a Macintosh have a Finder which supports the GURL
 * event, which is true for Mac OS 8.0 and 8.1 systems that have the Internet Scripting AppleScript dictionary installed
 * in the Scripting Additions folder in the Extensions folder (which is installed by default as far as I know under Mac
 * OS 8.0 and 8.1), and for all Mac OS 8.5 and later systems. On Windows, it only runs under Win32 systems (Windows 95,
 * 98, and NT 4.0, as well as later versions of all). On other systems, this drops back from the inherently
 * platform-sensitive concept of a default browser and simply attempts to launch Netscape via a shell command.</p>
 *
 * <p>This code is Copyright 1999-2001 by Eric Albert (ejalbert@cs.stanford.edu) and may be redistributed or modified in
 * any form without restrictions as long as the portion of this comment from this paragraph through the end of the
 * comment is not removed. The author requests that he be notified of any application, applet, or other binary that
 * makes use of this code, but that's more out of curiosity than anything and is not required. This software includes no
 * warranty. The author is not repsonsible for any loss of data or functionality or any adverse or unexpected effects of
 * using this software.</p>
 *
 * <p>Credits:<br>
 * Steven Spencer, JavaWorld magazine (<a href="http://www.javaworld.com/javaworld/javatips/jw-javatip66.html">Java Tip
 * 66</a>)<br>
 * Thanks also to Ron B. Yeh, Eric Shapiro, Ben Engber, Paul Teitlebaum, Andrea Cantatore, Larry Barowski, Trevor
 * Bedzek, Frank Miedrich, and Ron Rabakukk</p>
 *
 * @author   Eric Albert (<a href="mailto:ejalbert@cs.stanford.edu">ejalbert@cs.stanford.edu</a>)
 * @version  1.4b1 (Released June 20, 2001)
 */
public class BrowserLauncher {

    //~ Static fields/initializers ---------------------------------------------

    private static Desktop workingDesktop = null;

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(BrowserLauncher.class);
    /**
     * The Java virtual machine that we are running on. Actually, in most cases we only care about the operating system,
     * but some operating systems require us to switch on the VM.
     */
    private static int jvm;
    /** The browser for the system. */
    private static Object browser;
    /**
     * Caches whether any classes, methods, and fields that are not part of the JDK and need to be dynamically loaded at
     * runtime loaded successfully.
     *
     * <p>Note that if this is <code>false</code>, <code>openURL()</code> will always return an IOException.</p>
     */
    private static boolean loadedWithoutErrors;
    /** The com.apple.mrj.MRJFileUtils class. */
    private static Class mrjFileUtilsClass;
    /** The com.apple.mrj.MRJOSType class. */
    private static Class mrjOSTypeClass;
    /** The com.apple.MacOS.AEDesc class. */
    private static Class aeDescClass;
    /** The <init>(int) method of com.apple.MacOS.AETarget. */
    private static Constructor aeTargetConstructor;
    /** The <init>(int, int, int) method of com.apple.MacOS.AppleEvent. */
    private static Constructor appleEventConstructor;
    /** The <init>(String) method of com.apple.MacOS.AEDesc. */
    private static Constructor aeDescConstructor;
    /** The findFolder method of com.apple.mrj.MRJFileUtils. */
    private static Method findFolder;
    /** The getFileCreator method of com.apple.mrj.MRJFileUtils. */
    private static Method getFileCreator;
    /** The getFileType method of com.apple.mrj.MRJFileUtils. */
    private static Method getFileType;
    /** The openURL method of com.apple.mrj.MRJFileUtils. */
    private static Method openURL;
    /** The makeOSType method of com.apple.MacOS.OSUtils. */
    private static Method makeOSType;
    /** The putParameter method of com.apple.MacOS.AppleEvent. */
    private static Method putParameter;
    /** The sendNoReply method of com.apple.MacOS.AppleEvent. */
    private static Method sendNoReply;
    /** Actually an MRJOSType pointing to the System Folder on a Macintosh. */
    private static Object kSystemFolderType;
    /** The keyDirectObject AppleEvent parameter type. */
    private static Integer keyDirectObject;
    /** The kAutoGenerateReturnID AppleEvent code. */
    private static Integer kAutoGenerateReturnID;
    /** The kAnyTransactionID AppleEvent code. */
    private static Integer kAnyTransactionID;
    /** The linkage object required for JDirect 3 on Mac OS X. */
    private static Object linkage;
    /** The framework to reference on Mac OS X. */
    private static final String JDirect_MacOSX =
        "/System/Library/Frameworks/Carbon.framework/Frameworks/HIToolbox.framework/HIToolbox"; // NOI18N
    /** JVM constant for MRJ 2.0. */
    private static final int MRJ_2_0 = 0;
    /** JVM constant for MRJ 2.1 or later. */
    private static final int MRJ_2_1 = 1;
    /** JVM constant for Java on Mac OS X 10.0 (MRJ 3.0). */
    private static final int MRJ_3_0 = 3;
    /** JVM constant for MRJ 3.1. */
    private static final int MRJ_3_1 = 4;
    /** JVM constant for any Windows NT JVM. */
    private static final int WINDOWS_NT = 5;
    /** JVM constant for any Windows 9x JVM. */
    private static final int WINDOWS_9x = 6;
    /** JVM constant for any other platform. */
    private static final int OTHER = -1;
    /**
     * The file type of the Finder on a Macintosh. Hardcoding "Finder" would keep non-U.S. English systems from working
     * properly.
     */
    private static final String FINDER_TYPE = "FNDR"; // NOI18N
    /** The creator code of the Finder on a Macintosh, which is needed to send AppleEvents to the application. */
    private static final String FINDER_CREATOR = "MACS";                                                      // NOI18N
    /** The name for the AppleEvent type corresponding to a GetURL event. */
    private static final String GURL_EVENT = "GURL";                     // NOI18N
    /** The first parameter that needs to be passed into Runtime.exec() to open the default web browser on Windows. */
    private static final String FIRST_WINDOWS_PARAMETER = "/c";                                                    // NOI18N
    /** The second parameter for Runtime.exec() on Windows. */
    private static final String SECOND_WINDOWS_PARAMETER = "start"; // NOI18N
    /**
     * The third parameter for Runtime.exec() on Windows. This is a "title" parameter that the command line expects.
     * Setting this parameter allows URLs containing spaces to work.
     */
    private static final String THIRD_WINDOWS_PARAMETER = "\"\""; // NOI18N
    /**
     * The shell parameters for Netscape that opens a given URL in an already-open copy of Netscape on many command-line
     * systems.
     */
    private static final String NETSCAPE_REMOTE_PARAMETER = "-remote";       // NOI18N
    private static final String NETSCAPE_OPEN_PARAMETER_START = "'openURL("; // NOI18N
    private static final String NETSCAPE_OPEN_PARAMETER_END = ")'";          // NOI18N
    /** The message from any exception thrown throughout the initialization process. */
    private static String errorMessage;

    private static String customBrowserCmd;

    /**
     * An initialization block that determines the operating system and loads the necessary runtime data.
     */
    static {
        if (!Desktop.isDesktopSupported()) {
            try {
                loadedWithoutErrors = true;
                final String osName = System.getProperty("os.name");             // NOI18N
                if (osName.startsWith("Mac OS")) {                               // NOI18N
                    final String mrjVersion = System.getProperty("mrj.version"); // NOI18N
                    final String majorMRJVersion = mrjVersion.substring(0, 3);
                    try {
                        final double version = Double.valueOf(majorMRJVersion);
                        if (version == 2) {
                            jvm = MRJ_2_0;
                        } else if ((version >= 2.1) && (version < 3)) {
                            // Assume that all 2.x versions of MRJ work the same.  MRJ 2.1 actually
                            // works via Runtime.exec() and 2.2 supports that but has an openURL() method
                            // as well that we currently ignore.
                            jvm = MRJ_2_1;
                        } else if (version == 3.0) {
                            jvm = MRJ_3_0;
                        } else if (version >= 3.1) {
                            // Assume that all 3.1 and later versions of MRJ work the same.
                            jvm = MRJ_3_1;
                        } else {
                            loadedWithoutErrors = false;
                            errorMessage = "Unsupported MRJ version: " + version; // NOI18N
                        }
                    } catch (NumberFormatException nfe) {
                        loadedWithoutErrors = false;
                        errorMessage = "Invalid MRJ version: " + mrjVersion;      // NOI18N
                    }
                } else if (osName.startsWith("Windows")) {                        // NOI18N
                    if (osName.indexOf("9") != -1) {                              // NOI18N
                        jvm = WINDOWS_9x;
                    } else {
                        jvm = WINDOWS_NT;
                    }
                } else {
                    jvm = OTHER;
                }

                if (loadedWithoutErrors) { // if we haven't hit any errors yet
                    loadedWithoutErrors = loadClasses();
                }
            } catch (Throwable t) {
                log.warn("Problem with BrowserLauncher", t);
                throw new RuntimeException(t);
            }
        }
    }

    //~ Constructors -----------------------------------------------------------

    /**
     * Attempts to open the given URL with the default web browser.
     */

    /**
     * This class should be never be instantiated; this just ensures so.
     */
    private BrowserLauncher() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Called by a static initializer to load any classes, fields, and methods required at runtime to locate the user's
     * web browser.
     *
     * @return  <code>true</code> if all intialization succeeded <code>false</code> if any portion of the initialization
     *          failed
     */
    private static boolean loadClasses() {
        switch (jvm) {
            case MRJ_2_0: {
                try {
                    final Class aeTargetClass = Class.forName("com.apple.MacOS.AETarget");     // NOI18N
                    final Class osUtilsClass = Class.forName("com.apple.MacOS.OSUtils");       // NOI18N
                    final Class appleEventClass = Class.forName("com.apple.MacOS.AppleEvent"); // NOI18N
                    final Class aeClass = Class.forName("com.apple.MacOS.ae");                 // NOI18N
                    aeDescClass = Class.forName("com.apple.MacOS.AEDesc");                     // NOI18N

                    aeTargetConstructor = aeTargetClass.getDeclaredConstructor(new Class[] { int.class });
                    appleEventConstructor = appleEventClass.getDeclaredConstructor(
                            new Class[] { int.class, int.class, aeTargetClass, int.class, int.class });
                    aeDescConstructor = aeDescClass.getDeclaredConstructor(new Class[] { String.class });

                    makeOSType = osUtilsClass.getDeclaredMethod("makeOSType", new Class[] { String.class }); // NOI18N
                    putParameter = appleEventClass.getDeclaredMethod(
                            "putParameter",
                            new Class[] { int.class, aeDescClass });                                         // NOI18N
                    sendNoReply = appleEventClass.getDeclaredMethod("sendNoReply", new Class[] {});          // NOI18N

                    final Field keyDirectObjectField = aeClass.getDeclaredField("keyDirectObject");                     // NOI18N
                    keyDirectObject = (Integer)keyDirectObjectField.get(null);
                    final Field autoGenerateReturnIDField = appleEventClass.getDeclaredField("kAutoGenerateReturnID");  // NOI18N
                    kAutoGenerateReturnID = (Integer)autoGenerateReturnIDField.get(null);
                    final Field anyTransactionIDField = appleEventClass.getDeclaredField("kAnyTransactionID");          // NOI18N
                    kAnyTransactionID = (Integer)anyTransactionIDField.get(null);
                } catch (ClassNotFoundException cnfe) {
                    errorMessage = cnfe.getMessage();
                    return false;
                } catch (NoSuchMethodException nsme) {
                    errorMessage = nsme.getMessage();
                    return false;
                } catch (NoSuchFieldException nsfe) {
                    errorMessage = nsfe.getMessage();
                    return false;
                } catch (IllegalAccessException iae) {
                    errorMessage = iae.getMessage();
                    return false;
                }
                break;
            }
            case MRJ_2_1: {
                try {
                    mrjFileUtilsClass = Class.forName("com.apple.mrj.MRJFileUtils");                                    // NOI18N
                    mrjOSTypeClass = Class.forName("com.apple.mrj.MRJOSType");                                          // NOI18N
                    final Field systemFolderField = mrjFileUtilsClass.getDeclaredField("kSystemFolderType");            // NOI18N
                    kSystemFolderType = systemFolderField.get(null);
                    findFolder = mrjFileUtilsClass.getDeclaredMethod("findFolder", new Class[] { mrjOSTypeClass });     // NOI18N
                    getFileCreator = mrjFileUtilsClass.getDeclaredMethod("getFileCreator", new Class[] { File.class }); // NOI18N
                    getFileType = mrjFileUtilsClass.getDeclaredMethod("getFileType", new Class[] { File.class });       // NOI18N
                } catch (ClassNotFoundException cnfe) {
                    errorMessage = cnfe.getMessage();
                    return false;
                } catch (NoSuchFieldException nsfe) {
                    errorMessage = nsfe.getMessage();
                    return false;
                } catch (NoSuchMethodException nsme) {
                    errorMessage = nsme.getMessage();
                    return false;
                } catch (SecurityException se) {
                    errorMessage = se.getMessage();
                    return false;
                } catch (IllegalAccessException iae) {
                    errorMessage = iae.getMessage();
                    return false;
                }
                break;
            }
            case MRJ_3_0: {
                try {
                    final Class linker = Class.forName("com.apple.mrj.jdirect.Linker");                                 // NOI18N
                    final Constructor constructor = linker.getConstructor(new Class[] { Class.class });
                    linkage = constructor.newInstance(new Object[] { BrowserLauncher.class });
                } catch (ClassNotFoundException cnfe) {
                    errorMessage = cnfe.getMessage();
                    return false;
                } catch (NoSuchMethodException nsme) {
                    errorMessage = nsme.getMessage();
                    return false;
                } catch (InvocationTargetException ite) {
                    errorMessage = ite.getMessage();
                    return false;
                } catch (InstantiationException ie) {
                    errorMessage = ie.getMessage();
                    return false;
                } catch (IllegalAccessException iae) {
                    errorMessage = iae.getMessage();
                    return false;
                }
                break;
            }
            case MRJ_3_1: {
                try {
                    mrjFileUtilsClass = Class.forName("com.apple.mrj.MRJFileUtils");                                    // NOI18N
                    openURL = mrjFileUtilsClass.getDeclaredMethod("openURL", new Class[] { String.class });             // NOI18N
                } catch (ClassNotFoundException cnfe) {
                    errorMessage = cnfe.getMessage();
                    return false;
                } catch (NoSuchMethodException nsme) {
                    errorMessage = nsme.getMessage();
                    return false;
                }
                break;
            }
            default: {
                break;
            }
        }
        return true;
    }

    /**
     * Attempts to locate the default web browser on the local system. Caches results so it only locates the browser
     * once for each use of this class per JVM instance.
     *
     * @return  The browser for the system. Note that this may not be what you would consider to be a standard web
     *          browser; instead, it's the application that gets called to open the default web browser. In some cases,
     *          this will be a non-String object that provides the means of calling the default browser.
     */
    private static Object locateBrowser() {
        if (browser != null) {
            return browser;
        }
        switch (jvm) {
            case MRJ_2_0: {
                try {
                    final Integer finderCreatorCode = (Integer)makeOSType.invoke(null, new Object[] { FINDER_CREATOR });
                    final Object aeTarget = aeTargetConstructor.newInstance(new Object[] { finderCreatorCode });
                    final Integer gurlType = (Integer)makeOSType.invoke(null, new Object[] { GURL_EVENT });
                    final Object appleEvent = appleEventConstructor.newInstance(
                            new Object[] { gurlType, gurlType, aeTarget, kAutoGenerateReturnID, kAnyTransactionID });
                    // Don't set browser = appleEvent because then the next time we call
                    // locateBrowser(), we'll get the same AppleEvent, to which we'll already have
                    // added the relevant parameter. Instead, regenerate the AppleEvent every time.
                    // There's probably a way to do this better; if any has any ideas, please let
                    // me know.
                    return appleEvent;
                } catch (IllegalAccessException iae) {
                    browser = null;
                    errorMessage = iae.getMessage();
                    return browser;
                } catch (InstantiationException ie) {
                    browser = null;
                    errorMessage = ie.getMessage();
                    return browser;
                } catch (InvocationTargetException ite) {
                    browser = null;
                    errorMessage = ite.getMessage();
                    return browser;
                }
            }
            case MRJ_2_1: {
                final File systemFolder;
                try {
                    systemFolder = (File)findFolder.invoke(null, new Object[] { kSystemFolderType });
                } catch (IllegalArgumentException iare) {
                    browser = null;
                    errorMessage = iare.getMessage();
                    return browser;
                } catch (IllegalAccessException iae) {
                    browser = null;
                    errorMessage = iae.getMessage();
                    return browser;
                } catch (InvocationTargetException ite) {
                    browser = null;
                    errorMessage = ite.getTargetException().getClass() + ": " + ite.getTargetException().getMessage(); // NOI18N
                    return browser;
                }
                final String[] systemFolderFiles = systemFolder.list();
                // Avoid a FilenameFilter because that can't be stopped mid-list
                for (int i = 0; i < systemFolderFiles.length; i++) {
                    try {
                        final File file = new File(systemFolder, systemFolderFiles[i]);
                        if (!file.isFile()) {
                            continue;
                        }
                        // We're looking for a file with a creator code of 'MACS' and
                        // a type of 'FNDR'.  Only requiring the type results in non-Finder
                        // applications being picked up on certain Mac OS 9 systems,
                        // especially German ones, and sending a GURL event to those
                        // applications results in a logout under Multiple Users.
                        final Object fileType = getFileType.invoke(null, new Object[] { file });
                        if (FINDER_TYPE.equals(fileType.toString())) {
                            final Object fileCreator = getFileCreator.invoke(null, new Object[] { file });
                            if (FINDER_CREATOR.equals(fileCreator.toString())) {
                                browser = file.toString(); // Actually the Finder, but that's OK
                                return browser;
                            }
                        }
                    } catch (IllegalArgumentException iare) {
                        browser = browser;
                        errorMessage = iare.getMessage();
                        return null;
                    } catch (IllegalAccessException iae) {
                        browser = null;
                        errorMessage = iae.getMessage();
                        return browser;
                    } catch (InvocationTargetException ite) {
                        browser = null;
                        errorMessage = ite.getTargetException().getClass() + ": "
                                    + ite.getTargetException().getMessage(); // NOI18N
                        return browser;
                    }
                }
                browser = null;
                break;
            }
            case MRJ_3_0:
            case MRJ_3_1: {
                browser = "";                              // NOI18N// Return something non-null
                break;
            }
            case WINDOWS_NT: {
                browser = "cmd.exe";                       // NOI18N
                break;
            }
            case WINDOWS_9x: {
                browser = "command.com";                   // NOI18N
                break;
            }
            case OTHER:
            default: {
                browser = "firefox";                       // NOI18N
                break;
            }
        }
        return browser;
    }

    /**
     * Attempts to open the default web browser to the given URL. In the second attempt he tries to open the url as a
     * file.
     *
     * @param   url  url
     *
     * @throws  RuntimeException  DOCUMENT ME!
     */
    public static void openURLorFile(final String url) { // TODO should declare "throws Exception"
        if (url == null) {
            return;
        }
        String gotoUrl = url;
        try {
            openURL(gotoUrl);
        } catch (final Exception e1) {
            log.warn("Error while opening the url: " + gotoUrl + "\n Trying to open it as url-file.", e1);
            try {
                gotoUrl = gotoUrl.replaceAll("\\\\", "/");
                gotoUrl = gotoUrl.replaceAll(" ", "%20");
                openURL("file:///" + gotoUrl);
            } catch (final Exception e2) {
                log.error("Could not open file:///" + gotoUrl, e2);
                try {
                    final File file = new File(url);
                    if (file.canRead() && Desktop.isDesktopSupported()) {
                        getWorkingDesktop().open(file);
                        return;
                    }
                } catch (final Exception e3) {
                    log.error("File " + gotoUrl + " could not be opened.", e3);
                    throw new RuntimeException(e3);      // should throw the exception itself, not a runtimeexception
                }
                throw new RuntimeException(e2);          // should throw the exception itself, not a runtimeexception
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static Desktop getWorkingDesktop() {
        if (workingDesktop == null) {
            try {
                workingDesktop = Desktop.getDesktop();
            } catch (Throwable t) {
                log.error("No Desktop for you", t);
            }
        }
        return workingDesktop;
    }

    /**
     * DOCUMENT ME!
     */
    public static void initializeDesktop() {
        getWorkingDesktop();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        try {
            String type = (args.length > 0) ? args[0] : "runtime";
            String uri = (args.length > 1) ? args[1] : "http://test.loca";
            String option = (args.length > 2) ? args[2] : "google-chrome";
            if ("graphical".equals(type)) {
                final JComboBox<String> typeCb = new JComboBox<>(
                        new String[] { "runtime", "openUrl", "openUrlOrFile", "openUrlOrFileFixed", "desktopBrowse" });
                final JTextField uriTf = new JTextField(uri);
                final JTextField optionTf = new JTextField(option);
                final JPanel panel = new JPanel(new GridBagLayout());
                final GridBagConstraints gbConstraits = new GridBagConstraints();
                gbConstraits.insets = new Insets(4, 4, 4, 4);
                gbConstraits.fill = GridBagConstraints.BOTH;
                gbConstraits.gridy = 0;
                panel.add(new JLabel("type:"), gbConstraits);
                panel.add(typeCb, gbConstraits);
                gbConstraits.gridy = 1;
                panel.add(new JLabel("uri:"), gbConstraits);
                panel.add(uriTf, gbConstraits);
                gbConstraits.gridy = 2;
                panel.add(new JLabel("option:"), gbConstraits);
                gbConstraits.weightx = 1;
                panel.add(optionTf, gbConstraits);
                final int ret = JOptionPane.showConfirmDialog(
                        null,
                        panel,
                        "BrowserLauncher Tester",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);
                if (ret == JOptionPane.CANCEL_OPTION) {
                    return;
                }
                type = (String)typeCb.getSelectedItem();
                uri = uriTf.getText();
                option = optionTf.getText();
            }

            switch (type) {
                case "runtime": {
                    Runtime.getRuntime().exec(new String[] { option, uri });
                }
                break;
                case "openUrl": {
                    openURL(uri);
                }
                break;
                case "openUrlOrFile": {
                    openURLorFile(uri);
                }
                break;
                case "openUrlOrFileFixed": {
                    openURL("file:///" + uri.replaceAll("\\\\", "/").replaceAll(" ", "%20"));
                }
                break;
                case "desktopBrowse": {
                    getWorkingDesktop().browse(new URI(uri));
                }
                break;
                default: {
                    throw new IllegalArgumentException("unkown type");
                }
            }
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   url  DOCUMENT ME!
     *
     * @throws  Exception    DOCUMENT ME!
     * @throws  IOException  DOCUMENT ME!
     */
    public static void openURL(final String url) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("BrowserLauncher.openUrl:" + url);                                // NOI18N
        }
        boolean isValidUrl = false;
        if (!url.toLowerCase().startsWith("file:")) {
            try {
                new URL(url).toString();
                isValidUrl = true;
            } catch (final Exception ex) {
            }
        }
        if (isValidUrl && (customBrowserCmd != null)) {
            final List<String> list = new ArrayList<>();
            final Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(customBrowserCmd);
            while (m.find()) {
                list.add(m.group(1).replace("\"", "").replace("<url>", url));
            }
            Runtime.getRuntime().exec(list.toArray(new String[0]));
        } else if (Desktop.isDesktopSupported()) {                                      // NOI18N
            getWorkingDesktop().browse(new URI(url));
        } else {
            if (!loadedWithoutErrors) {
                throw new IOException("Exception in finding browser: " + errorMessage); // NOI18N
            }
            Object browser = locateBrowser();
            if (browser == null) {
                throw new IOException("Unable to locate browser: " + errorMessage);     // NOI18N
            }

            switch (jvm) {
                case MRJ_2_0: {
                    Object aeDesc = null;
                    try {
                        aeDesc = aeDescConstructor.newInstance(new Object[] { url });
                        putParameter.invoke(browser, new Object[] { keyDirectObject, aeDesc });
                        sendNoReply.invoke(browser, new Object[] {});
                    } catch (InvocationTargetException ite) {
                        throw new IOException("InvocationTargetException while creating AEDesc: " + ite.getMessage());  // NOI18N
                    } catch (IllegalAccessException iae) {
                        throw new IOException("IllegalAccessException while building AppleEvent: " + iae.getMessage()); // NOI18N
                    } catch (InstantiationException ie) {
                        throw new IOException("InstantiationException while creating AEDesc: " + ie.getMessage());      // NOI18N
                    } finally {
                        aeDesc = null;                                                                                  // Encourage it to get disposed if it was created
                        browser = null;                                                                                 // Ditto
                    }
                    break;
                }
                case MRJ_2_1: {
                    Runtime.getRuntime().exec(new String[] { (String)browser, url });
                    break;
                }
                case MRJ_3_0: {
                    final int[] instance = new int[1];
                    int result = ICStart(instance, 0);
                    if (result == 0) {
                        final int[] selectionStart = new int[] { 0 };
                        final byte[] urlBytes = url.getBytes();
                        final int[] selectionEnd = new int[] { urlBytes.length };
                        result = ICLaunchURL(
                                instance[0],
                                new byte[] { 0 },
                                urlBytes,
                                urlBytes.length,
                                selectionStart,
                                selectionEnd);
                        if (result == 0) {
                            // Ignore the return value; the URL was launched successfully
                            // regardless of what happens here.
                            ICStop(instance);
                        } else {
                            throw new IOException("Unable to launch URL: " + result);                     // NOI18N
                        }
                    } else {
                        throw new IOException("Unable to create an Internet Config instance: " + result); // NOI18N
                    }
                    break;
                }
                case MRJ_3_1: {
                    try {
                        openURL.invoke(null, new Object[] { url });
                    } catch (InvocationTargetException ite) {
                        throw new IOException("InvocationTargetException while calling openURL: " + ite.getMessage()); // NOI18N
                    } catch (IllegalAccessException iae) {
                        throw new IOException("IllegalAccessException while calling openURL: " + iae.getMessage()); // NOI18N
                    }
                    break;
                }
                case WINDOWS_NT:
                case WINDOWS_9x:
                    // Add quotes around the URL to allow ampersands and other special
                    // characters to work.
                    Process process = Runtime.getRuntime()
                                .exec(
                                    new String[] {
                                        (String)browser,
                                        FIRST_WINDOWS_PARAMETER,
                                        SECOND_WINDOWS_PARAMETER,
                                        THIRD_WINDOWS_PARAMETER,
                                        '"'
                                        + url
                                        + '"'
                                    });
                    // This avoids a memory leak on some versions of Java on Windows.
                    // That's hinted at in <http://developer.java.sun.com/developer/qow/archive/68/>.
                    try {
                        process.waitFor();
                        process.exitValue();
                    } catch (InterruptedException ie) {
                        throw new IOException("InterruptedException while launching browser: " + ie.getMessage()); // NOI18N
                    }
                    break;
                case OTHER: {
                    // Assume that we're on Unix and that Netscape is installed

                    // First, attempt to open the URL in a currently running session of Netscape
                    process = Runtime.getRuntime()
                                .exec(
                                        new String[] {
                                            (String)browser,
                                            NETSCAPE_REMOTE_PARAMETER,
                                            NETSCAPE_OPEN_PARAMETER_START
                                            + url
                                            + NETSCAPE_OPEN_PARAMETER_END
                                        });
                    try {
                        final int exitCode = process.waitFor();
                        if (exitCode != 0) {                                                                       // if Netscape was not open
                            Runtime.getRuntime().exec(new String[] { (String)browser, url });
                        }
                    } catch (InterruptedException ie) {
                        throw new IOException("InterruptedException while launching browser: " + ie.getMessage()); // NOI18N
                    }
                    break;
                }
                default: {
                    // This should never occur, but if it does, we'll try the simplest thing possible
                    Runtime.getRuntime().exec(new String[] { (String)browser, url });
                    break;
                }
            }
        }
    }

    /**
     * Methods required for Mac OS X. The presence of native methods does not cause any problems on other platforms.
     * //Placeholder
     *
     * @param   instance   instance
     * @param   signature  signature
     *
     * @return  int
     */
    private static native int ICStart(int[] instance, int signature);

    /**
     * Methods required for Mac OS X. The presence of native methods does not cause any problems on other platforms.
     * //Placeholder
     *
     * @param   instance  instance
     *
     * @return  int
     */
    private static native int ICStop(int[] instance);

    /**
     * Methods required for Mac OS X. The presence of native methods does not cause any problems on other platforms.
     * //Placeholder
     *
     * @param   instance        intance
     * @param   hint            hint
     * @param   data            data
     * @param   len             len
     * @param   selectionStart  selectionStart
     * @param   selectionEnd    selectiomEnd
     *
     * @return  int
     */
    private static native int ICLaunchURL(int instance,
            byte[] hint,
            byte[] data,
            int len,
            int[] selectionStart,
            int[] selectionEnd);

    /**
     * DOCUMENT ME!
     *
     * @param  customBrowserCmd  DOCUMENT ME!
     */
    public static void setCustomBrowserCmd(final String customBrowserCmd) {
        BrowserLauncher.customBrowserCmd = customBrowserCmd;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getCustomBrowserCmd() {
        return customBrowserCmd;
    }
}
