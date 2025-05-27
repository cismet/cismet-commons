/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.commons.utils.datasource;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class HtmlDetailConverter implements DatasourcesPojoConverter {

    //~ Static fields/initializers ---------------------------------------------

    private static final String CLOSE_DIV = "</div>";
    private static final String OPEN_ABSTRACT_DIV = "<div class=\"abstract\">";

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   ds  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String convertDatasource(final DatasourcesPojo ds) {
        final StringBuilder sb = new StringBuilder("<html><body>");

        for (final ServicePojo service : ds.getServices()) {
            sb.append(service2Html(service, 0));
        }
        sb.append("</body></html>");

        return sb.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   s          DOCUMENT ME!
     * @param   recursion  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String service2Html(final ServicePojo s, final int recursion) {
        final StringBuilder sb = new StringBuilder();

        if (recursion > 0) {
            sb.append(createIdentationPrefix(recursion + 1)).append("<summary>").append(s.getName());
            if (s.getAccessError() != null) {
                sb.append(createIdentationPrefix(recursion + 1))
                        .append("<div class='accessError'>")
                        .append(s.getAccessError())
                        .append("</div>");
            }
            sb.append("</summary>").append("\n");
        }

        if (s.getServices() != null) {
            for (final ServicePojo service : s.getServices()) {
                sb.append(createIdentationPrefix(recursion)).append("<details>").append("\n");
                sb.append(createIdentationPrefix(recursion)).append(service2Html(service, recursion + 1));
                sb.append(createIdentationPrefix(recursion)).append("</details>").append("\n");
            }
        }

        if (s.getLayers() != null) {
            for (final LayerPojo layer : s.getLayers()) {
                sb.append(createIdentationPrefix(recursion)).append("<details>").append("\n");
                sb.append(createIdentationPrefix(recursion)).append(layer2Html(layer, recursion + 1));
                sb.append(createIdentationPrefix(recursion)).append("</details>").append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   s          DOCUMENT ME!
     * @param   recursion  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String layer2Html(final LayerPojo s, final int recursion) {
        final StringBuilder sb = new StringBuilder();

        if (recursion > 0) {
            sb.append(createIdentationPrefix(recursion)).append("<summary>").append(s.getTitle());
            if ((s.getAbstractText() != null) && !s.getAbstractText().equals("")) {
                final String abstractTmp = s.getAbstractText().trim();

                sb.append("\n")
                        .append(createIdentationPrefix(recursion + 1))
                        .append(OPEN_ABSTRACT_DIV)
                        .append(abstractTmp)
                        .append(CLOSE_DIV);
            }
            sb.append("</summary>").append("\n");
        }

        if (s.getLayers() != null) {
            for (final LayerPojo layer : s.getLayers()) {
                sb.append(createIdentationPrefix(recursion)).append("<details>").append("\n");
                sb.append(layer2Html(layer, recursion + 1));
                sb.append(createIdentationPrefix(recursion)).append("</details>").append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   recursion  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String createIdentationPrefix(final int recursion) {
        final StringBuilder builder = new StringBuilder();

        for (int i = 1; i < recursion; ++i) {
            builder.append("\t");
        }

        return builder.toString();
    }
}
