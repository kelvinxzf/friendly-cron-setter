package com.kelvin.aem.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.kelvin.aem.service.SchedulerBundleInfo;

@SlingServlet(
        metatype = true,
        label = "Scheduler List Servlet",
        description = "Servlet that gets all scheduler pids",
        methods = { "GET" },
        resourceTypes = { "friendly-cron/components/utilities/cron-setter" },
        selectors = { "list" },
        extensions = { "json" })
public class SchedulerListServlet extends SlingAllMethodsServlet {
    private static final long serialVersionUID = -1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerListServlet.class);
    private static final String JSON_CONTENT_TYPE = "application/json";
    private static final String UTF8_ENCODING = "UTF-8";
    private static final String PROPERTY_BUNDLE_PREFIX = "bundlePrefix";
    private static final String PROPERTY_SCHEDULER_PID_PREFIX = "schedulerPIDPrefix";

    @Reference
    private transient SchedulerBundleInfo schedulerBundleInfo;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.debug("inside SchedulerListServlet servlet -> doGet");
        response.setContentType(JSON_CONTENT_TYPE);
        response.setCharacterEncoding(UTF8_ENCODING);

        String[] bundlePrefix = null;
        String[] schedulerPIDPrefix = null;
        final Resource resource = request.getResource();

        bundlePrefix = resource.getValueMap().get(PROPERTY_BUNDLE_PREFIX, String[].class);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Got bundlePrefix {}", Arrays.toString(bundlePrefix));
        }

        schedulerPIDPrefix = resource.getValueMap().get(PROPERTY_SCHEDULER_PID_PREFIX, String[].class);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Got schedulerPIDPrefix {}", Arrays.toString(schedulerPIDPrefix));
        }

        try {
            final List<SchedulerItem> pids = schedulerBundleInfo.getSchedulerServicePIDs(bundlePrefix,
                    schedulerPIDPrefix);
            final Gson gson = new Gson();
            final String pidsStr = gson.toJson(pids);
            response.getWriter().write(pidsStr);
        } catch (final Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + ex.getMessage() + "\"}");
        }
    }

}
