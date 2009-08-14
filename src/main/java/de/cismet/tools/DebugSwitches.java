/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.tools;

/**
 *
 * @author thorsten
 */
public class DebugSwitches {
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private static DebugSwitches instance=null;


    

    private DebugSwitches(){

    }

    public static DebugSwitches getInstance(){
        if (instance==null){
            instance=new DebugSwitches();
        }
        return instance;
    }



}
