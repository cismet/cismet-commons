/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * ConnectionInfo.java
 *
 * Created on 8. Januar 2005, 13:45
 */
package de.cismet.tools;

import org.jdom.Element;

/**
 * Bean Class to save Connection Infos.
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class ConnectionInfo {

    //~ Instance fields --------------------------------------------------------

    private String driver;
    private String url;
    private String user;
    private String pass;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of ConnectionInfo.
     */
    public ConnectionInfo() {
    }

    /**
     * Creates a new ConnectionInfo object.
     *
     * @param  element  <code>Elemtent</code>
     */
    public ConnectionInfo(final Element element) {              // throws NullPointerException{
        driver = element.getChild("driverClass").getTextTrim(); // NOI18N
        url = element.getChild("dbUrl").getTextTrim();          // NOI18N
        user = element.getChild("user").getTextTrim();          // NOI18N
        pass = element.getChild("pass").getTextTrim();          // NOI18N
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Returns the DriverClass
     *
     * @return  DriverClass
     */
    public String getDriver() {
        return driver;
    }

    /**
     * Sets the DriverClass
     *
     * @param  driver  Driver Class
     */
    public void setDriver(final String driver) {
        this.driver = driver;
    }

    /**
     * Returns the DB Url
     *
     * @return  DB Url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the DB Url
     *
     * @param  url  DB Url
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * Getter for User.
     *
     * @return  DB USer
     */
    public String getUser() {
        return user;
    }

    /**
     * Setter for User.
     *
     * @param  user  DB User
     */
    public void setUser(final String user) {
        this.user = user;
    }

    /**
     * Returns Password.
     *
     * @return  Password
     */
    public String getPass() {
        if ((pass != null) && pass.startsWith(PasswordEncrypter.CRYPT_PREFIX)) {
            return PasswordEncrypter.decryptString(pass);
        } else {
            return pass;
        }
    }

    /**
     * Sets Password
     *
     * @param  pass  Password
     */
    public void setPass(final String pass) {
        this.pass = pass;
    }

    /**
     * Getter for Element
     *
     * @return  Element
     */
    public Element getElement() {
        final Element e = new Element("dbConnectionInfo");                                       // NOI18N
        e.addContent(new Element("driverClass").addContent(driver));                             // NOI18N
        e.addContent(new Element("dbUrl").addContent(url));                                      // NOI18N
        e.addContent(new Element("user").addContent(user));                                      // NOI18N
        if (!pass.trim().startsWith("crypt::")) {                                                // NOI18N
            e.addContent(new Element("pass").addContent(PasswordEncrypter.encryptString(pass))); // NOI18N
        } else {
            e.addContent(new Element("pass").addContent(pass));                                  // NOI18N
        }
        return e;
    }
}
