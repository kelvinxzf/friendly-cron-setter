package com.kelvin.aem.service.impl;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kelvin.aem.service.OsgiConfigService;

@Service(value = OsgiConfigService.class)
@Component(
        immediate = true,
        metatype = true,
        label = "Kelvin OSGi Configuration Service",
        description = "Programatically set properties of OSGi configurations.")
public class OsgiConfigServiceImpl implements OsgiConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OsgiConfigServiceImpl.class);

    @Reference
    private ConfigurationAdmin configAdmin;

    @Override
    public Object getProperty(String pid, String property) throws IOException {
        final Configuration conf = configAdmin.getConfiguration(pid);
        LOGGER.debug("Got configuration location {}", conf.getBundleLocation());
        final Dictionary<String, Object> props = conf.getProperties();
        if (props != null) {
            return props.get(property);
        }

        return null;
    }

    @Override
    public boolean setProperty(String pid, String property, Object value) throws IOException {
        final Configuration conf = configAdmin.getConfiguration(pid);
        Dictionary<String, Object> props = conf.getProperties();
        if (props == null) {
            props = new Hashtable<>();
        }

        props.put(property, value != null ? value : StringUtils.EMPTY);
        conf.update(props);
        return true;
    }
}
