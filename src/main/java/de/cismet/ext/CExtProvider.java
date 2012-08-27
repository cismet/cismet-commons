/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.ext;

import java.util.Collection;

/**
 *
 * @author thorsten
 */
public interface CExtProvider<T> {
    Collection<T> provideExtensions(CExtContext context);
    Class<T> getValidClass();
}
