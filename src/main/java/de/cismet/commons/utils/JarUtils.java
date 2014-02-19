/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.commons.utils;

import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;

import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class JarUtils {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(JarUtils.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * This method checks every single class of the given jar, but classes only, no other resources. It validates
     * whether all classes have been signed with a signature, defined via the arguments <code>keystorePath</code> and
     * <code>alias</code>.
     *
     * @param   toSign        the jar file to verify
     * @param   keystorePath  DOCUMENT ME!
     * @param   keystorePass  DOCUMENT ME!
     * @param   alias         DOCUMENT ME!
     *
     * @return  true if checkSignature is true and all class files of the given jar are signed with the specified
     *          signature, false in any other case
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public static boolean isSigned(final File toSign,
            final String keystorePath,
            final String keystorePass,
            final String alias) {
        if (toSign == null) {
            throw new IllegalArgumentException("toSign file must not be null"); // NOI18N
        }

        if (LOG.isInfoEnabled()) {
            LOG.info("verifying signature for: " + toSign); // NOI18N
        }

        if ((keystorePass == null) || (keystorePath == null)) {
            LOG.warn(
                "Cannot verify signature because either keystorePath or keystorePass is null"); // NOI18N

            return false;
        }

        try {
            final JarInputStream jis = new JarInputStream(new BufferedInputStream(new FileInputStream(toSign)), true);
            final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(new BufferedInputStream(new FileInputStream(keystorePath)), keystorePass.toCharArray());
            final Certificate certificate = keystore.getCertificate(alias);
            final PublicKey key = certificate.getPublicKey();

            JarEntry entry;
            while ((entry = jis.getNextJarEntry()) != null) {
                // read from the stream to ensure the presence of the certs if any
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int byteRead;
                while ((byteRead = jis.read()) != -1) {
                    baos.write(byteRead);
                }

                final Certificate[] certs = entry.getCertificates();
                if (certs == null) {
                    if (entry.getName().endsWith(".class")) {
                        LOG.warn("class file not signed: " + entry + " | " + toSign); // NOI18N

                        // bail out, signature check failed
                        return false;
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("no certs for non-class entry, skipping: " + entry); // NOI18N
                        }
                    }
                } else {
                    boolean isVerified = false;
                    for (final Certificate cert : certs) {
                        if (cert.equals(certificate)) {
                            try {
                                cert.verify(key);
                                isVerified = true;

                                // we can get outta here
                                break;
                            } catch (final Exception e) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("certificate of entry cannot be verified: " // NOI18N
                                                + cert + " | entry: " + entry + " | toSign: " + toSign, // NOI18N
                                        e);
                                }
                            }
                        } else {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("skipping non-cismet cert: " + cert + " | entry: " + entry // NOI18N
                                            + " | toSign: " + toSign);              // NOI18N
                            }
                        }
                    }

                    if (!isVerified) {
                        LOG.warn("cannot verify entry: " + entry + " | toSign: " + toSign); // NOI18N

                        return false;
                    }
                }
            }
        } catch (final Exception e) {
            LOG.warn("cannot verify signature: " + toSign, e); // NOI18N
            return false;
        }

        if (LOG.isInfoEnabled()) {
            LOG.info("signature verified: " + toSign); // NOI18N
        }

        return true;
    }
}
