import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: filth
 * Date: 28.11.14
 * Time: 14:00
 * To change this template use File | Settings | File Templates.
 */
public class DataSender {

    private final String apiUrl = "http://010102.de/api/addvisit/";
    private static final Logger logger = LoggerFactory.getLogger(DataSender.class);


    public void sendData(String systemName, String commander) {
        HttpTools httpTools = new HttpTools();
        String command = "system=" + systemName;
        if(commander != null) {
            command = command + "&commander=" +commander;
        }

        try {
            httpTools.sendPost(apiUrl, command);
        }
        catch (Exception e) {
            logger.error("failed to send data!");
        }

    }
}
