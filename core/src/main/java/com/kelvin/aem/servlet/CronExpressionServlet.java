package com.kelvin.aem.servlet;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.scheduler.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kelvin.aem.service.OsgiConfigService;
import com.kelvin.aem.service.SchedulerBundleInfo;

@SlingServlet(
        metatype = true,
        label = "Cron Expression Servlet",
        description = "Servlet that gets and sets cron expression for a scheduler",
        methods = { "GET", "POST" },
        resourceTypes = { "friendly-cron/components/utilities/cron-setter" },
        selectors = { "service" },
        extensions = { "json" })
public class CronExpressionServlet extends SlingAllMethodsServlet {
    private static final long serialVersionUID = -1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(CronExpressionServlet.class);
    private static final String JSON_CONTENT_TYPE = "application/json";
    private static final String UTF8_ENCODING = "UTF-8";
    private static final String PARAM_PID = "pid";
    private static final String PARAM_EXPRESSION = "expression";

    @Reference
    private transient OsgiConfigService osgiConfigService;

    @Reference
    private transient SchedulerBundleInfo schedulerBundleInfo;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.debug("inside CronExpressionServlet servlet -> doGet");
        response.setContentType(JSON_CONTENT_TYPE);
        response.setCharacterEncoding(UTF8_ENCODING);

        final String pid = request.getParameter(PARAM_PID);
        if (StringUtils.isNotEmpty(pid)) {
            try {
                final Object obj = osgiConfigService.getProperty(pid, Scheduler.PROPERTY_SCHEDULER_EXPRESSION);
                final String expression = (String) obj;
                LOGGER.debug("Got cron expression for pid {}: [{}]", pid, expression);
                response.getWriter().write("{\"" + PARAM_EXPRESSION + "\": \"" + expression + "\"}");
            } catch (final Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\": \"" + ex.getMessage() + "\"}");
            }
        } else {
            // no pid provided, simply return 204 to prevent it from being cached
            response.setStatus(SlingHttpServletResponse.SC_NO_CONTENT);
        }
    }

    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.debug("inside CronExpressionServlet servlet -> doPost");
        response.setContentType(JSON_CONTENT_TYPE);
        response.setCharacterEncoding(UTF8_ENCODING);

        final StringBuilder reqBuilder = new StringBuilder();
        String s;
        while ((s = request.getReader().readLine()) != null) {
            reqBuilder.append(s);
        }
        final String reqStr = reqBuilder.toString();
        LOGGER.debug("Got request JSON = {}", reqStr);

        if (!reqStr.isEmpty()) {
            final Gson gson = new Gson();
            try {
                final Type reqMapType = new TypeToken<HashMap<String, String>>() {
                }.getType();
                final Map<String, String> reqMap = gson.fromJson(reqStr, reqMapType);
                final String pid = reqMap.get(PARAM_PID);
                final String expression = reqMap.get(PARAM_EXPRESSION);
                if (StringUtils.isNotEmpty(pid) && StringUtils.isNotEmpty(expression)) {
                    final boolean success = osgiConfigService.setProperty(pid, Scheduler.PROPERTY_SCHEDULER_EXPRESSION,
                            expression);
                    response.getWriter().write("{\"success\": \"" + success + "\"}");
                } else {
                    // missing any of the params, simply return 204 to prevent it from being cached
                    response.setStatus(SlingHttpServletResponse.SC_NO_CONTENT);
                }

            } catch (final Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\": \"" + ex.getMessage() + "\"}");
            }
        } else {
            // no request json provided, simply return 204 to prevent it from being cached
            response.setStatus(SlingHttpServletResponse.SC_NO_CONTENT);
        }

    }
}
