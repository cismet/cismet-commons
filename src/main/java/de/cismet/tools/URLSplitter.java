/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools;

/**
 * Urlsplitter
 *
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
public class URLSplitter {

    //~ Instance fields --------------------------------------------------------

    private String prot_prefix = ""; // NOI18N
    private String server = "";      // NOI18N
    private String path = "";        // NOI18N
    private String object_name = ""; // NOI18N

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new URLSplitter object.
     *
     * @param  url  given url
     */
    public URLSplitter(final String url) {
        // Versuch den Protokoll Prefix zu f\u00FCllen
        String rest = url;
        int pos = -1;
        final String[] s = rest.split("://"); // NOI18N
        if (s.length > 1) {
            prot_prefix = s[0] + "://";       // NOI18N
            rest = s[1];
        }

        // Versuch den Server rauszufiltern
        if (rest.startsWith("\\\\")) { // NOI18N
            // Windowskram
            prot_prefix = "\\\\";     // NOI18N
            rest = rest.substring(2);
            pos = rest.indexOf("\\"); // NOI18N
            if (pos != -1) {
                server = rest.substring(0, pos);
                rest = rest.substring(pos, rest.length());
            }
        } else {
            pos = rest.indexOf("/");  // NOI18N
            if (pos != -1) {
                server = rest.substring(0, pos);
                rest = rest.substring(pos, rest.length());
            }
        }

        // Versuch den Pfad rauszufiltern
        pos = rest.lastIndexOf("/");           // NOI18N
        if (pos != -1) {
            if (rest.lastIndexOf("?") > pos) { // NOI18N
                pos = rest.lastIndexOf("?");   // NOI18N
            }
            path = rest.substring(0, pos + 1);
            rest = rest.substring(pos + 1, rest.length());
        } else {
            pos = rest.lastIndexOf("\\");      // NOI18N
            if (pos != -1) {
                path = rest.substring(0, pos + 1);
                rest = rest.substring(pos + 1, rest.length());
            }
        }

        object_name = rest;
    }

    /**
     * Creates a new instance of URLSplitter.
     */
    private URLSplitter() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Getter for <code>prot_prefix</code>
     *
     * @return  <code>prot_prefix</code>
     */
    public String getProt_prefix() {
        return prot_prefix;
    }

    /**
     * Getter for <code>server</code>
     *
     * @return  <code>server</code>
     */
    public String getServer() {
        return server;
    }

    /**
     * Getter for <code>path</code>
     *
     * @return  <code>path</code>
     */
    public String getPath() {
        return path;
    }

    /**
     * Getter for <code>object_name</code>
     *
     * @return  <code>object_name</code>
     */
    public String getObject_name() {
        return object_name;
    }

    /**
     * returns a <code>String</code>, which has the following form<br>
     * Prot:   {@link #prot_prefix}<br>
     * Server: {@link #server}<br>
     * Path:   {@link #path}<br>
     * Objekt: {@link #object_name}<br>
     * 
     * @return <code>String</code>
     */
    
    @Override
    public String toString() {
        return "Prot:   " + prot_prefix + "\n"   // NOI18N
                    + "Server: " + server + "\n" // NOI18N
                    + "Path:   " + path + "\n"   // NOI18N
                    + "Objekt: " + object_name;  // NOI18N
    }

    /**
     * main
     *
     * @param  args  args
     */
    public static void main(final String[] args) {
        System.out.println("\n" + new URLSplitter("http://www.google.de/"));                                          // NOI18N
        System.out.println("\n" + new URLSplitter("https://groups.google.de/grphp?hl=de&tab=wg&q="));                 // NOI18N
        System.out.println("\n" + new URLSplitter("name:pass@ftp://wupp.com/file.txt"));                              // NOI18N
        System.out.println("\n"
                    + new URLSplitter(
                        "file:///c:/Dokumente%20und%20Einstellungen/hell/Desktop/dev/wuppertalerCapabilities.xml"));  // NOI18N
        System.out.println("\n" + new URLSplitter("C:\\"));                                                           // NOI18N
        System.out.println("\n"
                    + new URLSplitter("C:\\Dokumente und Einstellungen\\hell\\Desktop\\Neu Notepad++ Document.txt")); // NOI18N
        System.out.println("\n" + new URLSplitter("\\\\192.168.100.150\\nfs\\archivierte VM's"));                     // NOI18N
    }
}
