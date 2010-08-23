/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.tools;

import java.awt.Color;
import java.awt.Font;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author thorsten
 * 
 */
public class StaticXMLTools {
        private final static Logger log = org.apache.log4j.Logger.getLogger(StaticXMLTools.class);
    /**
     * Konvertiert eine Farbe zu folgendem XML-Code
     * <Color red=X green=Y blue=Z alpha=A>
     * @param c die in XML darzustellende Farbe
     * @return JDOM-Element
     */
    public static Element convertColorToXML(Color c) {
        Element colorElement = new Element("Color");  // NOI18N
        colorElement.setAttribute("red", new Integer(c.getRed()).toString());  // NOI18N
        colorElement.setAttribute("green", new Integer(c.getGreen()).toString());  // NOI18N
        colorElement.setAttribute("blue", new Integer(c.getBlue()).toString());  // NOI18N
        colorElement.setAttribute("alpha", new Integer(c.getAlpha()).toString());  // NOI18N
        return colorElement;
    }

    public static Color convertXMLElementToColor(Element xmlElement) {
        int red = 0;
        int green = 0;
        int blue = 0;
        int alpha = 255;

        try {
            red = new Integer(xmlElement.getAttributeValue("red")).intValue();  // NOI18N
        } catch (Exception skip) {}
        try {
            green = new Integer(xmlElement.getAttributeValue("green")).intValue();  // NOI18N
        } catch (Exception skip) {}
        try {
            blue = new Integer(xmlElement.getAttributeValue("blue")).intValue();  // NOI18N
        } catch (Exception skip) {}
        try {
            alpha = new Integer(xmlElement.getAttributeValue("alpha")).intValue();  // NOI18N
        } catch (Exception skip) {}

        Color c = new Color(red, green, blue, alpha);
        return c;
    }

    /**
     * Konvertiert eine Schrift zu folgendem XML-Code
     * <Font name=X style="0" size="12">
     * @param f die zu konvertierende Schrift
     * @return JDOM-Element
     */
    public static Element convertFontToXML(Font f) {
        Element fontElement = new Element("Font");  // NOI18N
        fontElement.setAttribute("name", f.getFamily());  // NOI18N
        fontElement.setAttribute("style", new Integer(f.getStyle()).toString());  // NOI18N
        fontElement.setAttribute("size", new Integer(f.getSize()).toString());  // NOI18N
        return fontElement;
    }

    public static Font convertXMLElementToFont(Element xmlElement) {
        String name = "sansserif";  // NOI18N
        int style = Font.PLAIN;
        int size = 12;

        try {
            name = xmlElement.getAttributeValue("name");  // NOI18N
        } catch (Exception skip) {}
        try {
            style = new Integer(xmlElement.getAttributeValue("style")).intValue();  // NOI18N
        } catch (Exception skip) {}
        try {
            size = new Integer(xmlElement.getAttributeValue("size")).intValue();  // NOI18N
        } catch (Exception skip) {}
        
        return new Font(name, style, size);
    }
    
    
    public static void logXML( org.jdom.Element element) {
        org.jdom.Document doc = new org.jdom.Document();
        //is this the right way
        doc.setRootElement(( org.jdom.Element) element.clone());
        XMLOutputter out = new XMLOutputter();
        String postString = out.outputString(doc);
        if(log.isDebugEnabled())
            log.debug("logXML :" + postString, new CurrentStackTrace());  // NOI18N
    }             
    
}
