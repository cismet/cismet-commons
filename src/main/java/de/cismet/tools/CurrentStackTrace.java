/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.tools;

/**
 *
 * @author thorsten
 */
public class CurrentStackTrace extends Throwable{

    @Override
    public StackTraceElement[] getStackTrace() {
        return Thread.currentThread().getStackTrace();
    }
    

}
