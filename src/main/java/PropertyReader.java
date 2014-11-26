import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;


public class PropertyReader {

    private static final Logger logger = LoggerFactory.getLogger(PropertyReader.class);

    public String getProperty(String property) {

        Properties prop = new Properties();
        String propFileName = "application.properties";

        File configFile = new File(propFileName);
        if(configFile.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(configFile));
                prop.load(br);
                return prop.getProperty(property);
            } catch (IOException e) {
                logger.error("Could not read property [" + property + "]", e);
            }
        }

        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
            prop.load(inputStream);
            if (inputStream == null) {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
            return prop.getProperty(property);
        } catch (IOException e) {
            logger.error("Could not read property [" + property + "]", e);
        }

        logger.warn("Propery [" + property + "] not found!");
        return "";
    }

}
