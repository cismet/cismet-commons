/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.commons.utils.datasource;

import org.apache.log4j.Logger;

import org.jdom.Element;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import de.cismet.commons.capabilities.SimpleCapabilitiesCache;

import de.cismet.commons.wms.capabilities.Layer;
import de.cismet.commons.wms.capabilities.WMSCapabilities;
import de.cismet.commons.wms.capabilities.WMSCapabilitiesFactory;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class DatasourcesUtils {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(DatasourcesUtils.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * create a list with all data sources.
     *
     * @param  serverElement             DOCUMENT ME!
     * @param  clientElement             DOCUMENT ME!
     * @param  basicAuthorizationTokens  parent DOCUMENT ME!
     * @param  converter                 DOCUMENT ME!
     * @param  file                      DOCUMENT ME!
     */
    public static void createLayerListHeadless(final Element serverElement,
            final Element clientElement,
            final String[] basicAuthorizationTokens,
            final DatasourcesPojoConverter converter,
            final File file) {
        final CapabilitiesListTreeNode node = createCapabilitiesListTreeNode(
                null,
                serverElement.getChild("cismapCapabilitiesPreferences"));

        if (file != null) {
            BufferedWriter bw = null;

            try {
                final DatasourcesPojo ds = createDataSources(node, basicAuthorizationTokens);
                bw = new BufferedWriter(new FileWriter(file));
                bw.write(converter.convertDatasource(ds));
            } catch (Exception e) {
                LOG.error(e);
            } finally {
                if (bw != null) {
                    try {
                        bw.close();
                    } catch (IOException ex) {
                        LOG.error("Cannot close file " + file.getAbsolutePath(), ex);
                    }
                }
            }
        }
    }

    /**
     * Erzeugt rekursiv aus einem JDom-Element einen CapabilitiesList-Knoten samt CapabilitiesList und Unterknoten.
     *
     * @param   nodetitle  Title des CapabilitiesList-Knotens
     * @param   element    JDom-Element
     *
     * @return  CapabilitiesList-Knoten
     */
    private static CapabilitiesListTreeNode createCapabilitiesListTreeNode(final String nodetitle,
            final Element element) {
        final CapabilitiesListTreeNode node = new CapabilitiesListTreeNode();
        int listCounter = 0;

        node.setTitle(nodetitle);

        final TreeMap<Integer, CapabilityLink> capabilitiesList = new TreeMap<Integer, CapabilityLink>();
        for (final Element elem : (List<Element>)element.getChildren("capabilitiesList")) { // NOI18N
            try {
                final String type = elem.getAttribute("type").getValue();                   // NOI18N
                final String title = elem.getAttribute("titlestring").getValue();           // NOI18N

                if (type.equals("MENU")) {
                    // Unterknoten erzeugen
                    node.addSubnode(createCapabilitiesListTreeNode(title, elem));
                } else {
                    // CapabilitiesList-Eintrag erzeugen
                    final String link = elem.getTextTrim();
                    final String subparent = elem.getAttributeValue("subparent");  // NOI18N
                    capabilitiesList.put(new Integer(listCounter++),
                        new CapabilityLink(type, link, title, subparent));
                }
            } catch (Throwable t) {
                LOG.warn("Error while reading the CapabilityListPreferences.", t); // NOI18N
            }
        }

        // CapabilitiesList
        node.setCapabilitiesList(capabilitiesList);

        // fertig
        return node;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   node                      DOCUMENT ME!
     * @param   basicAuthorizationTokens  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static DatasourcesPojo createDataSources(final CapabilitiesListTreeNode node,
            final String[] basicAuthorizationTokens) {
        final DatasourcesPojo datasource = new DatasourcesPojo();

        datasource.addService(createServicePojo(node, basicAuthorizationTokens));

        return datasource;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   node                      DOCUMENT ME!
     * @param   basicAuthorizationTokens  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static ServicePojo createServicePojo(final CapabilitiesListTreeNode node,
            final String[] basicAuthorizationTokens) {
        final ServicePojo service = new ServicePojo();
        service.setName(node.getTitle());

        for (final CapabilitiesListTreeNode subnode : node.getSubnodes()) {
            service.addService(createServicePojo(subnode, basicAuthorizationTokens));
        }

        for (final Integer key : node.getCapabilitiesList().keySet()) {
            final CapabilityLink link = node.getCapabilitiesList().get(key);

            if (!link.getType().equals("SEPARATOR") || !((link.getLink() != null) && link.getLink().equals(""))) {
                final ServicePojo servicePojo = new ServicePojo();
                servicePojo.setName(link.getTitle());
                servicePojo.setUrl(link.getLink());
                servicePojo.setType(link.getType());
                servicePojo.setSubparent(link.getSubparent());
                addLayers(servicePojo, basicAuthorizationTokens);
                service.addService(servicePojo);
            }
        }

        return service;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  service                   DOCUMENT ME!
     * @param  basicAuthorizationTokens  DOCUMENT ME!
     */
    private static void addLayers(final ServicePojo service,
            final String[] basicAuthorizationTokens) {
        if (service.getType().equalsIgnoreCase("OGC-WMS") || service.getUrl().toLowerCase().contains("service=wms")) {
            try {
                final WMSCapabilitiesFactory capFact = new WMSCapabilitiesFactory(SimpleCapabilitiesCache.getInstance(
                            basicAuthorizationTokens));
                final WMSCapabilities cap = capFact.createCapabilities(service.getUrl());
                Layer l = cap.getLayer();
                service.setAbstractText(cap.getService().getAbstract());

                if (service.getSubparent() != null) {
                    final Layer subLayer = getLayerByTitle(l, service.getSubparent());

                    if (subLayer != null) {
                        l = subLayer;
                    }
                }

                for (final Layer layer : l.getChildren()) {
                    service.addLayer(createSubLayers(layer));
                }
            } catch (Exception e) {
                service.setAccessError(e.getMessage());
                LOG.warn("Error while reading capabilities", e);
            }
        } else {
            System.out.println("type: " + service.getType());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   layer  DOCUMENT ME!
     * @param   title  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static Layer getLayerByTitle(final Layer layer, final String title) {
        if ((layer.getTitle() != null) && layer.getTitle().equals(title)) {
            return layer;
        } else {
            final Layer[] larr = layer.getChildren();
            for (final Layer l : larr) {
                final Layer test = getLayerByTitle(l, title);
                if (test != null) {
                    return test;
                }
            }
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   l  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static LayerPojo createSubLayers(final Layer l) {
        final LayerPojo layer = new LayerPojo();
        layer.setName(l.getName());
        layer.setTitle(l.getTitle());
        layer.setKeywords(l.getKeywords());
        layer.setAbstractText(l.getAbstract());

        for (final Layer tmpLayer : l.getChildren()) {
            layer.addLayer(createSubLayers(tmpLayer));
        }

        return layer;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static class CapabilitiesListTreeNode {

        //~ Instance fields ----------------------------------------------------

        private TreeMap<Integer, CapabilityLink> capabilitiesList = new TreeMap<Integer, CapabilityLink>();
        private LinkedList<CapabilitiesListTreeNode> subnodes = new LinkedList<CapabilitiesListTreeNode>();
        private String title = null;

        //~ Constructors -------------------------------------------------------

        /**
         * Erzeugt einen CapabilitiesList-Knoten.
         */
        public CapabilitiesListTreeNode() {
        }

        //~ Methods ------------------------------------------------------------

        /**
         * Gibt die CapabilitiesList des Knotens zurück.
         *
         * @return  CapabilitiesList des Knotens
         */
        public TreeMap<Integer, CapabilityLink> getCapabilitiesList() {
            return capabilitiesList;
        }

        /**
         * Setzt die CapabilitiesList des Knotens.
         *
         * @param  capabilitiesList  DOCUMENT ME!
         */
        public void setCapabilitiesList(final TreeMap<Integer, CapabilityLink> capabilitiesList) {
            this.capabilitiesList = capabilitiesList;
        }

        /**
         * Fügt dem Knoten einen Unterknoten hinzu.
         *
         * @param  subnode  Unterknoten
         */
        public void addSubnode(final CapabilitiesListTreeNode subnode) {
            subnodes.add(subnode);
        }

        /**
         * Gibt die Liste der Unterknoten zurück.
         *
         * @return  Liste der Unterknoten
         */
        public List<CapabilitiesListTreeNode> getSubnodes() {
            return (List<CapabilitiesListTreeNode>)subnodes.clone();
        }

        /**
         * Gibt den Titel des CapabilitiesList-Knotens zurück.
         *
         * @return  Titel des CapabilitiesList-Knotens
         */
        public String getTitle() {
            return title;
        }

        /**
         * Setzt den Titel des CapabilitiesList-Knotens.
         *
         * @param  title  Title des Knotens
         */
        public void setTitle(final String title) {
            this.title = title;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static class CapabilityLink {

        //~ Instance fields ----------------------------------------------------

        private String type;
        private String subparent;
        private String link;
        private String title;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new CapabilityLink object.
         *
         * @param  type       DOCUMENT ME!
         * @param  link       DOCUMENT ME!
         * @param  title      DOCUMENT ME!
         * @param  subparent  DOCUMENT ME!
         */
        public CapabilityLink(final String type, final String link, final String title, final String subparent) {
            this.type = type;
            this.subparent = subparent;
            this.link = link;
            this.title = title;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  the type
         */
        public String getType() {
            return type;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  type  the type to set
         */
        public void setType(final String type) {
            this.type = type;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  the subparent
         */
        public String getSubparent() {
            return subparent;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  subparent  the subparent to set
         */
        public void setSubparent(final String subparent) {
            this.subparent = subparent;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  the link
         */
        public String getLink() {
            return link;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  link  the link to set
         */
        public void setLink(final String link) {
            this.link = link;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  the title
         */
        public String getTitle() {
            return title;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  title  the title to set
         */
        public void setTitle(final String title) {
            this.title = title;
        }
    }
}
