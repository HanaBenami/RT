package il.co.rtcohen.rt.utils;

import org.slf4j.LoggerFactory;

public class Logger {
    private Logger() {}

    public static org.slf4j.Logger getLogger(Object o) {
        org.slf4j.Logger logger = LoggerFactory.getLogger(o.getClass());
        return logger;
    }
}
