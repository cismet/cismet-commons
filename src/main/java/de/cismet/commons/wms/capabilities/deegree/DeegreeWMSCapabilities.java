/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 *  Copyright (C) 2010 therter
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cismet.commons.wms.capabilities.deegree;

import org.apache.log4j.Logger;

import org.deegree.framework.xml.XMLFragment;
import org.deegree.ogcwebservices.getcapabilities.InvalidCapabilitiesException;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilitiesDocument;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilitiesDocument_1_3_0;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.List;

import de.cismet.commons.capabilities.Service;

import de.cismet.commons.wms.capabilities.Layer;
import de.cismet.commons.wms.capabilities.Request;
import de.cismet.commons.wms.capabilities.WMSCapabilities;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class DeegreeWMSCapabilities implements WMSCapabilities {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger logger = Logger.getLogger(DeegreeWMSCapabilities.class);

    //~ Instance fields --------------------------------------------------------

    private final org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities cap;
    private final URL url;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DeegreeWMSCapabilities object.
     *
     * @param   in       DOCUMENT ME!
     * @param   nameID   DOCUMENT ME!
     * @param   version  DOCUMENT ME!
     *
     * @throws  InvalidCapabilitiesException  DOCUMENT ME!
     * @throws  IOException                   DOCUMENT ME!
     * @throws  SAXException                  DOCUMENT ME!
     */
    public DeegreeWMSCapabilities(final InputStream in, final String nameID, final String version)
            throws InvalidCapabilitiesException, IOException, SAXException {
        String urlString = nameID;
        WMSCapabilitiesDocument parser;

        if (((version != null) && ((version.equals("1.3.0")) || version.equals("1.3")))) {
            parser = new WMSCapabilitiesDocument_1_3_0();
        } else {
            parser = new WMSCapabilitiesDocument();
        }

        if (urlString.indexOf("?") != -1) {
            urlString = urlString.substring(0, urlString.indexOf("?"));
        }
        this.url = new URL(urlString);
        parser.load(in, XMLFragment.DEFAULT_URL);
        cap = (org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities)parser.parseCapabilities();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Service getService() {
        return new DeegreeService(cap.getServiceProvider(), cap.getServiceIdentification());
    }

    @Override
    public Request getRequest() {
        return new DeegreeRequest(cap.getOperationMetadata());
    }

    @Override
    public Layer getLayer() {
        return new DeegreeLayer(cap.getLayer(), this);
    }

    @Override
    public String getVersion() {
        return cap.getVersion();
    }

    @Override
    public URL getURL() {
        return url;
    }

    @Override
    public List<String> getExceptions() {
        return cap.getExceptions();
    }
}
