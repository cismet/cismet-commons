/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools;

import java.util.Locale;

/**
 * JnlpTools Class
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class JnlpTools {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new JnlpTools object.
     */
    private JnlpTools() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This operation creates a new default {@link Locale} using the information provided through the relevant
     * {@link System} properties. The relevant properties are:<br/>
     * <br/>
     *
     * <ul>
     *   <li><code>user.language</code></li>
     *   <li><code>user.country</code></li>
     *   <li><code>user.variant</code></li>
     * </ul>
     * <br/>
     * <br/>
     * The adjustment of the <code>Locale</code> is performed by exchanging the <code>language</code>, <code>
     * country</code> and <code>variant</code> of the default <code>Locale</code> with the values of the properties
     * mentioned above. However, a replacement will only be done if the specific value is not empty. In this case the
     * default (platform provided) values will remain. This operation will not change anything for applications started
     * using the <code>java</code> binary (unless the <code>user.*</code> are not changed at runtime before). For <code>
     * javaws</code> applications using this operation at application startup it is possible to adjust the <code>
     * Locale</code> properties according to the information provided by the JNLP file (and thus the resulting <code>
     * System</code> properties. As of the non-destructive nature of this operation it is recommended to call it at
     * application startup if you plan to distribute it using Java WebStart. <b>However, there is a catch: the
     * {@link SecurityManager} has to grant the permission to change the default <code>Locale</code>.</b>
     *
     * @throws  SecurityException  if the <code>SecurityManager</code> denies the change of the default <code>
     *                             Locale</code>
     */
    public static void adjustDefaultLocale() throws SecurityException {
        final Locale defaultLocale = Locale.getDefault();
        final String defaultLang = defaultLocale.getLanguage();
        final String defaultCountry = defaultLocale.getCountry();
        final String defaultVariant = defaultLocale.getVariant();

        final String lang = System.getProperty("user.language", "");   // NOI18N
        final String country = System.getProperty("user.country", ""); // NOI18N
        final String variant = System.getProperty("user.variant", ""); // NOI18N

        final String newLang;
        if (lang.isEmpty()) {
            newLang = defaultLang;
        } else {
            newLang = lang;
        }

        final String newCountry;
        if (country.isEmpty()) {
            newCountry = defaultCountry;
        } else {
            newCountry = country;
        }

        final String newVariant;
        if (variant.isEmpty()) {
            newVariant = defaultVariant;
        } else {
            newVariant = variant;
        }

        Locale.setDefault(new Locale(newLang, newCountry, newVariant));
    }
}
