package com.kelvin.aem.service;

import java.util.List;

import com.kelvin.aem.bean.SchedulerItem;

/**
 * @author kexu
 *
 *         Interface that gets scheduler services
 */
public interface SchedulerBundleInfo {

    public List<SchedulerItem> getSchedulerServicePIDs(String[] bundlePrefixes, String[] servicePrefixes);

}
