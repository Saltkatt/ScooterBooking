package com.wirelessiths.test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * Hello world!
 *
 */
public class App {

    private final Logger logger = LogManager.getLogger(this.getClass());

    public void testLogger()  {
        logger.info("testing testing");
        logger.error("jadpgjeap");
    }

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        App app = new App();
        app.testLogger();
    }
}
