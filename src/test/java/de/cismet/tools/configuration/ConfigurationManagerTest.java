/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools.configuration;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLTestCase;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;

import java.io.StringReader;

import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public class ConfigurationManagerTest implements Lookup.Provider {

    //~ Static fields/initializers ---------------------------------------------

    private static XMLOutputter out;
    private static XMLOutputter raw;
    private static XMLTestCase xmltestcase;
    private static Map<String, String> userConfig;
    private static Map<String, String> groupConfig;
    private static Map<String, String> domainConfig;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ConfigurationManagerTest object.
     */
    public ConfigurationManagerTest() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    @Before
    public void setUp() {
        userConfig = new HashMap<String, String>();
        groupConfig = new HashMap<String, String>();
        domainConfig = new HashMap<String, String>();
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
     * @throws  Throwable  DOCUMENT ME!
     */
    @BeforeClass
    public static void setUpClass() throws Throwable {
        final Properties p = new Properties();
        p.put("log4j.appender.Remote", "org.apache.log4j.net.SocketAppender");
        p.put("log4j.appender.Remote.remoteHost", "localhost");
        p.put("log4j.appender.Remote.port", "4445");
        p.put("log4j.appender.Remote.locationInfo", "true");
        p.put("log4j.rootLogger", "ALL,Remote");
        org.apache.log4j.PropertyConfigurator.configure(p);
        out = new XMLOutputter(Format.getPrettyFormat());
        raw = new XMLOutputter(Format.getRawFormat());
        xmltestcase = new XMLTestCase(ConfigurationManagerTest.class.getName()) {
            };
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
    @Ignore
    @Test
    public void testConfigure_0args() {
    }

    /**
     * DOCUMENT ME!
     */
    @Ignore
    @Test
    public void testConfigure_String() {
    }

    /**
     * DOCUMENT ME!
     */
    @Ignore
    @Test
    public void testConfigure_Configurable() {
    }

    /**
     * DOCUMENT ME!
     */
    @Ignore
    @Test
    public void testConfigure_Configurable_String() {
    }

    /**
     * DOCUMENT ME!
     */
    @Ignore
    @Test
    public void testConfigureFromClasspath_0args() {
    }

    /**
     * DOCUMENT ME!
     */
    @Ignore
    @Test
    public void testConfigureFromClasspath_Configurable() {
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Ignore
    @Test
    public void testConfigureFromClasspath_String_Configurable() throws Exception {
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test
    public void testCreateElement() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        final ConfigurationManager manager = new ConfigurationManager();

        final String simpleElement = "<testElement/>";
        Set<Element> result = manager.createElements("<root>" + simpleElement + "</root>");
        assertNotNull(result);
        assertTrue(result.size() == 1);
        xmltestcase.assertXMLEqual(simpleElement, out.outputString(result.iterator().next()));

        final String complexElement =
            "<emailConfiguration username=\"\" password=\"\" senderAddress=\"sebastian.puhl@cismet.de\" smtpHost=\"smtp.uni-saarland.de\">"
                    + "<nkfMailAddresses><receiver>lagerbuch.102@stadt.wuppertal.de</receiver></nkfMailAddresses>"
                    + "<developerMailaddresses><receiver>sebastian.puhl@cismet.de</receiver></developerMailaddresses>"
                    + "<maintenanceMailaddresses><receiver>lagerbuch.102@stadt.wuppertal.de</receiver></maintenanceMailaddresses>"
                    + "</emailConfiguration>";

        result = manager.createElements("<root>" + complexElement + "</root>");
        assertNotNull(result);
        assertTrue(result.size() == 1);
        final DetailedDiff diff = new DetailedDiff(new Diff(
                    complexElement,
                    raw.outputString(result.iterator().next())));
        assertTrue("difference: " + diff, diff.similar());

        final String manyElements = "<myElement/><myElement></myElement><myElement/>";

        result = manager.createElements("<root>" + manyElements + "</root>");
        assertNotNull(result);
        assertTrue(result.size() == 3);
        for (int i = 0; i < result.size(); ++i) {
            xmltestcase.assertXMLEqual("<myElement/>", out.outputString(result.iterator().next()));
        }

        final String noElement = "<unclosedTag>";
        result = manager.createElements(noElement);
        assertNull(result);

        final String elementNS = "<ns:element xmlns:ns=\"test\"/>";

        result = manager.createElements("<root>" + elementNS + "</root>");
        assertNotNull(result);
        assertTrue(result.size() == 1);
        xmltestcase.assertXMLEqual(elementNS, out.outputString(result.iterator().next()));
    }

    /**
     * DOCUMENT ME!
     */
    @Test
    public void testHasSubstitutionAttr() {
        System.out.println("TEST " + getCurrentMethodName());

        final ConfigurationManager manager = new ConfigurationManager();

        final String elementWithSA =
            "<root><emailConfiguration username=\"\" password=\"\" substitutionAttribute=\"abc\" senderAddress=\"sebastian.puhl@cismet.de\" smtpHost=\"smtp.uni-saarland.de\">"
                    + "<nkfMailAddresses>"
                    + "<receiver>lagerbuch.102@stadt.wuppertal.de</receiver>"
                    + "</nkfMailAddresses>"
                    + "<developerMailaddresses>"
                    + "<receiver>sebastian.puhl@cismet.de</receiver>"
                    + "</developerMailaddresses>"
                    + "<maintenanceMailaddresses>"
                    + "<receiver>lagerbuch.102@stadt.wuppertal.de</receiver>           "
                    + "</maintenanceMailaddresses>"
                    + "</emailConfiguration></root>";
        Set<Element> element = manager.createElements(elementWithSA);
        boolean result = manager.hasSubstitutionAttr(element.iterator().next());
        assertTrue(result);

        final String elementWithoutSA =
            "<root><emailConfiguration username=\"\" password=\"\" senderAddress=\"sebastian.puhl@cismet.de\" smtpHost=\"smtp.uni-saarland.de\">"
                    + "<nkfMailAddresses>"
                    + "<receiver>lagerbuch.102@stadt.wuppertal.de</receiver>"
                    + "</nkfMailAddresses>"
                    + "<developerMailaddresses>"
                    + "<receiver>sebastian.puhl@cismet.de</receiver>"
                    + "</developerMailaddresses>"
                    + "<maintenanceMailaddresses>"
                    + "<receiver>lagerbuch.102@stadt.wuppertal.de</receiver>           "
                    + "</maintenanceMailaddresses>"
                    + "</emailConfiguration></root>";

        element = manager.createElements(elementWithoutSA);
        result = manager.hasSubstitutionAttr(element.iterator().next());
        assertFalse(result);

        final String elementWithSAAndNamespace =
            "<root><ns:test substitutionAttribute=\"abc\" xmlns:ns=\"test\"/></root>";

        element = manager.createElements(elementWithSAAndNamespace);
        result = manager.hasSubstitutionAttr(element.iterator().next());
        assertTrue(result);
    }

    /**
     * DOCUMENT ME!
     */
    @Test
    public void testGetSubstitutionAttr() {
        System.out.println("TEST " + getCurrentMethodName());

        final ConfigurationManager manager = new ConfigurationManager();

        final String elementWithSA =
            "<root><emailConfiguration username=\"\" password=\"\" substitutionAttribute=\"abc\" senderAddress=\"sebastian.puhl@cismet.de\" smtpHost=\"smtp.uni-saarland.de\">"
                    + "<nkfMailAddresses>"
                    + "<receiver>lagerbuch.102@stadt.wuppertal.de</receiver>"
                    + "</nkfMailAddresses>"
                    + "<developerMailaddresses>"
                    + "<receiver>sebastian.puhl@cismet.de</receiver>"
                    + "</developerMailaddresses>"
                    + "<maintenanceMailaddresses>"
                    + "<receiver>lagerbuch.102@stadt.wuppertal.de</receiver>           "
                    + "</maintenanceMailaddresses>"
                    + "</emailConfiguration></root>";
        Set<Element> element = manager.createElements(elementWithSA);
        String result = manager.getSubstitutionAttr(element.iterator().next());
        assertEquals("abc", result);

        final String elementWithoutSA =
            "<root><emailConfiguration username=\"\" password=\"\" senderAddress=\"sebastian.puhl@cismet.de\" smtpHost=\"smtp.uni-saarland.de\">"
                    + "<nkfMailAddresses>"
                    + "<receiver>lagerbuch.102@stadt.wuppertal.de</receiver>"
                    + "</nkfMailAddresses>"
                    + "<developerMailaddresses>"
                    + "<receiver>sebastian.puhl@cismet.de</receiver>"
                    + "</developerMailaddresses>"
                    + "<maintenanceMailaddresses>"
                    + "<receiver>lagerbuch.102@stadt.wuppertal.de</receiver>           "
                    + "</maintenanceMailaddresses>"
                    + "</emailConfiguration></root>";

        element = manager.createElements(elementWithoutSA);
        result = manager.getSubstitutionAttr(element.iterator().next());
        assertNull(result);
    }

    /**
     * DOCUMENT ME!
     */
    @Test
    public void testRemoveVoid() {
        System.out.println("TEST " + getCurrentMethodName());

        final ConfigurationManager manager = new ConfigurationManager();

        final String parentNoVoid =
            "<root><emailConfiguration username=\"\" password=\"\" senderAddress=\"sebastian.puhl@cismet.de\" smtpHost=\"smtp.uni-saarland.de\">"
                    + "<nkfMailAddresses substitutionAttribute=\"abc\">"
                    + "<receiver>lagerbuch.102@stadt.wuppertal.de</receiver>"
                    + "</nkfMailAddresses>"
                    + "<developerMailaddresses>"
                    + "<receiver>sebastian.puhl@cismet.de</receiver>"
                    + "</developerMailaddresses>"
                    + "<maintenanceMailaddresses>"
                    + "<receiver>lagerbuch.102@stadt.wuppertal.de</receiver>           "
                    + "</maintenanceMailaddresses>"
                    + "</emailConfiguration></root>";
        Set<Element> parent = manager.createElements(parentNoVoid);
        Element child = parent.iterator().next().getChild("nkfMailAddresses");
        assertNotNull(parent);
        assertNotNull(child);
        manager.removeVoid(child);
        assertNotNull(parent.iterator().next().getChild("nkfMailAddresses"));

        final String parentNoVoid2 =
            "<root><emailConfiguration username=\"\" password=\"\" senderAddress=\"sebastian.puhl@cismet.de\" smtpHost=\"smtp.uni-saarland.de\">"
                    + "<nkfMailAddresses />"
                    + "<developerMailaddresses>"
                    + "<receiver>sebastian.puhl@cismet.de</receiver>"
                    + "</developerMailaddresses>"
                    + "<maintenanceMailaddresses>"
                    + "<receiver>lagerbuch.102@stadt.wuppertal.de</receiver>           "
                    + "</maintenanceMailaddresses>"
                    + "</emailConfiguration></root>";
        parent = manager.createElements(parentNoVoid2);
        child = parent.iterator().next().getChild("nkfMailAddresses");
        assertNotNull(parent.iterator().next());
        assertNotNull(child);
        manager.removeVoid(child);
        assertNotNull(parent.iterator().next().getChild("nkfMailAddresses"));

        final String parentVoid =
            "<root><emailConfiguration username=\"\" password=\"\" senderAddress=\"sebastian.puhl@cismet.de\" smtpHost=\"smtp.uni-saarland.de\">"
                    + "<nkfMailAddresses substitutionAttribute=\"abc\"/>"
                    + "<developerMailaddresses>"
                    + "<receiver>sebastian.puhl@cismet.de</receiver>"
                    + "</developerMailaddresses>"
                    + "<maintenanceMailaddresses>"
                    + "<receiver>lagerbuch.102@stadt.wuppertal.de</receiver>           "
                    + "</maintenanceMailaddresses>"
                    + "</emailConfiguration></root>";
        parent = manager.createElements(parentVoid);
        child = parent.iterator().next().getChild("nkfMailAddresses");
        assertNotNull(parent.iterator().next());
        assertNotNull(child);
        manager.removeVoid(child);
        assertNull(parent.iterator().next().getChild("nkfMailAddresses"));

        final String parentVoid2 =
            "<root><emailConfiguration username=\"\" password=\"\" senderAddress=\"sebastian.puhl@cismet.de\" smtpHost=\"smtp.uni-saarland.de\">"
                    + "<nkfMailAddresses substitutionAttribute=\"abc\">"
                    + "</nkfMailAddresses>"
                    + "<developerMailaddresses>"
                    + "<receiver>sebastian.puhl@cismet.de</receiver>"
                    + "</developerMailaddresses>"
                    + "<maintenanceMailaddresses>"
                    + "<receiver>lagerbuch.102@stadt.wuppertal.de</receiver>           "
                    + "</maintenanceMailaddresses>"
                    + "</emailConfiguration></root>";

        parent = manager.createElements(parentVoid2);
        child = parent.iterator().next().getChild("nkfMailAddresses");
        assertNotNull(parent);
        assertNotNull(child);
        manager.removeVoid(child);
        assertNull(parent.iterator().next().getChild("nkfMailAddresses"));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test
    public void testResolveNamespace() throws Exception {
        final ConfigurationManager manager = new ConfigurationManager();
        String nsTestParent = "<root xmlns:ns1=\"abc\"><ns1:el1><ns1:el2></ns1:el2></ns1:el1></root>";
        final SAXBuilder builder = new SAXBuilder(false);
        StringReader reader = new StringReader(nsTestParent);
        Element root = builder.build(reader).detachRootElement();
        reader.close();
        Element parent = (Element)((Element)root.getChildren().get(0)).getChildren().get(0);

        final String nsTestChildren = "<root xmlns:ns1=\""
                    + ConfigurationManager.DUMMY_NS_ATTR_VALUE
                    + "\" xmlns:ns2=\""
                    + ConfigurationManager.DUMMY_NS_ATTR_VALUE
                    + "\"><ns1:el1/><ns2:el2/></root>";
        final Set<Element> children = manager.createElements(nsTestChildren);
        manager.resolveNamespace(parent, children);

        final String el1Result = "<ns1:el1 xmlns:ns1=\"abc\"/>";
        String el2Result = " <ns2:el2 xmlns:ns2=\"http://www.cismet.de/config/dummyNamespace\"/>";

        Iterator<Element> it = children.iterator();
        xmltestcase.assertXMLEqual(el1Result, out.outputString(it.next()));
        xmltestcase.assertXMLEqual(el2Result, out.outputString(it.next()));

        nsTestParent = "<root xmlns:ns1=\"abc\" xmlns:ns2=\"cba\"><ns1:el1><ns1:el2></ns1:el2></ns1:el1></root>";
        reader = new StringReader(nsTestParent);
        root = builder.build(reader).detachRootElement();
        reader.close();
        parent = (Element)((Element)root.getChildren().get(0)).getChildren().get(0);

        manager.resolveNamespace(parent, children);

        el2Result = " <ns2:el2 xmlns:ns2=\"cba\"/>";

        it = children.iterator();
        xmltestcase.assertXMLEqual(el1Result, out.outputString(it.next()));
        xmltestcase.assertXMLEqual(el2Result, out.outputString(it.next()));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test
    public void testProcessElement_NoReplacement() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        final ConfigurationManager manager = new ConfigurationManager();

        final SAXBuilder builder = new SAXBuilder(false);
        final Document doc = builder.build(this.getClass().getResourceAsStream("NoReplacement.xml"));

        final Method method = manager.getClass().getDeclaredMethod("preprocessElement", Element.class);
        method.setAccessible(true);

        final Element result = (Element)method.invoke(manager, doc.getRootElement());
        assertNotNull(result);

        final Document original = builder.build(this.getClass().getResourceAsStream("NoReplacement.xml"));

        xmltestcase.assertXMLEqual(out.outputString(original), out.outputString(result));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test
    public void testProcessElement_RemoveVoid_simple() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        final ConfigurationManager manager = new ConfigurationManager();

        final SAXBuilder builder = new SAXBuilder(false);
        final Document doc = builder.build(this.getClass().getResourceAsStream("RemoveVoid_simple_test.xml"));

        final Method method = manager.getClass().getDeclaredMethod("preprocessElement", Element.class);
        method.setAccessible(true);

        final Element result = (Element)method.invoke(manager, doc.getRootElement());
        assertNotNull(result);

        final Document original = builder.build(this.getClass().getResourceAsStream("RemoveVoid_simple_expected.xml"));

        xmltestcase.assertXMLEqual(out.outputString(original), out.outputString(result));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test
    public void testProcessElement_RemoveVoid_complex() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        final ConfigurationManager manager = new ConfigurationManager();

        final SAXBuilder builder = new SAXBuilder(false);
        final Document doc = builder.build(this.getClass().getResourceAsStream("RemoveVoid_complex_test.xml"));

        final Method method = manager.getClass().getDeclaredMethod("preprocessElement", Element.class);
        method.setAccessible(true);

        final Element result = (Element)method.invoke(manager, doc.getRootElement());
        assertNotNull(result);

        final Document original = builder.build(this.getClass().getResourceAsStream("RemoveVoid_complex_expected.xml"));

        xmltestcase.assertXMLEqual(out.outputString(original), out.outputString(result));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test
    public void testProcessElement_ReplaceSingle_simple() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        final ConfigurationManager manager = new ConfigurationManager();

        final SAXBuilder builder = new SAXBuilder(false);
        final Document doc = builder.build(this.getClass().getResourceAsStream("ReplaceSingle_simple_test.xml"));

        final Method method = manager.getClass().getDeclaredMethod("preprocessElement", Element.class);
        method.setAccessible(true);

        domainConfig.put(
            "perm",
            "<root>"
                    + "<permission>"
                    + "<readWrite>true</readWrite>"
                    + "<userGroup>subGroup</userGroup>"
                    + "<userDomain>LAGIS</userDomain>"
                    + "</permission>"
                    + "</root>");
        final Element result = (Element)method.invoke(manager, doc.getRootElement());
        assertNotNull(result);

        final Document original = builder.build(this.getClass().getResourceAsStream(
                    "ReplaceSingle_simple_expected.xml"));

        xmltestcase.assertXMLEqual(out.outputString(original), out.outputString(result));
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test
    public void testProcessElement_ReplaceSingle_complex() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        final ConfigurationManager manager = new ConfigurationManager();

        final SAXBuilder builder = new SAXBuilder(false);
        final Document doc = builder.build(this.getClass().getResourceAsStream("ReplaceSingle_complex_test.xml"));

        final Method method = manager.getClass().getDeclaredMethod("preprocessElement", Element.class);
        method.setAccessible(true);

        groupConfig.put(
            "perm",
            "<root>"
                    + "<permission>"
                    + "<readWrite>true</readWrite>"
                    + "<userGroup>subGroup</userGroup>"
                    + "<userDomain>LAGIS</userDomain>"
                    + "</permission>"
                    + "</root>");

        userConfig.put(
            "email",
            "<root>"
                    + "<emailConfiguration username=\"\" password=\"\" senderAddress=\"martin.scholl@cismet.de\" smtpHost=\"smtp.uni-saarland.de\">"
                    + "<nkfMailAddresses>"
                    + "<receiver>lagerbuch.103@stadt.wuppertal.de</receiver>"
                    + "</nkfMailAddresses>"
                    + "<developerMailaddresses>"
                    + "<receiver>martin.scholl@cismet.de</receiver>"
                    + "</developerMailaddresses>"
                    + "</emailConfiguration></root>");

        final Element result = (Element)method.invoke(manager, doc.getRootElement());
        assertNotNull(result);

        final Document original = builder.build(this.getClass().getResourceAsStream(
                    "ReplaceSingle_complex_expected.xml"));

        final DetailedDiff diff = new DetailedDiff(new Diff(out.outputString(original), out.outputString(result)));
        assertTrue("diff: " + diff, diff.similar());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test
    public void testProcessElement_ReplaceSingle_uugd_simple() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        final ConfigurationManager manager = new ConfigurationManager();

        final SAXBuilder builder = new SAXBuilder(false);
        final Document doc = builder.build(this.getClass().getResourceAsStream("ReplaceSingle_uugd_simple_test.xml"));

        final Method method = manager.getClass().getDeclaredMethod("preprocessElement", Element.class);
        method.setAccessible(true);

        userConfig.put(
            "perm",
            "<root>"
                    + "<permission>"
                    + "<readWrite>true</readWrite>"
                    + "<userGroup>subUser</userGroup>"
                    + "<userDomain>LAGIS</userDomain>"
                    + "</permission>"
                    + "<permission substitutionAttribute=\"perm\"/>"
                    + "</root>");

        groupConfig.put(
            "perm",
            "<root>"
                    + "<permission substitutionAttribute=\"perm\">"
                    + "<readWrite>false</readWrite>"
                    + "<userGroup>subGroup</userGroup>"
                    + "<userDomain>LAGIS</userDomain>"
                    + "</permission>"
                    + "</root>");

        domainConfig.put(
            "perm",
            "<root>"
                    + "<permission specialPerm=\"sp\">"
                    + "<readWrite>false</readWrite>"
                    + "<userGroup>subDomain</userGroup>"
                    + "<userDomain>Test</userDomain>"
                    + "</permission>"
                    + "</root>");

        final Element result = (Element)method.invoke(manager, doc.getRootElement());
        assertNotNull(result);

        final Document original = builder.build(this.getClass().getResourceAsStream(
                    "ReplaceSingle_uugd_simple_expected.xml"));

        final DetailedDiff diff = new DetailedDiff(new Diff(out.outputString(original), out.outputString(result)));
        assertTrue("diff: " + diff, diff.similar());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test
    public void testProcessElement_ReplaceAll() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        final ConfigurationManager manager = new ConfigurationManager();

        final SAXBuilder builder = new SAXBuilder(false);
        final Document doc = builder.build(this.getClass().getResourceAsStream("ReplaceAll_test.xml"));

        final Method method = manager.getClass().getDeclaredMethod("preprocessElement", Element.class);
        method.setAccessible(true);

        userConfig.put(
            "perm",
            "<root>"
                    + "<permission>"
                    + "<readWrite>true</readWrite>"
                    + "<userGroup>subUser</userGroup>"
                    + "<userDomain>LAGIS</userDomain>"
                    + "</permission>"
                    + "<permission substitutionAttribute=\"perm\"/>"
                    + "</root>");
        userConfig.put(
            "email",
            "<root>"
                    + "<emailConfiguration substitutionAttribute=\"email\" username=\"\" password=\"\" senderAddress=\"martin.scholl@cismet.de\" smtpHost=\"smtp.uni-saarland.de\">"
                    + "<nkfMailAddresses>"
                    + "<receiver>lagerbuch.103@stadt.wuppertal.de</receiver>"
                    + "</nkfMailAddresses>"
                    + "<developerMailaddresses>"
                    + "<receiver>martin.scholl@cismet.de</receiver>"
                    + "</developerMailaddresses>"
                    + "</emailConfiguration></root>");

        groupConfig.put(
            "perm",
            "<root>"
                    + "<permission substitutionAttribute=\"perm\">"
                    + "<readWrite>false</readWrite>"
                    + "<userGroup>subGroup</userGroup>"
                    + "<userDomain>LAGIS</userDomain>"
                    + "</permission>"
                    + "</root>");
        groupConfig.put(
            "prop",
            "<root xmlns:wfs=\"http://www.cismet.de/config/dummyNamespace\">"
                    + "<wfs:PropertyName>app:the_testgroup1</wfs:PropertyName>"
                    + "<wfs:PropertyName>app:the_testgroup2</wfs:PropertyName>"
                    + "<wfs:PropertyName substitutionAttribute=\"prop\">app:the_testgroup3</wfs:PropertyName></root>");

        domainConfig.put(
            "perm",
            "<root>"
                    + "<permission specialPerm=\"sp\">"
                    + "<readWrite>false</readWrite>"
                    + "<userGroup>subDomain</userGroup>"
                    + "<userDomain>Test</userDomain>"
                    + "</permission>"
                    + "</root>");
        domainConfig.put(
            "email",
            "<root>"
                    + "<emailConfiguration substitutionAttribute=\"email\" username=\"\" password=\"\" senderAddress=\"thorsten.hell@cismet.de\" smtpHost=\"smtp.uni-saarland.de\">"
                    + "<developerMailaddresses>"
                    + "<receiver>thorsten.hell@cismet.de</receiver>"
                    + "</developerMailaddresses>"
                    + "<maintenanceMailaddresses>"
                    + "<receiver>lagerbuch.104@stadt.wuppertal.de</receiver>           "
                    + "</maintenanceMailaddresses>"
                    + "</emailConfiguration></root>");
        domainConfig.put(
            "prop",
            "<root xmlns:wfs=\"http://www.cismet.de/config/dummyNamespace\">"
                    + "<wfs:PropertyName>app:the_testdomain1</wfs:PropertyName>"
                    + "<wfs:PropertyName>app:the_testdomain2</wfs:PropertyName>"
                    + "<wfs:PropertyName>app:the_testdomain3</wfs:PropertyName>"
                    + "<wfs:PropertyName substitutionAttribute=\"prop\"/></root>");

        final Element result = (Element)method.invoke(manager, doc.getRootElement());
        assertNotNull(result);

        final Document original = builder.build(this.getClass().getResourceAsStream(
                    "ReplaceAll_expected.xml"));

        final DetailedDiff diff = new DetailedDiff(new Diff(out.outputString(original), out.outputString(result)));
        assertTrue("diff: " + diff, diff.similar());
    }

    /**
     * DOCUMENT ME!
     */
    @Ignore
    @Test
    public void testWriteConfiguration_0args() {
    }

    /**
     * DOCUMENT ME!
     */
    @Ignore
    @Test
    public void testWriteConfiguration_String() {
    }

    @Override
    public Lookup getLookup() {
        final InstanceContent ic = new InstanceContent();
        ic.add(new TestConfigAttrProvider());
        return new AbstractLookup(ic);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @ServiceProvider(service = ConfigAttrProvider.class)
    public static class TestConfigAttrProvider implements ConfigAttrProvider {

        //~ Methods ------------------------------------------------------------

        @Override
        public String getUserConfigAttr(final String key) {
            return userConfig.get(key);
        }

        @Override
        public String getGroupConfigAttr(final String key) {
            return groupConfig.get(key);
        }

        @Override
        public String getDomainConfigAttr(final String key) {
            return domainConfig.get(key);
        }
    }
}
