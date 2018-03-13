/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools.configuration;

import org.apache.log4j.Logger;

import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import org.openide.util.Lookup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.cismet.connectioncontext.AbstractConnectionContext;
import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

/**
 * Configuaration Manager.
 *
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
public class ConfigurationManager {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(ConfigurationManager.class);

    public static final String SUBSTITUTION_ATTR = "substitutionAttribute";                        // NOI18N
    public static final String DUMMY_NS_ATTR_VALUE = "http://www.cismet.de/config/dummyNamespace"; // NOI18N
    private static final String XML_ENCODING;

    static {
        final String charset = Charset.defaultCharset().toString();
        if ("MacRoman".equals(charset)) { // NOI18N
            XML_ENCODING = "UTF-8";
        } else {
            XML_ENCODING = "ISO-8859-1";
        }
    }

    //~ Instance fields --------------------------------------------------------

    private final List<Configurable> configurables;
    private String fileName;
    private String fallBackFileName;
    private String defaultFileName;
    private String classPathFolder;
    private String folder;
    private String home;
    private String fs;
    private Element serverRootObject;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of ConfigurationManager.
     */
    public ConfigurationManager() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create ConfigurationManager."); // NOI18N
        }
        home = System.getProperty("user.home");        // NOI18N
        fs = System.getProperty("file.separator");     // NOI18N
        fileName = "configuration.xml";
        fallBackFileName = fileName;
        defaultFileName = fileName;
        classPathFolder = "/";
        folder = ".cismet";
        configurables = new ArrayList<Configurable>();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Appends specified <code>Configurable</code> to the list {@link #configurables}.
     *
     * @param  configurable  <code>Configurable</code>, which should get append to the List
     */
    public void addConfigurable(final Configurable configurable) {
        configurables.add(configurable);
    }

    /**
     * Removes specified <code>Configurable</code> from the list {@link #configurables}.
     *
     * @param  configurable  <code>Configurable</code>, which should get removed from the List
     */
    public void removeConfigurable(final Configurable configurable) {
        configurables.remove(configurable);
    }

    /**
     * Getter for <code>fileName</code>.
     *
     * @return  {@link #fileName}
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Setter for <code>fileName</code>.
     *
     * @param  fileName  {@link #fileName}
     */
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * Getter for <code>folder</code>.
     *
     * @return  {@link #folder}
     */
    public String getFolder() {
        return folder;
    }

    /**
     * Setter for <code>folder</code>.
     *
     * @param  folder  {@link #folder}
     */
    public void setFolder(final String folder) {
        this.folder = folder;
    }

    /**
     * Configures all <code>Configurable</code> in <code>configuables</code>; Uses the deafault Path to the local root
     * Object.
     *
     * @see  #configure(de.cismet.tools.configuration.Configurable)
     */
    public void configure() {
        configure((Configurable)null);
    }

    /**
     * Configures all <code>Configurable</code> in <code>configuables</code>; Uses a specified Path to the local root
     * Object.
     *
     * @param  path  specified path
     *
     * @see    #configure(de.cismet.tools.configuration.Configurable, java.lang.String)
     */
    public void configure(final String path) {
        configure(null, path);
    }

    /**
     * Configures the specified <code>Configurable</code>; Uses the default Path to the local root Object.
     *
     * @param  singleConfig  specified <code>Configurable</code>; if ==null: uses all <code>Configurable</code> in
     *                       <code>configurables</code>.
     *
     * @see    #configure(de.cismet.tools.configuration.Configurable, java.lang.String)
     */
    public void configure(final Configurable singleConfig) {
        configure(singleConfig, home + fs + folder + fs + fileName);
    }

    /**
     * Configures the specified <code>Configurable</code>; Uses a specified path to the local Object.
     *
     * @param  singleConfig  specified <code>Configurable</code>; if ==null: uses all <code>Configurable</code> in
     *                       <code>configurables</code>.
     * @param  path          specified <code>path</code> to the local root Object.
     *
     * @see    #pureConfigure(de.cismet.tools.configuration.Configurable, org.jdom.Element, org.jdom.Element)
     */
    public void configure(final Configurable singleConfig, final String path) {
        Element rootObject = null;
        try {
            final SAXBuilder builder = new SAXBuilder(false);
            final Document doc = builder.build(new File(path));

            rootObject = doc.getRootElement();
        } catch (final Exception e) {
            final String message = "Error while reading configuration (User.Home) (" + singleConfig // NOI18N
                        + ") if null then all are";                                                 // NOI18N
            LOG.warn(message, e);
        }

        if (rootObject == null) {
            // load predefined
            rootObject = getRootObjectFromClassPath();
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("rootObject:" + rootObject); // NOI18N
        }

        final Element srvRootObj = getRootObjectFromClassPath();

        final Format format = Format.getPrettyFormat();
        // TODO: WHY NOT USING UTF-8
        format.setEncoding("ISO-8859-1"); // NOI18N
        final XMLOutputter serializer = new XMLOutputter(format);

        if (LOG.isInfoEnabled()) {
            LOG.info("Configuration Document: " + serializer.outputString(rootObject.getDocument()));        // NOI18N
            LOG.info("Server Configuration Document: " + serializer.outputString(srvRootObj.getDocument())); // NOI18N
        }
        pureConfigure(singleConfig, rootObject, srvRootObj);
    }

    /**
     * Configures all <code>Configurable</code> in <code>configurables</code>; Uses the Classpath to the local root
     * Object.
     *
     * @see  #configureFromClasspath(de.cismet.tools.configuration.Configurable)
     */
    public void configureFromClasspath() {
        configureFromClasspath(null);
    }

    /**
     * Configures the specified <code>Configurable</code>; Uses the Classpath to the local root Object.
     *
     * @param  singleConfig  specified <code>Configurable</code>; if ==null uses all <code>Configurable</code> in <code>
     *                       configurables</code>.
     *
     * @see    #pureConfigure(de.cismet.tools.configuration.Configurable, org.jdom.Element, org.jdom.Element)
     */
    public void configureFromClasspath(final Configurable singleConfig) {
        final Element rootObject = getRootObjectFromClassPath();
        pureConfigure(singleConfig, rootObject, getRootObjectFromClassPath());
    }

    /**
     * Configures the specified <code>Configurable</code>; Uses the Classpath to the local root Object.
     *
     * @param   url           path
     * @param   singleConfig  singleConfig specified <code>Configurable</code>; if ==null uses all <code>
     *                        Configurable</code> in <code>configurables</code>.
     *
     * @throws  Exception  Throws Excpetion if anything went wrong.
     *
     * @see     #pureConfigure(de.cismet.tools.configuration.Configurable, org.jdom.Element, org.jdom.Element)
     */
    public void configureFromClasspath(final String url, final Configurable singleConfig) throws Exception {
        final Element rootObject = getObjectFromClassPath(url);
        pureConfigure(singleConfig, rootObject, getRootObjectFromClassPath());
    }

    /**
     * Initialises the Local Configuration Classpath.
     */
    public void initialiseLocalConfigurationClasspath() {
        try {
            final SAXBuilder builder = new SAXBuilder(false);
            final Document doc = builder.build(getClass().getResourceAsStream(classPathFolder + defaultFileName));
            final Element configuration = doc.getRootElement().getChild("Configuration");                         // NOI18N
            setFolder(configuration.getChildText("LocalFolder"));                                                 // NOI18N
        } catch (final Exception ex) {
            LOG.error("error during initialisation of configuration manager using file: " + defaultFileName, ex); // NOI18N
        }
    }

    /**
     * Loads the server root object from classpath if it is still not set (if it is <code>null</code>). If every attempt
     * fails an IllegalStateException is thrown.
     *
     * @return  the root element of the server configuration, never null
     *
     * @throws  IllegalStateException  if the configuration cannot be loaded from any location
     */
    private Element getRootObjectFromClassPath() {
        if (serverRootObject == null) {
            if (LOG.isInfoEnabled()) {
                LOG.info("reading settings (InputStream from ClassPath)");                                           // NOI18N
            }
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("getRootObjectFromClassPath():classPathFolder+defaultFileName="
                                + classPathFolder                                                                    // NOI18N
                                + defaultFileName);
                }
                serverRootObject = getObjectFromClassPath(classPathFolder + defaultFileName);
            } catch (final Exception e) {
                LOG.warn(
                    "in getRootObjectFromClassPath: error reading settings (InputStream from ClassPath) "            // NOI18N
                            + "trying fallback filename: "                                                           // NOI18N
                            + classPathFolder
                            + fallBackFileName,
                    e);
                try {
                    serverRootObject = getObjectFromClassPath(classPathFolder + fallBackFileName);
                } catch (final Exception t) {
                    final String message =
                        "error reading settings (FallBackFilename), check your local folder for res.jar or similar"; // NOI18N
                    LOG.error(message, t);
                    throw new IllegalStateException(message, t);
                }
            }
        }

        assert serverRootObject != null : "illegal state, server root object still null"; // NOI18N

        return serverRootObject;
    }

    /**
     * Loads the object from specified ClassPath.
     *
     * @param   classPathUrl  Classpath as Url
     *
     * @return  <code>Object</code>
     *
     * @throws  JDOMException  Error while building with JDOM
     * @throws  IOException    <code>IOException</code>
     */
    private Element getObjectFromClassPath(final String classPathUrl) throws JDOMException, IOException {
        final SAXBuilder builder = new SAXBuilder(false);
        final Document doc = builder.build(getClass().getResourceAsStream(classPathUrl));

        return doc.getRootElement();
    }

    /**
     * Configures whether all Elements of the <code>configurables</code> if the given <code>Configurable</code> is null,
     * or the specified <code>Configurable</code>.
     *
     * @param  singleConfig      if ==null all <code>Configurable <code>in <code>configurables</code> will be used, if
     *                           !=null the specified <code>Configurable</code> will be used</code></code>
     * @param  rootObject        local root Object
     * @param  serverRootObject  server root Object
     */
    private void pureConfigure(final Configurable singleConfig,
            final Element rootObject,
            final Element serverRootObject) {
        final Element serverObject = preprocessElement(serverRootObject);
        if (singleConfig == null) {
            for (final Configurable elem : configurables) {
                try {
                    elem.masterConfigure(serverObject);
                } catch (final Exception serverT) {
                    LOG.warn("Error in elem.masterConfigure(serverRootObject)", serverT); // NOI18N
                }
                try {
                    elem.configure(rootObject);
                } catch (final Exception clientT) {
                    LOG.warn("Error in elem.configure(rootObject)", clientT);             // NOI18N
                }
            }
        } else {
            singleConfig.masterConfigure(serverObject);
            singleConfig.configure(rootObject);
        }
    }

    /**
     * Preprocesses the given Element. Resolves the Element with the <code>configAttrProvider</code>
     *
     * @param   e  Element
     *
     * @return  resolved Element
     *
     * @throws  IllegalStateException  Error while resolving.
     */
    private Element preprocessElement(final Element e) {
        if (e == null) {
            LOG.warn("cannot preprocess null element"); // NOI18N

            return null;
        }

        final ConfigAttrProvider attrProvider = Lookup.getDefault().lookup(ConfigAttrProvider.class);
        if (attrProvider == null) {
            if (LOG.isInfoEnabled()) {
                LOG.info("no ConfigAttrProvider found in lookup, skipping preprocessing"); // NOI18N
            }

            return e;
        }

        if (attrProvider instanceof ConnectionContextStore) {
            final ConnectionContext connectionContext = ConnectionContext.create(
                    AbstractConnectionContext.Category.OPTIONS,
                    getClass().getSimpleName());
            ((ConnectionContextStore)attrProvider).initWithConnectionContext(connectionContext);
        }

        if (LOG.isDebugEnabled()) {
            final XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
            LOG.debug("Element before resolve: " + out.outputString(e));
        }

        final Set<Element> resolved = resolveElement(e, new EntryResolver(attrProvider));

        if (resolved.size() != 1) {
            throw new IllegalStateException(
                "during resolve the given element was duplicated. This is illegal. Check your configuration"); // NOI18N
        }

        final Element resolvedRoot = resolved.iterator().next();

        if (LOG.isDebugEnabled()) {
            final XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
            LOG.debug("Element after resolve: " + out.outputString(resolvedRoot));
        }

        return resolvedRoot;
    }

    /**
     * Resolves the given <code>Element</code> with the given <code>AttrResolver</code>.
     *
     * @param   e         Element to be resolved
     * @param   resolver  the AttrResolver
     *
     * @return  resolved Element
     */
    private Set<Element> resolveElement(final Element e, final AttrResolver resolver) {
        Set<Element> toResolve = new LinkedHashSet<Element>();
        toResolve.add(e);

        if (resolver == null) {
            // end of processing
            return toResolve;
        }

        if (hasSubstitutionAttr(e)) {
            final Set<Element> resolved = new LinkedHashSet<Element>();
            try {
                // retrieve the substitution value ...
                final String value = resolver.getAttr();
                if (value != null) {
                    // ... try to create elements from the substitution value ...
                    final Set<Element> created = createElements(value);
                    if (created != null) {
                        // ... and set the substitution elements to be resolved
                        toResolve = created;
                    }
                }

                for (final Element element : toResolve) {
                    // resolve the elements to resolve against the attr resolver
                    resolved.addAll(resolveElement(element, getAttrResolver(resolver, getSubstitutionAttr(e))));
                }
            } catch (Exception resolvingException) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Error during resolving of Element (no surprise when not logged in)", resolvingException);
                }
            }
            return resolved;
        } else {
            for (int i = 0; i < e.getContentSize(); ++i) {
                final Content content = e.getContent(i);
                if (content instanceof Element) {
                    final Element child = (Element)content;
                    final Set<Element> newChildren = resolveElement(child, resolver);

                    if ((newChildren.size() == 1) && newChildren.iterator().next().equals(child)) {
                        // if the child does not change during resolve we remove the element if it is considered 'void'
                        removeVoid(child);
                    } else {
                        resolveNamespace(e, newChildren);
                        // we replace the child with the new children
                        e.setContent(i, newChildren);
                        // skip the already resolved elements
                        i += newChildren.size();
                    }
                }
            }
        }

        return toResolve;
    }

    /**
     * Iterates through all the given children and tries to resolve every children's DUMMY{@link Namespace} against the
     * given parent. The implementation traverses upwards beginning with the given parent {@link Element} and uses the
     * value of the first occurrence of the child's DUMMY{@link Namespace} prefix as a new {@link Namespace} for the
     * child.
     *
     * @param  parent       the {@link Element} used for resolving the {@link Namespace}s
     * @param  newChildren  the candidate {@link Element}s probably being resolved
     *
     * @see    ConfigurationManager#DUMMY_NS_ATTR_VALUE
     */
    public void resolveNamespace(final Element parent, final Set<Element> newChildren) {
        for (final Element child : newChildren) {
            if (DUMMY_NS_ATTR_VALUE.equals(child.getNamespaceURI())) {
                Element current = parent;
                boolean found = false;

                while ((current != null) && !found) {
                    final List addNamespaces = current.getAdditionalNamespaces();
                    final List<Namespace> namespaces = new ArrayList<Namespace>(addNamespaces.size() + 1);
                    namespaces.add(current.getNamespace());
                    namespaces.addAll(addNamespaces);

                    for (final Namespace ns : namespaces) {
                        if (ns.getPrefix().equals(child.getNamespacePrefix())) {
                            child.setNamespace(ns);
                            found = true;
                            break;
                        }
                    }

                    current = current.getParentElement();
                }

                if (!found) {
                    LOG.warn("could not resolve dummy namespace for attribute: " + child);
                }
            }
        }
    }

    /**
     * Checks whether the given {@link Element} has a substitution attribute.
     *
     * @param   e  the {@link Element} to be checked for a substitution attribute
     *
     * @return  true if the {@link Element} has a substitution attribute, false otherwise
     *
     * @see     ConfigurationManager#SUBSTITUTION_ATTR
     */
    public boolean hasSubstitutionAttr(final Element e) {
        final String key = getSubstitutionAttr(e);

        return (key != null) && !key.isEmpty();
    }

    /**
     * Returns the value of the substitution attribute of the given {@link Element}.
     *
     * @param   e  the {@link Element} containing the substitution attribute
     *
     * @return  the value of the substitution attribute of the given {@link Element}
     *
     * @see     ConfigurationManager#SUBSTITUTION_ATTR
     */
    public String getSubstitutionAttr(final Element e) {
        return e.getAttributeValue(SUBSTITUTION_ATTR);
    }

    /**
     * Creates a {@link Set} from the given xml snippet. This implementation assumes that there is a single root
     * {@link Element} whose only purpose is to wrap the {@link Element}s to be created. In other words this
     * implementation will return the child {@link Element}s of the {@link Document}'s root {@link Element}. The
     * returned {@link Element}s are detached from their root so they can be inserted into another {@link Document}.
     *
     * @param   xmlSnippet  the xml {@link String} the consisting of a single root {@link Element} and the desired child
     *                      {@link Element}s
     *
     * @return  the detached children of the root {@link Element} of the given xml snippet
     */
    public Set<Element> createElements(final String xmlSnippet) {
        final SAXBuilder builder = new SAXBuilder(false);
        final StringReader reader = new StringReader(xmlSnippet);
        try {
            final Element root = builder.build(reader).detachRootElement();
            final List children = root.getChildren();
            final Set<Element> elements = new LinkedHashSet<Element>(children.size());
            for (final Object o : children) {
                elements.add((Element)o);
            }

            // to be done in seperate loop to avoid concurrent modification
            for (final Element e : elements) {
                e.detach();
            }

            return elements;
        } catch (final Exception ex) {
            LOG.warn("cannot create elements from xml snipped: " + xmlSnippet, ex); // NOI18N
        } finally {
            reader.close();
        }

        return null;
    }

    /**
     * Removes an {@link Element} from its parent if the {@link Element} does not have any children or any attributes
     * except the substitution attribute.
     *
     * @param  child  the child that is a candidate for removal
     */
    public void removeVoid(final Element child) {
        final List attributes = child.getAttributes();
        if (attributes.size() == 1) {
            final Attribute attribute = (Attribute)attributes.get(0);
            if (SUBSTITUTION_ATTR.equals(attribute.getName()) && child.getChildren().isEmpty()) {
                child.getParent().removeContent(child);
            }
        }
    }

    /**
     * Returns a new {@link AttrResolver} based on the given {@link AttrResolver} and the given key. The creation of the
     * {@link AttrResolver}s is done in a specific way:
     *
     * <ul>
     *   <li>{@link EntryResolver}, <code>key</code> --&gt; {@link UserAttrResolver} using <code>key</code></li>
     *   <li>{@link UserAttrResolver}, <code>key</code> --&gt; {@link GroupAttrResolver} using <code>
     *     resolver.key</code></li>
     *   <li>{@link GroupAttrResolver}, <code>key</code> --&gt; {@link DomainAttrResolver} using <code>
     *     resolver.key</code></li>
     *   <li>{@link DomainAttrResolver}, <code>key</code> --&gt; <code>null</code></li>
     * </ul>
     *
     * <p>Any other {@link AttrResolver} implementation will result in a return value of <code>null</code>.</p>
     *
     * @param   resolver  resolver that is basis for the decision which {@link AttrResolver} to return
     * @param   key       the key the {@link AttrResolver} shall return the value for
     *
     * @return  a new {@link AttrResolver}
     */
    private AttrResolver getAttrResolver(final AttrResolver resolver, final String key) {
        if (resolver instanceof DefaultAttrResolver) {
            final DefaultAttrResolver defaultResolver = (DefaultAttrResolver)resolver;
            if (defaultResolver instanceof KeyAttrResolver) {
                final KeyAttrResolver keyResolver = (KeyAttrResolver)defaultResolver;
                if (keyResolver instanceof UserAttrResolver) {
                    return new GroupAttrResolver(keyResolver.key, keyResolver.provider);
                } else if (keyResolver instanceof GroupAttrResolver) {
                    return new DomainAttrResolver(keyResolver.key, keyResolver.provider);
                }
            } else if (defaultResolver instanceof EntryResolver) {
                return new UserAttrResolver(key, defaultResolver.provider);
            }
        }

        return null;
    }

    /**
     * Writes a new Configuration File into the LocalAbsouluteConfigurationFolder.
     *
     * @see  #getLocalAbsoluteConfigurationFolder()
     * @see  #writeConfiguration(java.lang.String)
     */
    public void writeConfiguration() {
        new File(getLocalAbsoluteConfigurationFolder()).mkdirs();
        writeConfiguration(getLocalAbsoluteConfigurationFolder() + fileName);
    }

    /**
     * Returns the Url "~/.cismet/"
     *
     * @return  ~/.cismet/
     */
    public String getLocalAbsoluteConfigurationFolder() {
        return home + fs + folder + fs;
    }

    /**
     * Writes a Configuration File in specified path.
     *
     * @param  path  path
     */
    public void writeConfiguration(final String path) {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("try to write configuration of this configurables:" + configurables); // NOI18N
            }
            final Element root = new Element("cismetConfigurationManager");                     // NOI18N

            for (final Configurable elem : configurables) {
                try {
                    final Element e = elem.getConfiguration();

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Writing Element: " + e);         // NOI18N
                    }
                    if (e != null) {
                        root.addContent(e);
                    }
                } catch (final Exception t) {
                    LOG.warn("error while writing config part", t); // NOI18N
                }
            }
            final Document doc = new Document(root);
            final Format format = Format.getPrettyFormat();
            format.setEncoding(XML_ENCODING);                       // NOI18N
            // TODO: why not using UTF-8
            final XMLOutputter serializer = new XMLOutputter(format);
            final File file = new File(path);
            final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), XML_ENCODING);
            serializer.output(doc, writer);
            writer.flush();
        } catch (final Exception tt) {
            LOG.error("Error while writing configuration.", tt); // NOI18N
        }
    }

    /**
     * Getter for {@link #defaultFileName}.
     *
     * @return  <code>defaultFileName</code>
     */
    public String getDefaultFileName() {
        return defaultFileName;
    }

    /**
     * Setter for {@link #defaultFileName}.
     *
     * @param  defaultFileName  <code>defaultFileName</code>
     */
    public void setDefaultFileName(final String defaultFileName) {
        this.defaultFileName = defaultFileName;
    }

    /**
     * Getter for {@link #classPathFolder}.
     *
     * @return  <code>classPathFolder</code>
     */
    public String getClassPathFolder() {
        return classPathFolder;
    }

    /**
     * Setter for {@link #classPathFolder}.
     *
     * @param  classPathFolder  <code>classPathFolder</code>
     */
    public void setClassPathFolder(final String classPathFolder) {
        this.classPathFolder = classPathFolder;
    }

    /**
     * Getter for {@link #home}.
     *
     * @return  <code>home</code>
     */
    public String getHome() {
        return home;
    }

    /**
     * Setter for {@link #home}.
     *
     * @param  home  <code>home</code>
     */
    public void setHome(final String home) {
        this.home = home;
    }

    /**
     * Getter for {@link #fs}.
     *
     * @return  file separator
     */
    public String getFileSeperator() {
        return fs;
    }

    /**
     * Setter for {@link #fs}.
     *
     * @param  fs  file seperator
     */
    public void setFileSeperator(final String fs) {
        this.fs = fs;
    }

    /**
     * Getter for {@link #fallBackFileName}.
     *
     * @return  fallBackFileName
     */
    public String getFallBackFileName() {
        return fallBackFileName;
    }

    /**
     * Setter for {@link #fallBackFileName}.
     *
     * @param  fallBackFileName  <code>fallBackFileName</code>
     */
    public void setFallBackFileName(final String fallBackFileName) {
        this.fallBackFileName = fallBackFileName;
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * AttrResolver Interface.
     *
     * @version  $Revision$, $Date$
     */
    private static interface AttrResolver {

        //~ Methods ------------------------------------------------------------

        /**
         * Getter for Attribute.
         *
         * @return  Attribute
         */
        String getAttr();
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * AttrResolver Implementation.
     *
     * @version  $Revision$, $Date$
     */
    private abstract static class DefaultAttrResolver implements AttrResolver {

        //~ Instance fields ----------------------------------------------------

        protected final transient ConfigAttrProvider provider;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DefaultAttrResolver object.
         *
         * @param  provider  ConfigAttrProvider
         */
        public DefaultAttrResolver(final ConfigAttrProvider provider) {
            this.provider = provider;
        }
    }

    /**
     * Extended <code>DefaultAttrResolver</code> for Keys.
     *
     * @version  $Revision$, $Date$
     */
    private abstract static class KeyAttrResolver extends DefaultAttrResolver {

        //~ Instance fields ----------------------------------------------------

        protected final transient String key;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new KeyAttrResolver object.
         *
         * @param  key       the given key
         * @param  provider  the given ConfigAttrProvider
         */
        public KeyAttrResolver(final String key, final ConfigAttrProvider provider) {
            super(provider);
            this.key = key;
        }
    }

    /**
     * Extended <code>DefaultAttrResolver</code> for Entrys.
     *
     * @version  $Revision$, $Date$
     */
    private static final class EntryResolver extends DefaultAttrResolver {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new EntryResolver object.
         *
         * @param  provider  given ConfigAttrProvider
         */
        public EntryResolver(final ConfigAttrProvider provider) {
            super(provider);
        }

        //~ Methods ------------------------------------------------------------

        /**
         * No Attributes.
         *
         * @return  <code>null</code>
         */
        @Override
        public String getAttr() {
            return null;
        }
    }

    /**
     * Extended KeyAttrResolver for Users.
     *
     * @version  $Revision$, $Date$
     */
    private static final class UserAttrResolver extends KeyAttrResolver {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new UserAttrResolver object.
         *
         * @param  key       given key
         * @param  provider  given ConfigAttrProvider
         */
        public UserAttrResolver(final String key, final ConfigAttrProvider provider) {
            super(key, provider);
        }

        //~ Methods ------------------------------------------------------------

        /**
         * Getter for UserConfigAttr.
         *
         * @return  userConfigAttr
         */
        @Override
        public String getAttr() {
            return provider.getUserConfigAttr(key);
        }
    }

    /**
     * Extended KeyAttrResolver for Groups.
     *
     * @version  $Revision$, $Date$
     */
    private static final class GroupAttrResolver extends KeyAttrResolver {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new GroupAttrResolver object.
         *
         * @param  key       given key
         * @param  provider  given ConfigAttrProvider
         */
        public GroupAttrResolver(final String key, final ConfigAttrProvider provider) {
            super(key, provider);
        }

        //~ Methods ------------------------------------------------------------

        /**
         * Getter for GroupConfigAttr.
         *
         * @return  GroupConfigAttr
         */
        @Override
        public String getAttr() {
            return provider.getGroupConfigAttr(key);
        }
    }

    /**
     * Extended KeyAttrResolver for Domains.
     *
     * @version  $Revision$, $Date$
     */
    private static final class DomainAttrResolver extends KeyAttrResolver {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DomainAttrResolver object.
         *
         * @param  key       given key
         * @param  provider  given ConfigAttrProvider
         */
        public DomainAttrResolver(final String key, final ConfigAttrProvider provider) {
            super(key, provider);
        }

        //~ Methods ------------------------------------------------------------

        /**
         * Getter for DomainConfigAttr.
         *
         * @return  DomainConfigAttr
         */
        @Override
        public String getAttr() {
            return provider.getDomainConfigAttr(key);
        }
    }
}
