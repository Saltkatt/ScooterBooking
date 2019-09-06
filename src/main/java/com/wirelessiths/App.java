package com.wirelessiths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;


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

    public static void main( String[] args ) throws URISyntaxException, IOException {
        System.out.println( "Hello World!" );
        App app = new App();
        app.testLogger();


    }
}
