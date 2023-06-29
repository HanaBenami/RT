package il.co.rtcohen.rt.utils;

import il.co.rtcohen.rt.app.GeneralErrorHandler;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {
    private Logger() {}

    public static org.slf4j.Logger getLogger(Object o) {
        org.slf4j.Logger logger = LoggerFactory.getLogger(o.getClass());
        return logger;
    }

    public static void info(Object o, String str) {
        Logger.getLogger(o).info(str);
    }

    public static void exception(Object o, Exception e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        Logger.getLogger(o).error(stringWriter.toString());
    }
}
