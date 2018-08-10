package com.kelvin.aem.service;

import java.util.List;

import com.kelvin.aem.bean.SchedulerItem;

public interface SchedulerBundleInfo {

    public List<SchedulerItem> getSchedulerServicePIDs(String[] bundlePrefixes, String[] servicePrefixes);

}
