/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.commons.classloading;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import static org.junit.Assert.*;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public class ClassPathInfoTest {

    //~ Static fields/initializers ---------------------------------------------

    private static final String CP_PROP = "java.class.path";
    private static String CLASSPATH;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ClassPathInfoTest object.
     */
    public ClassPathInfoTest() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
        Logger.getRootLogger().addAppender(TEST_APPENDER);
        CLASSPATH = System.getProperty(CP_PROP);
    }
    
    private static final TestAppender TEST_APPENDER = new TestAppender();
    
    private static final class TestAppender extends AppenderSkeleton {
        
        private final transient Set<AppenderListener> listeners = new HashSet<AppenderListener>();
        
        public void addAppenderListener(final AppenderListener l){
            synchronized(listeners){
                listeners.add(l);
            }
        }
        
        public void removeAppenderListener(final AppenderListener l){
            synchronized(listeners){
                listeners.remove(l);
            }
        }

        @Override
        public void close()
        {
            // noop
        }

        @Override
        public boolean requiresLayout()
        {
            return false;
        }

        @Override
        protected void append(LoggingEvent event)
        {
            final Iterator<AppenderListener> it;
            
            synchronized(listeners){
                it = new HashSet<AppenderListener>(listeners).iterator();
            }
            
            while(it.hasNext()){
                it.next().loggingEvent(event);
            }
        }
        
        interface AppenderListener {
            void loggingEvent(LoggingEvent event);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @AfterClass
    public static void tearDownClass() throws Exception {
        System.setProperty(CP_PROP, CLASSPATH);
    }

    /**
     * DOCUMENT ME!
     */
    @Before
    public void setUp() {
    }

    /**
     * DOCUMENT ME!
     */
    @After
    public void tearDown() {
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getCurrentMethodName() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }

    /**
     * DOCUMENT ME!
     */
    @Test
    public void testIsInitialised() {
        System.out.println("TEST " + getCurrentMethodName());

        final File jar = new File("src/test/resources/de/cismet/commons/classloading/cismet-commons-2.0-rc1.jar");
        System.setProperty(CP_PROP, jar.getAbsolutePath());

        ClassPathInfo cpi = new ClassPathInfo();
        assertFalse(cpi.isInitialised());
        cpi.scan(false);
        assertTrue(cpi.isInitialised());
        cpi.scan(true);
        assertTrue(cpi.isInitialised());

        cpi = new ClassPathInfo();
        cpi.scan(true);
        assertTrue(cpi.isInitialised());
        cpi.scan(false);
        assertTrue(cpi.isInitialised());
    }

    /**
     * DOCUMENT ME!
     */
    @Test
    public void testScan() {
        System.out.println("TEST " + getCurrentMethodName());

        final File jar1 = new File("src/test/resources/de/cismet/commons/classloading/cismet-commons-2.0-rc1.jar");
        System.setProperty(CP_PROP, jar1.getAbsolutePath());

        final ClassPathInfo cpi = new ClassPathInfo();
        cpi.scan(false);
        Collection<String> packages = cpi.getAllPackages();
        assertEquals(12, packages.size());

        final File jar2 = new File("src/test/resources/de/cismet/commons/classloading/wss-bean-1.0.jar");
        System.setProperty(CP_PROP, jar2.getAbsolutePath());
        cpi.scan(false);
        packages = cpi.getAllPackages();
        assertEquals(12, packages.size());

        cpi.scan(true);
        packages = cpi.getAllPackages();
        assertEquals(8, packages.size());

        System.setProperty(CP_PROP, jar1.getAbsolutePath() + File.pathSeparator + jar2.getAbsolutePath());
        cpi.scan(true);
        packages = cpi.getAllPackages();
        assertEquals(20, packages.size());
    }

    /**
     * DOCUMENT ME!
     */
    @Test
    public void testGetAllPackages() {
        System.out.println("TEST " + getCurrentMethodName());

        final File jar1 = new File("src/test/resources/de/cismet/commons/classloading/cismet-commons-2.0-rc1.jar");
        System.setProperty(CP_PROP, jar1.getAbsolutePath());

        final ClassPathInfo cpi = new ClassPathInfo();
        cpi.scan(true);

        Collection<String> packages = cpi.getAllPackages();

        for (final String pakkage : packages) {
            if (!("de.cismet.cismap.commons.jtsgeometryfactories".equals(pakkage)
                            || "de.cismet.ext".equals(pakkage)
                            || "de.cismet.math.delaunytriangulation".equals(pakkage)
                            || "de.cismet.math.geometry".equals(pakkage)
                            || "de.cismet.netutil".equals(pakkage)
                            || "de.cismet.netutil.tunnel".equals(pakkage)
                            || "de.cismet.remote".equals(pakkage)
                            || "de.cismet.tools".equals(pakkage)
                            || "de.cismet.tools.collections".equals(pakkage)
                            || "de.cismet.tools.configuration".equals(pakkage)
                            || "de.cismet.veto".equals(pakkage)
                            || "Sirius.util.collections".equals(pakkage))) {
                fail("wrong package: " + pakkage);
            }
        }

        final File jar2 = new File("src/test/resources/de/cismet/commons/classloading/wss-bean-1.0.jar");
        System.setProperty(CP_PROP, jar1.getAbsolutePath() + File.pathSeparator + jar2.getAbsolutePath());
        cpi.scan(true);

        packages = cpi.getAllPackages();

        for (final String pakkage : packages) {
            if (!("de.cismet.cismap.commons.jtsgeometryfactories".equals(pakkage)
                            || "de.cismet.ext".equals(pakkage)
                            || "de.cismet.math.delaunytriangulation".equals(pakkage)
                            || "de.cismet.math.geometry".equals(pakkage)
                            || "de.cismet.netutil".equals(pakkage)
                            || "de.cismet.netutil.tunnel".equals(pakkage)
                            || "de.cismet.remote".equals(pakkage)
                            || "de.cismet.tools".equals(pakkage)
                            || "de.cismet.tools.collections".equals(pakkage)
                            || "de.cismet.tools.configuration".equals(pakkage)
                            || "de.cismet.veto".equals(pakkage)
                            || "Sirius.util.collections".equals(pakkage)
                            || "org.apache.commons.httpclient.contrib.ssl".equals(pakkage)
                            || "net.environmatics.acs.accessor".equals(pakkage)
                            || "net.environmatics.acs.accessor.interfaces".equals(pakkage)
                            || "net.environmatics.acs.accessor.methods".equals(pakkage)
                            || "net.environmatics.acs.accessor.obsolete".equals(pakkage)
                            || "net.environmatics.acs.accessor.utils".equals(pakkage)
                            || "net.environmatics.acs.exceptions".equals(pakkage)
                            || "changes".equals(pakkage))) {
                fail("wrong package: " + pakkage);
            }
        }

        System.setProperty(
            CP_PROP,
            jar1.getAbsolutePath()
                    + File.pathSeparator
                    + jar2.getAbsolutePath()
                    + File.pathSeparator
                    + jar2.getAbsolutePath());
        cpi.scan(true);

        packages = cpi.getAllPackages();

        for (final String pakkage : packages) {
            if (!("de.cismet.cismap.commons.jtsgeometryfactories".equals(pakkage)
                            || "de.cismet.ext".equals(pakkage)
                            || "de.cismet.math.delaunytriangulation".equals(pakkage)
                            || "de.cismet.math.geometry".equals(pakkage)
                            || "de.cismet.netutil".equals(pakkage)
                            || "de.cismet.netutil.tunnel".equals(pakkage)
                            || "de.cismet.remote".equals(pakkage)
                            || "de.cismet.tools".equals(pakkage)
                            || "de.cismet.tools.collections".equals(pakkage)
                            || "de.cismet.tools.configuration".equals(pakkage)
                            || "de.cismet.veto".equals(pakkage)
                            || "Sirius.util.collections".equals(pakkage)
                            || "org.apache.commons.httpclient.contrib.ssl".equals(pakkage)
                            || "net.environmatics.acs.accessor".equals(pakkage)
                            || "net.environmatics.acs.accessor.interfaces".equals(pakkage)
                            || "net.environmatics.acs.accessor.methods".equals(pakkage)
                            || "net.environmatics.acs.accessor.obsolete".equals(pakkage)
                            || "net.environmatics.acs.accessor.utils".equals(pakkage)
                            || "net.environmatics.acs.exceptions".equals(pakkage)
                            || "changes".equals(pakkage))) {
                fail("wrong package: " + pakkage);
            }
        }
        
        final File jar3 = new File("src/test/resources/de/cismet/commons/classloading/wss-bean-1.0-copy.jar");
        System.setProperty(
            CP_PROP,
            jar1.getAbsolutePath()
                    + File.pathSeparator
                    + jar2.getAbsolutePath()
                    + File.pathSeparator
                    + jar3.getAbsolutePath());
        class DoublePackageAppenderListener implements TestAppender.AppenderListener {

            private int count = 0;
            
            int getCount(){
                return count;
            }
            
            @Override
            public void loggingEvent(LoggingEvent event)
            {
                if(event.getLevel() == Level.WARN && ((String)event.getMessage()).startsWith("multiple origins for package: [package=")){
                    ++count;
                    System.out.println(event.getMessage());
                }
            }
        }
        
        final DoublePackageAppenderListener l = new DoublePackageAppenderListener();
        TEST_APPENDER.addAppenderListener(l);
        cpi.scan(true);
        TEST_APPENDER.removeAppenderListener(l);

        packages = cpi.getAllPackages();

        for (final String pakkage : packages) {
            if (!("de.cismet.cismap.commons.jtsgeometryfactories".equals(pakkage)
                            || "de.cismet.ext".equals(pakkage)
                            || "de.cismet.math.delaunytriangulation".equals(pakkage)
                            || "de.cismet.math.geometry".equals(pakkage)
                            || "de.cismet.netutil".equals(pakkage)
                            || "de.cismet.netutil.tunnel".equals(pakkage)
                            || "de.cismet.remote".equals(pakkage)
                            || "de.cismet.tools".equals(pakkage)
                            || "de.cismet.tools.collections".equals(pakkage)
                            || "de.cismet.tools.configuration".equals(pakkage)
                            || "de.cismet.veto".equals(pakkage)
                            || "Sirius.util.collections".equals(pakkage)
                            || "org.apache.commons.httpclient.contrib.ssl".equals(pakkage)
                            || "net.environmatics.acs.accessor".equals(pakkage)
                            || "net.environmatics.acs.accessor.interfaces".equals(pakkage)
                            || "net.environmatics.acs.accessor.methods".equals(pakkage)
                            || "net.environmatics.acs.accessor.obsolete".equals(pakkage)
                            || "net.environmatics.acs.accessor.utils".equals(pakkage)
                            || "net.environmatics.acs.exceptions".equals(pakkage)
                            || "changes".equals(pakkage))) {
                fail("wrong package: " + pakkage);
            }
        }
        
        assertEquals(8, l.getCount());
    }

    /**
     * DOCUMENT ME!
     */
    @Test
    public void testGetResources() {
    }
}
