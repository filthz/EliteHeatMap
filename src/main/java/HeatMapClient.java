import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: filth
 * Date: 26.11.14
 * Time: 20:52
 * To change this template use File | Settings | File Templates.
 */
public class HeatMapClient {

    private static final Logger logger = LoggerFactory.getLogger(HeatMapClient.class);

    public static void main(String[] args) throws Exception {

        LogReader logReader = new LogReader();
        logReader.start();

        logger.info("Started...");
    }

}
