package com.kelvin.aem.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kelvin.aem.service.SchedulerBundleInfo;
import com.kelvin.aem.servlet.SchedulerContent;
import com.kelvin.aem.servlet.SchedulerItem;

@Component(
        immediate = true,
        metatype = true,
        label = "Kelvin Getting Schedulers Service",
        description = "Find all scheduler services matching the prefix.")
@Service(SchedulerBundleInfo.class)
public class SchedulerBundleInfoImpl implements SchedulerBundleInfo {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerBundleInfoImpl.class);
    private BundleContext bundleContext;

    @Override
    public List<SchedulerItem> getSchedulerServicePIDs(String[] bundlePrefixes, String[] servicePrefixes) {
        final List<String> bundlePrefixList = getBundlePrefixList(bundlePrefixes);
        final List<String> servicePrefixList = getServicePrefixList(servicePrefixes);
        final List<SchedulerItem> pidList = new ArrayList<>();

        final Bundle[] bundles = bundleContext.getBundles();
        for (final Bundle bundle : bundles) {
            if (bundle.getSymbolicName() != null && listStartsWith(bundlePrefixList, bundle.getSymbolicName())) {
                LOGGER.debug("Found bundle with name: {}", bundle.getSymbolicName());

                final ServiceReference<?>[] registeredServices = bundle.getRegisteredServices();
                if (registeredServices != null) {
                    for (final ServiceReference<?> registeredService : registeredServices) {
                        final String pid = (String) registeredService.getProperty("service.pid");
                        if (pid != null && listStartsWith(servicePrefixList, pid)
                                && registeredService.getProperty(Scheduler.PROPERTY_SCHEDULER_EXPRESSION) != null) {
                            final SchedulerContent content = new SchedulerContent();
                            content.setTextContent(pid);
                            final SchedulerItem item = new SchedulerItem();
                            item.setValue(pid);
                            item.setContent(content);
                            pidList.add(item);
                            LOGGER.debug("Found scheduler service with pid: {}", pid);
                        }
                    }
                }
            }
        }

        return pidList;
    }

    private List<String> getBundlePrefixList(String[] bundlePrefixes) {
        if (bundlePrefixes != null && bundlePrefixes.length > 0) {
            return Arrays.asList(bundlePrefixes);
        }
        // if bundle prefix is not specified, use the current bundle name
        final Bundle thisBundle = bundleContext.getBundle();
        LOGGER.debug("Got current bundle is {}", thisBundle.getSymbolicName());
        return Arrays.asList(thisBundle.getSymbolicName());
    }

    private List<String> getServicePrefixList(String[] servicePrefixes) {
        if (servicePrefixes != null && servicePrefixes.length > 0) {
            return Arrays.asList(servicePrefixes);
        }
        // if service prefix is not specified, return empty
        return Collections.emptyList();
    }

    private boolean listStartsWith(List<String> list, String string) {
        if (list == null || list.isEmpty()) {
            // if list doesn't have anything, then always return true
            return true;
        }
        return list.stream().anyMatch(s -> string.startsWith(s));
    }

    @Activate
    @Modified
    protected void activate(ComponentContext cc) {
        bundleContext = cc.getBundleContext();
    }
}
