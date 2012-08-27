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
import org.openide.util.Lookup;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class CExtManager {

    
    //~ Methods ----------------------------------------------------------------
    
    /**
     * DOCUMENT ME!
     *
     * @param   context  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */    
    
    public <T> Collection<T> getExtensionFor(Class<T> c,final CExtContext context) { 
        ArrayList<T> extensions=new ArrayList<T>();
        //Get All Providers
        Collection<? extends CExtProvider> allProviders=Lookup.getDefault().lookupAll(CExtProvider.class);
        
        for (CExtProvider extProvider:allProviders){
            extensions.addAll(extProvider.provideExtensions(context));
        }
        
        return extensions;
    }
}
