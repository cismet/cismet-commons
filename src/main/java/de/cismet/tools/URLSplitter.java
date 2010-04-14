/*
 * URLSplitter.java
 * Copyright (C) 2005 by:
 *
 *----------------------------
 * cismet GmbH
 * Goebenstrasse 40
 * 66117 Saarbruecken
 * http://www.cismet.de
 *----------------------------
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *----------------------------
 * Author:
 * thorsten.hell@cismet.de
 *----------------------------
 *
 * Created on 8. Februar 2006, 14:55
 *
 */

package de.cismet.tools;

/**
 *
 * @author thorsten.hell@cismet.de
 */
public class URLSplitter {
    private String prot_prefix="";  // NOI18N
    private String server="";   // NOI18N
    private String path="";  // NOI18N
    private String object_name="";  // NOI18N
    
    /** Creates a new instance of URLSplitter */
    private URLSplitter() {
        
    }
    public URLSplitter(String url) {
        //Versuch den Protokoll Prefix zu f\u00FCllen
        String rest=url;
        int pos=-1;
        String[] s=rest.split("://");  // NOI18N
        if (s.length>1) {
            prot_prefix=s[0]+"://";  // NOI18N
            rest=s[1];
        }
        
        //Versuch den Server rauszufiltern
        if (rest.startsWith("\\\\")) {  // NOI18N
            //Windowskram
            prot_prefix="\\\\";  // NOI18N
            rest=rest.substring(2);
            pos=rest.indexOf("\\");  // NOI18N
            if (pos!=-1) {
                server=rest.substring(0,pos);
                rest=rest.substring(pos,rest.length());
            }
            
        }
        else {
            pos=rest.indexOf("/");  // NOI18N
            if (pos!=-1) {
                server=rest.substring(0,pos);
                rest=rest.substring(pos,rest.length());
            }
        }
        
        //Versuch den Pfad rauszufiltern
        pos=rest.lastIndexOf("/");  // NOI18N
        if (pos!=-1) {
            if (rest.lastIndexOf("?")>pos) {  // NOI18N
                pos=rest.lastIndexOf("?");  // NOI18N
            }
            path=rest.substring(0,pos+1);
            rest=rest.substring(pos+1,rest.length());
        }
        else {
            pos=rest.lastIndexOf("\\");  // NOI18N
            if (pos!=-1) {
                path=rest.substring(0,pos+1);
                rest=rest.substring(pos+1,rest.length());
            }
        }
        
        object_name=rest;
        
    }

    public String getProt_prefix() {
        return prot_prefix;
    }

    public String getServer() {
        return server;
    }

    public String getPath() {
        return path;
    }

    public String getObject_name() {
        return object_name;
    }
    
    public String toString() {
        return "Prot:   "+prot_prefix+"\n"+  // NOI18N
               "Server: "+server+"\n"+  // NOI18N
               "Path:   "+path+"\n"+  // NOI18N
               "Objekt: "+object_name;  // NOI18N
    }
    
    public static void main(String[] args){
        System.out.println("\n"+new URLSplitter("http://www.google.de/"));  // NOI18N
        System.out.println("\n"+new URLSplitter("https://groups.google.de/grphp?hl=de&tab=wg&q="));  // NOI18N
        System.out.println("\n"+new URLSplitter("name:pass@ftp://wupp.com/file.txt"));  // NOI18N
        System.out.println("\n"+new URLSplitter("file:///c:/Dokumente%20und%20Einstellungen/hell/Desktop/dev/wuppertalerCapabilities.xml"));  // NOI18N
        System.out.println("\n"+new URLSplitter("C:\\"));  // NOI18N
        System.out.println("\n"+new URLSplitter("C:\\Dokumente und Einstellungen\\hell\\Desktop\\Neu Notepad++ Document.txt"));  // NOI18N
        System.out.println("\n"+new URLSplitter("\\\\192.168.100.150\\nfs\\archivierte VM's"));  // NOI18N
        
        
        
    }
    
    
}
