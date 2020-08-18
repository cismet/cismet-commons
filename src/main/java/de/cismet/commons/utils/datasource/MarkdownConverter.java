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
public class MarkdownConverter implements DatasourcesPojoConverter {

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
        final StringBuilder sb = new StringBuilder();

        for (final ServicePojo service : ds.getServices()) {
            sb.append(service2Markdown(service, 0));
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
    private String service2Markdown(final ServicePojo s, final int recursion) {
        final StringBuilder sb = new StringBuilder();

        if (recursion > 0) {
            sb.append(createServicePrefix(recursion)).append(" ").append(s.getName()).append("\n");
        }

        if (s.getServices() != null) {
            for (final ServicePojo service : s.getServices()) {
                sb.append(service2Markdown(service, recursion + 1));
            }
        }
        if (s.getLayers() != null) {
            for (final LayerPojo layer : s.getLayers()) {
                sb.append(layer2Markdown(layer, 1));
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
    private String layer2Markdown(final LayerPojo s, final int recursion) {
        final StringBuilder sb = new StringBuilder();

        if (recursion > 0) {
            sb.append(createLayerPrefix(recursion)).append(" ").append(s.getTitle()).append("\n");
            if ((s.getAbstractText() != null) && !s.getAbstractText().equals("")) {
                final String abstractTmp = s.getAbstractText().trim();

                sb.append("(").append(abstractTmp).append(")").append("\n");
            }
        }

        if (s.getLayers() != null) {
            for (final LayerPojo layer : s.getLayers()) {
                sb.append(layer2Markdown(layer, recursion + 1));
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
    private String createLayerPrefix(final int recursion) {
        final StringBuilder builder = new StringBuilder();

        for (int i = 1; i < recursion; ++i) {
            builder.append("  ");
        }

        builder.append('-');

        return builder.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   recursion  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String createServicePrefix(final int recursion) {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < recursion; ++i) {
            builder.append('#');
        }

        return builder.toString();
    }
}
