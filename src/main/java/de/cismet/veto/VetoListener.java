/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.veto;

/**
 *
 * @author spuhl
 */
public interface VetoListener {
    void veto() throws VetoException;
}
