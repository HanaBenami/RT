package il.co.rtcohen.rt.utils;

import org.slf4j.LoggerFactory;

public class Logger {
    private Logger() {}

    public static org.slf4j.Logger getLogger(Object o) {
        return LoggerFactory.getLogger(o.getClass());
    }
}
