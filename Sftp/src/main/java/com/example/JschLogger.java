package com.example;

import org.apache.log4j.Logger;

public class JschLogger implements com.jcraft.jsch.Logger {
    private Logger logger;

    public JschLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public boolean isEnabled(int pLevel) {
        return true; // here, all levels enabled
    }

    @Override
    public void log(int pLevel, String pMessage) {
        logger.debug(pMessage);
    }
}
