import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;

public class LogReader extends Thread {

    private String systemName = "";

    private static final Logger logger = LoggerFactory.getLogger(LogReader.class);


    private String getNewestLogfile() {
        PropertyReader propertyReader = new PropertyReader();
        String logPath = propertyReader.getProperty("logPath");

        File dir = new File(logPath);
        FileFilter fileFilter = new WildcardFileFilter("*.log");
        File[] files = dir.listFiles(fileFilter);

        if(files == null) {
            logFileError();
        }

        Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);

        for(File file : files) {
            if(file.getName().contains("netLog")) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }

    public void logFileError() {
        PropertyReader propertyReader = new PropertyReader();
        String logPath = propertyReader.getProperty("logPath");
        logger.error("Log File not found in Directory: " + logPath + " . Application closing..");
        System.exit(0);
    }

    private void waitDelay() {
        try {
            sleep(60000);
        } catch (InterruptedException e) {
            logger.error("Sleep failed", e);
        }
    }

    public void run()  {

        PropertyReader propertyReader = new PropertyReader();
        String commander = propertyReader.getProperty("commander");
        DataSender dataSender = new DataSender();

        while(true) {
            String fileContent = "";
            String file = getNewestLogfile();

            if(file == null) {
                logFileError();
            }

            try {
                FileReader reader = new FileReader(file);
                BufferedReader br = new BufferedReader(reader);
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                fileContent = sb.toString();
                reader.close();
            } catch (Exception e) {
                logger.error("Could not read log-file", e);
            }

            int systemPosition = fileContent.lastIndexOf("System:");

            // no system data in log
            if(systemPosition == -1) {
                waitDelay();
                continue;
            }

            int systemNameStart = fileContent.indexOf("(", systemPosition);
            int systemNameEnd = fileContent.indexOf(")", systemPosition);

            String newSystemName = fileContent.substring(systemNameStart+1, systemNameEnd);
            if(!systemName.equalsIgnoreCase(newSystemName)) {
                logger.info("New Systemname: " + newSystemName);
                dataSender.sendData(newSystemName, commander);
            }

            systemName = newSystemName;

            waitDelay();
        }

    }

}
