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
package de.cismet.tools;

import org.apache.log4j.Logger;

import java.awt.Desktop;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * EMailComposer launches the mail composing window of the user default mail client with the following message fields:
 * "to", "cc", "bcc", "subject", "body". None of these fields have to be set, only some of the fields can be set or all
 * fields can be set.
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 * @see      Desktop.mail()
 * @see      The mailto URL scheme (RFC 2368) (http://www.ietf.org/rfc/rfc2368.txt)
 */
public class EMailComposer {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(EMailComposer.class);

    //~ Instance fields --------------------------------------------------------

    private String subject;
    private String body;
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private boolean isFirstHeader;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EMailComposer object.
     */
    public EMailComposer() {
        to = new ArrayList<String>();
        cc = new ArrayList<String>();
        bcc = new ArrayList<String>();
    }

    /**
     * Creates a new EMailComposer object.
     *
     * @param  to       DOCUMENT ME!
     * @param  subject  DOCUMENT ME!
     * @param  body     DOCUMENT ME!
     */
    public EMailComposer(final String to, final String subject, final String body) {
        this.subject = subject;
        this.body = body;
        this.to = new ArrayList<String>();
        this.to.add(to);
        this.cc = new ArrayList<String>();
        this.bcc = new ArrayList<String>();
    }

    /**
     * Creates a new EMailComposer object.
     *
     * @param  subject  DOCUMENT ME!
     * @param  body     DOCUMENT ME!
     * @param  to       DOCUMENT ME!
     * @param  cc       DOCUMENT ME!
     * @param  bcc      DOCUMENT ME!
     */
    public EMailComposer(final String subject,
            final String body,
            final List<String> to,
            final List<String> cc,
            final List<String> bcc) {
        this.subject = subject;
        this.body = body;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void compose() {
        isFirstHeader = true;
        final StringBuilder uriBuilder = new StringBuilder("mailto:");
        appendTo(uriBuilder);
        appendCC(uriBuilder);
        appendBCC(uriBuilder);
        appendSubject(uriBuilder);
        appendBody(uriBuilder);

        try {
            final String buildedString = uriBuilder.toString();
            System.out.println(buildedString);
            if (buildedString.equals("mailto:")) {
                // opens an empty mail
                Desktop.getDesktop().mail();
            } else {
                final URI mailtoURI = new URI(uriBuilder.toString());
                Desktop.getDesktop().mail(mailtoURI);
            }
        } catch (IOException ex) {
            LOG.error("Error while trying to open standard e-mail program.", ex);
        } catch (URISyntaxException ex) {
            LOG.error("Error while creating mailtoUri.", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  uriBuilder  DOCUMENT ME!
     */
    private void appendTo(final StringBuilder uriBuilder) {
        appendMail(uriBuilder, "to", to);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  uriBuilder  DOCUMENT ME!
     */
    private void appendCC(final StringBuilder uriBuilder) {
        appendMail(uriBuilder, "cc", cc);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  uriBuilder  DOCUMENT ME!
     */
    private void appendBCC(final StringBuilder uriBuilder) {
        appendMail(uriBuilder, "bcc", bcc);
    }

    /**
     * Append mail addresses from a List to a SringBuilder. Those addresses will be separated by semicolons.
     *
     * @param  uriBuilder     DOCUMENT ME!
     * @param  header         DOCUMENT ME!
     * @param  mailAddresses  DOCUMENT ME!
     */
    private void appendMail(final StringBuilder uriBuilder, final String header, final List<String> mailAddresses) {
        if ((mailAddresses != null) && !mailAddresses.isEmpty()) {
            uriBuilder.append(headerSeparator()).append(header).append("=");
            uriBuilder.append(urlEncode(mailAddresses.get(0)));
            for (int i = 1; i < mailAddresses.size(); i++) {
                uriBuilder.append(";");
                uriBuilder.append(urlEncode(mailAddresses.get(i)));
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  uriBuilder  DOCUMENT ME!
     */
    private void appendSubject(final StringBuilder uriBuilder) {
        appendText(uriBuilder, "subject", subject);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  uriBuilder  DOCUMENT ME!
     */
    private void appendBody(final StringBuilder uriBuilder) {
        appendText(uriBuilder, "body", body);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  uriBuilder  DOCUMENT ME!
     * @param  header      DOCUMENT ME!
     * @param  value       DOCUMENT ME!
     */
    private void appendText(final StringBuilder uriBuilder, final String header, final String value) {
        if ((value != null) && !value.equals("")) {
            uriBuilder.append(headerSeparator()).append(header).append("=");
            uriBuilder.append(urlEncode(value));
        }
    }

    /**
     * Encodes a URL with the class URLEncoder and the encoding UTF-8. If the encoding is not supported a standard
     * encoding will be used.
     *
     * @param   string  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String urlEncode(String string) {
        try {
            string = URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOG.error("The encoding UTF-8 is not supported, use standard encoding instead.", ex);
            string = URLEncoder.encode(string);
        }
        return string.replace("+", "%20");
    }

    /**
     * Return '?' if isFirstHeader is true. Otherwise it returns '&'.
     *
     * @return  DOCUMENT ME!
     */
    private String headerSeparator() {
        if (isFirstHeader) {
            isFirstHeader = false;
            return "?";
        } else {
            return "&";
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getSubject() {
        return subject;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  subject  DOCUMENT ME!
     */
    public void setSubject(final String subject) {
        this.subject = subject;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getBody() {
        return body;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  body  DOCUMENT ME!
     */
    public void setBody(final String body) {
        this.body = body;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<String> getTo() {
        return to;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  to  DOCUMENT ME!
     */
    public void setTo(final List<String> to) {
        this.to = to;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<String> getCc() {
        return cc;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cc  DOCUMENT ME!
     */
    public void setCc(final List<String> cc) {
        this.cc = cc;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<String> getBcc() {
        return bcc;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bcc  DOCUMENT ME!
     */
    public void setBcc(final List<String> bcc) {
        this.bcc = bcc;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  to  DOCUMENT ME!
     */
    public void addTo(final String... to) {
        for (final String mail : to) {
            if (mail != null) {
                this.to.add(mail);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cc  DOCUMENT ME!
     */
    public void addCC(final String... cc) {
        for (final String mail : cc) {
            if (mail != null) {
                this.cc.add(mail);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bcc  DOCUMENT ME!
     */
    public void addBCC(final String... bcc) {
        for (final String mail : bcc) {
            if (mail != null) {
                this.bcc.add(mail);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  UnsupportedEncodingException  DOCUMENT ME!
     */
    public static void main(final String[] args) throws UnsupportedEncodingException {
        final EMailComposer mail = new EMailComposer();
        mail.setBody("bla bla \n bla? \n\r ??? \r\n win newline");
        mail.setSubject("bla bla bla? & ???");
        mail.addTo("hehf@ifhf.de", "guwgfuirwe@ihwfco.de;guwgfuirwe2@ihwfco.de");
        mail.addBCC("hal+&lo@test.de", "ui_i_u_aa@tralalala.com");

        mail.compose();
    }
}
