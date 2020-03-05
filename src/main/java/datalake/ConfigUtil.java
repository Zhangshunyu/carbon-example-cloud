package datalake;

import java.io.*;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Properties;

import org.apache.carbondata.common.logging.LogServiceFactory;
import org.apache.carbondata.core.datastore.filesystem.CarbonFile;
import org.apache.carbondata.core.datastore.impl.FileFactory;
import org.apache.carbondata.core.util.CarbonProperties;

import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;
import org.apache.spark.sql.SparkSession;

public class ConfigUtil {
    private static Logger LOGGER =
            LogServiceFactory.getLogService(ConfigUtil.class.getCanonicalName());

    /**
     * load config.conf, including kafka
     */
    public static Properties loadCommonConfig(String configFileName, final Configuration hadoopConf) {
        Properties properties = new Properties();
        String configFilePath = configFileName;
        try {
            File configFile = new File(ConfigUtil.class.
                    getResource("/").getPath() + "/" + configFileName);
            if (configFile.exists()) {
                configFilePath = new File(ConfigUtil.class.
                        getResource("/").getPath() + "/" + configFileName).getCanonicalPath();
                properties = loadProperties(configFilePath, hadoopConf);
            } else {
                properties.load(new FileInputStream(configFileName));
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load your spark conf: ", e);
        }
        return properties;
    }

    /**
     * load carbon.properties into CarbonProperties
     */
    public static void loadCarbonConfig(final String configFile, final Configuration hadoopConf) throws IOException {
        if (!configFile.isEmpty()) {
            LOGGER.info("====" + configFile);
            Properties properties = loadProperties(configFile, hadoopConf);
            // add all property into CarbonProperties
            Iterator<String> iterator = properties.stringPropertyNames().iterator();
            CarbonProperties carbonProperties = CarbonProperties.getInstance();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = properties.getProperty(key).trim();
                LOGGER.info(key + "=" + value);
                carbonProperties.addProperty(key, value);
            }
        } else {
            LOGGER.info("no carbon property file config, no need to load.");
        }
    }

    /**
     * load spark-obs.conf to overwrite the default config of Spark session
     */
    public static void loadSparkConfig(String configFileName, final Configuration hadoopConf,
                                       final SparkSession.Builder builder) {
        Properties properties = new Properties();
        String configFilePath = configFileName;
        try {
            File configFile = new File(ConfigUtil.class.
                    getResource("/").getPath() + "/" + configFileName);
            if (configFile.exists()) {
                configFilePath = new File(ConfigUtil.class.
                        getResource("/").getPath() + "/" + configFileName).getCanonicalPath();
                properties = loadProperties(configFilePath, hadoopConf);
            } else {
                properties.load(new FileInputStream(configFileName));
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load your spark conf: ", e);
        }

        // add all property into SparkConf
        Iterator<String> iterator = properties.stringPropertyNames().iterator();
        LOGGER.info("start to load config file " + configFilePath);
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = properties.getProperty(key).trim();
            builder.config(key, value);
            if (!key.endsWith(".key")) {
                // ak sk should not print out into log!
                LOGGER.info(key + "=" + value);
            }
        }
    }

    /**
     * load a property file in obs into Properties object
     */
    private static Properties loadProperties(final String configFile,
                                             final Configuration hadoopConf) throws UnknownHostException {
        Properties properties = new Properties();
        DataInputStream dataInputStream = null;
        try {
            CarbonFile file = FileFactory.getCarbonFile(configFile, hadoopConf);
            dataInputStream =
                    file.getDataInputStream(configFile, FileFactory.getFileType(configFile), 4096,
                            hadoopConf);
            properties.load(dataInputStream);
        } catch (IOException e) {
            LOGGER.error("Failed to load config file " + configFile, e);
        } finally {
            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    LOGGER.error("Failed to close config file " + configFile, e);
                }
            }
        }
        return properties;
    }
}
