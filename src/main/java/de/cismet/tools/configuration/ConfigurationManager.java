/*
 * ConfigurationManager.java
 * Copyright (C) 2005 by:
 *
 *----------------------------
 * cismet GmbH
 * Goebenstrasse 40
 * 66117 Saarbruecken
 * http://www.cismet.de
 *----------------------------
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *----------------------------
 * Author:
 * thorsten.hell@cismet.de
 *----------------------------
 *
 * Created on 3. M\u00E4rz 2006, 14:14
 *
 */
package de.cismet.tools.configuration;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Vector;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author thorsten.hell@cismet.de
 */
public class ConfigurationManager {

    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    Vector<Configurable> configurables = new Vector<Configurable>();
    private String fileName = "configuration.xml";
    private String fallBackFileName = "configuration.xml";
    private String defaultFileName = fallBackFileName;
    private String classPathFolder = "/";
    private String folder = ".cismet";
    private String home;
    private String fs;
    private Element rootObject = null;
    private Element serverRootObject = null;

    /** Creates a new instance of ConfigurationManager */
    public ConfigurationManager() {
        log.debug("Create ConfigurationManager.");
        home = System.getProperty("user.home");
        fs = System.getProperty("file.separator");
    }

    public void addConfigurable(Configurable configurable) {
        configurables.add(configurable);
    }

    public void removeConfigurable(Configurable configurable) {
        configurables.remove(configurable);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void configure() {
        configure((Configurable) null);
    }

    public void configure(String path) {
        configure(null, path);
    }

    public void configure(Configurable singleConfig) {
        configure(singleConfig, home + fs + folder + fs + fileName);
    }        

    public void configure(Configurable singleConfig, String path) {
        try {
            SAXBuilder builder = new SAXBuilder(false);
            Document doc = builder.build(new File(path));
            rootObject = null;
            rootObject = doc.getRootElement();
        } catch (Throwable e) {
            log.warn("Fehler beim Lesen der Einstellungen (User.Home) (" + singleConfig + ") wenn null dann alle", e);
            System.out.println("Fehler beim Lesen der Einstellungen (User.Home)");
            e.printStackTrace();
            log.info("rootObject:" + rootObject);
        }
        if (rootObject == null) {
            //Keins da. Deshalb das vordefinierte laden
            rootObject = getRootObjectFromClassPath();

        }


        serverRootObject = getRootObjectFromClassPath();

        if (rootObject == null) {
            log.fatal("Fehler beim Konfigurationsmanagement. Von einem fehlerfreien Start kann nicht ausgegangen werden.");
        }

        XMLOutputter serializer = new XMLOutputter();
        serializer.setEncoding("ISO-8859-1");
        log.debug("ENCODING:" + serializer.toString());
        serializer.setIndent("\t");


        log.info("ConfigurationDocument: " + serializer.outputString(rootObject.getDocument()));
        pureConfigure(singleConfig);
    }

    public void configureFromClasspath() {
        configureFromClasspath(null);
    }

    public void configureFromClasspath(Configurable singleConfig) {
        rootObject = getRootObjectFromClassPath();
        pureConfigure(singleConfig);
    }

    //document
    public void initialiseLocalConfigurationClasspath() {
        try {
            SAXBuilder builder = new SAXBuilder(false);
            Document doc = builder.build(getClass().getResourceAsStream(classPathFolder + defaultFileName));
            Element configuration = doc.getRootElement().getChild("Configuration");
            setFolder(configuration.getChildText("LocalFolder"));            
        } catch (Exception ex) {
            log.error("Fehler beim initialisieren des Configuration Managers mit File",ex);
        }
    }

    private Element getRootObjectFromClassPath() {
        log.info("Lesen der Einstellungen (InputStream vom ClassPath)");
        SAXBuilder builder = new SAXBuilder(false);
        try {
            log.debug("getRootObjectFromClassPath():classPathFolder+defaultFileName=" + classPathFolder + defaultFileName);
            Document doc = builder.build(getClass().getResourceAsStream(classPathFolder + defaultFileName));
            return doc.getRootElement();
        } catch (Throwable e) {
            log.warn("in getRootObjectFromClassPath: Fehler beim Lesen der Einstellungen (InputStream vom ClassPath) probiere es jetzt mit dem FallbackFilename: " + classPathFolder + fallBackFileName, e);
            try {
                InputStream is = getClass().getResourceAsStream(classPathFolder + fallBackFileName);
                Document doc = builder.build(is);
                return doc.getRootElement();
            } catch (Throwable t) {
                log.error("Fehler beim Lesen der Einstellungen (FallBackFilename)", t);
            }
// Wenn in Netbeansumgebung eingesetzt            
//            log.warn("in getRootObjectFromClassPath: Fehler beim Lesen der Einstellungen (InputStream vom ClassPath)", e);
//            try {
//                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(classPathFolder + defaultFileName);
//                Document doc = builder.build(is);
//                return doc.getRootElement();
//            } catch (Throwable t) {
//                log.error("Fehler beim Lesen der Einstellungen (InputStream vom ClassPath mit NB Classloader", t);
//            }
            return null;
        }
    }

    private void pureConfigure(Configurable singleConfig) {
        if (singleConfig == null) {
            for (Configurable elem : configurables) {
                try {
                    elem.masterConfigure(serverRootObject);
                } catch (Throwable serverT) {
                    log.warn("Fehler bei elem.masterConfigure(serverRootObject)", serverT);
                }
                try {
                    elem.configure(rootObject);
                } catch (Throwable clientT) {
                    log.warn("Fehler bei elem.configure(rootObject)", clientT);
                }
            }
        } else {
            singleConfig.masterConfigure(serverRootObject);
            singleConfig.configure(rootObject);
        }
    }

    public void writeConfiguration() {
        new File(home + fs + folder).mkdirs();
        writeConfiguration(home + fs + folder + fs + fileName);
    }
    
    public String getLocalAbsoluteConfigurationFolder(){
        return home + fs + folder + fs;
    }

    public void writeConfiguration(String path) {
        try {
            log.debug("try to write configuration of this configurables:" + configurables);
            Element root = new Element("cismetConfigurationManager");

            for (Configurable elem : configurables) {
                try {
                    Element e = elem.getConfiguration();

                    log.debug("Schreibe Element: " + e);
                    if (e != null) {
                        root.addContent(e);
                    }
                } catch (Exception t) {
                    log.warn("Fehler beim Schreiben der eines Konfigurationsteils.", t);
                }
            }
            Document doc = new Document(root);
            XMLOutputter serializer = new XMLOutputter("\t", true, "ISO-8859-1");
            log.debug("ENCODING:" + serializer.toString());
            serializer.setTrimAllWhite(true);
            File file = new File(path);
            FileWriter writer = new FileWriter(file);
            serializer.output(doc, writer);
            writer.flush();
        } catch (Throwable tt) {
            log.error("Fehler beim Schreiben der Konfiguration.", tt);
        }
    }

    public String getDefaultFileName() {
        return defaultFileName;
    }

    public void setDefaultFileName(String defaultFileName) {
        this.defaultFileName = defaultFileName;
    }

    public String getClassPathFolder() {
        return classPathFolder;
    }

    public void setClassPathFolder(String classPathFolder) {
        this.classPathFolder = classPathFolder;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getFileSeperator() {
        return fs;
    }

    public void setFileSeperator(String fs) {
        this.fs = fs;
    }

    public String getFallBackFileName() {
        return fallBackFileName;
    }

    public void setFallBackFileName(String fallBackFileName) {
        this.fallBackFileName = fallBackFileName;
    }
}
