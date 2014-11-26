import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: filth
 * Date: 26.11.14
 * Time: 20:45
 * To change this template use File | Settings | File Templates.
 */
public class LogReader extends Thread {

    private String systemName = "";

    private HttpTools httpTools = new HttpTools();

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

    public void run()  {

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
                e.printStackTrace();
            }

            int systemPosition = fileContent.lastIndexOf("System:");
            int systemNameStart = fileContent.indexOf("(", systemPosition);
            int systemNameEnd = fileContent.indexOf(")", systemPosition);

            String newSystemName = fileContent.substring(systemNameStart+1, systemNameEnd);
            if(!systemName.equalsIgnoreCase(newSystemName)) {
               // System.out.println("send data!");
                try {
                    httpTools.sendPost(newSystemName);
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

            systemName = newSystemName;

            try {
                sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
