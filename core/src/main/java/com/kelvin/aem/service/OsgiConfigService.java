package com.kelvin.aem.service;

import java.io.IOException;

/**
 * @author kexu
 *
 *         Interface that gets or sets osgi configs
 */
public interface OsgiConfigService {

    public Object getProperty(String pid, String property) throws IOException;

    public boolean setProperty(String pid, String property, Object value) throws IOException;
}
