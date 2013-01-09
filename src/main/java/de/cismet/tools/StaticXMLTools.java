/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools;

import org.apache.log4j.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import java.awt.Color;
import java.awt.Font;

/**
 * Converter for Color to XML and XML to Color.
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class StaticXMLTools {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(StaticXMLTools.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new StaticXMLTools object.
     */
    private StaticXMLTools() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Converts a <code>Color</code> into XML-Code <Color red=X green=Y blue=Z alpha=A>.
     *
     * @param   c  <code>Color</code>, which is going to be converted
     *
     * @return  JDOM-Element
     */
    public static Element convertColorToXML(final Color c) {
        final Element colorElement = new Element("Color");                        // NOI18N
        colorElement.setAttribute("red", new Integer(c.getRed()).toString());     // NOI18N
        colorElement.setAttribute("green", new Integer(c.getGreen()).toString()); // NOI18N
        colorElement.setAttribute("blue", new Integer(c.getBlue()).toString());   // NOI18N
        colorElement.setAttribute("alpha", new Integer(c.getAlpha()).toString()); // NOI18N
        return colorElement;
    }

    /**
     * Converts the specified <code>XMLElement</code> into a <code>Color.</code>
     *
     * @param   xmlElement  <code>xmlElemt</code>, which is going to be converted
     *
     * @return  <code>Color</code>
     */
    public static Color convertXMLElementToColor(final Element xmlElement) {
        int red = 0;
        int green = 0;
        int blue = 0;
        int alpha = 255;

        try {
            red = new Integer(xmlElement.getAttributeValue("red")).intValue();     // NOI18N
        } catch (Exception skip) {
        }
        try {
            green = new Integer(xmlElement.getAttributeValue("green")).intValue(); // NOI18N
        } catch (Exception skip) {
        }
        try {
            blue = new Integer(xmlElement.getAttributeValue("blue")).intValue();   // NOI18N
        } catch (Exception skip) {
        }
        try {
            alpha = new Integer(xmlElement.getAttributeValue("alpha")).intValue(); // NOI18N
        } catch (Exception skip) {
        }

        final Color c = new Color(red, green, blue, alpha);
        return c;
    }

    /**
     * Converts the <code>Font</code> into <code>XML-Code.</code>
     *
     * @param   f  <code>Font</code>, which is going to be converted
     *
     * @return  JDOM-Element
     */
    public static Element convertFontToXML(final Font f) {
        final Element fontElement = new Element("Font");                         // NOI18N
        fontElement.setAttribute("name", f.getFamily());                         // NOI18N
        fontElement.setAttribute("style", new Integer(f.getStyle()).toString()); // NOI18N
        fontElement.setAttribute("size", new Integer(f.getSize()).toString());   // NOI18N
        return fontElement;
    }

    /**
     * Converts <code>XMLElement</code> into <code>Font.</code>
     *
     * @param   xmlElement  <code>xmlElement</code>, which is going to be converted
     *
     * @return  <code>Font</code>
     */
    public static Font convertXMLElementToFont(final Element xmlElement) {
        String name = "sansserif"; // NOI18N
        int style = Font.PLAIN;
        int size = 12;

        try {
            name = xmlElement.getAttributeValue("name");                           // NOI18N
        } catch (Exception skip) {
        }
        try {
            style = new Integer(xmlElement.getAttributeValue("style")).intValue(); // NOI18N
        } catch (Exception skip) {
        }
        try {
            size = new Integer(xmlElement.getAttributeValue("size")).intValue();   // NOI18N
        } catch (Exception skip) {
        }

        return new Font(name, style, size);
    }

    /**
     * Posts the specified XML on the Logger.
     *
     * @param  element  XML, which is going to be posted
     */
    public static void logXML(final Element element) {
        final Document doc = new Document();
        // is this the right way
        doc.setRootElement((Element)element.clone());
        final XMLOutputter out = new XMLOutputter();
        final String postString = out.outputString(doc);
        if (LOG.isDebugEnabled()) {
            LOG.debug("logXML :" + postString, new CurrentStackTrace()); // NOI18N
        }
    }
}
