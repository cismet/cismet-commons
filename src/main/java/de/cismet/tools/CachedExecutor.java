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
package de.cismet.tools;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public abstract class CachedExecutor {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(CachedExecutor.class);
    private static final List<String> RUNNING_REQUESTS = Collections.synchronizedList(new ArrayList<>());
    private static final Map<String, List<CachedExecutor>> WAITING_REQUESTS = Collections.synchronizedMap(
            new HashMap<>());
//    private static final Map<String, Object> results = Collections.synchronizedMap(new HashMap<>());

    //~ Instance fields --------------------------------------------------------

    private boolean resultExists = false;
    private Object result = null;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    protected abstract Object run() throws Exception;

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public Object execute(final String key) throws Exception {
        boolean wait = false;

        synchronized (RUNNING_REQUESTS) {
            if (RUNNING_REQUESTS.contains(key)) {
                List<CachedExecutor> executors = WAITING_REQUESTS.get(key);

                if (executors == null) {
                    executors = new ArrayList<>();

                    WAITING_REQUESTS.put(key, executors);
                }

                executors.add(this);
                wait = true;
            } else {
                RUNNING_REQUESTS.add(key);
            }
        }

        if (wait) {
            while (!resultExists) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ex) {
                    // nothing to do
                }
            }

            if (result != null) {
                return result;
            } else {
                return run();
            }
        } else {
            boolean exceptionThrown = false;
            Object res = null;

            try {
                res = run();
            } catch (Exception e) {
                LOG.error("Error while processing request", e);
                res = e;
                exceptionThrown = true;
            }

            synchronized (RUNNING_REQUESTS) {
                final List<CachedExecutor> executors = WAITING_REQUESTS.get(key);
                RUNNING_REQUESTS.remove(key);

                if (executors != null) {
                    for (final CachedExecutor tmp : executors) {
                        if (!(res instanceof Exception)) {
                            tmp.result = res;
                        }
                        tmp.resultExists = true;
                    }
                }
            }

            if (exceptionThrown) {
                throw (Exception)res;
            } else {
                return res;
            }
        }
    }
}
