package org.softclicker.server.config;

import java.io.File;
import java.net.URLClassLoader;

public interface ConfigManager {

    String SOFTCLICKER_HOME_DIR = System.getProperty("SOFTCLICKER_HOME", System.getProperty("user.dir") + File.separator);

    String SOFTCLICKER_CONFIG_DIR = SOFTCLICKER_HOME_DIR + "conf";

    DataSourceConfig getDatasourceConfiguration();
}