import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HeatMapClient {

    private static final Logger logger = LoggerFactory.getLogger(HeatMapClient.class);
    private static final String build = "3";

    public static void main(String[] args) throws Exception {

        logger.info("Heatmap v." + build);

        LogReader logReader = new LogReader();
        logReader.start();

        logger.info("Started...");
    }

}
