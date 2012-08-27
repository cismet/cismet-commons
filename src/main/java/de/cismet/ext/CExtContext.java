/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.ext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class CExtContext {   
    private HashMap<String,Object> propertyBag=new HashMap<String, Object>();

    public CExtContext(String key, Object value) {
         propertyBag.put(key, value);
    }

    public CExtContext addContextProperty(String key, Object value){
        propertyBag.put(key, value);
        return this;
    }

    public HashMap<String, Object> getPropertyBag() {
        return propertyBag;
    }
    
}
