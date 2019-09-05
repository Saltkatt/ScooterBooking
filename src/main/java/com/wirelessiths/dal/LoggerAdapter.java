package com.wirelessiths.dal;


import org.apache.logging.log4j.Logger;
//temp solution for testing locally
public class LoggerAdapter {

    private Logger logger;

    public void info(String message){
        if(logger != null){
            logger.info(message);
        }else{
            System.out.println("fake logger: " + message);
        }
    }

    public void error(String message){
        if(logger != null){
            logger.error(message);
        }else{
            System.out.println("fake logger error: " + message);
        }
    }

    public LoggerAdapter(){

    }

    public LoggerAdapter(Logger logger) {
        this.logger = logger;
    }
}
