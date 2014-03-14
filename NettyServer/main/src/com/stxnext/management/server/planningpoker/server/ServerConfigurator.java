package com.stxnext.management.server.planningpoker.server;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class ServerConfigurator {

    private static ServerConfigurator _instance;
    private Logger logger;
    public static ServerConfigurator getInstance(){
        if(_instance == null){
            _instance = new ServerConfigurator();
        }
        return _instance;
    }
    
    public ServerConfigurator(){
        
    }
    
    public Logger getLogger() {
        if(logger == null){
            configureLogger();
        }
        return logger;
    }

    private boolean configureLogger(){
        boolean configOk = true;
        
        try{
            DOMConfigurator.configure("main/res/log4jconfig.xml");
        }
        catch(FactoryConfigurationError e){
            configOk = false;
            e.printStackTrace();
        }
        
        logger = Logger.getLogger("A1");
        
        return configOk;
    }
    
    public boolean configure(){
        boolean configOk = true;
        
        if(!configureLogger())
            configOk = false;
        
        return configOk;
    }
    
    
}
