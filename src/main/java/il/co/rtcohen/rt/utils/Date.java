package il.co.rtcohen.rt.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Date {
    final static public DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    final static public DateTimeFormatter dateFormatterForUrls = DateTimeFormatter.ofPattern("yyyyMMdd");
    final static public String nullDateString = "1901-01-01"; // TODO: change to private

    private final LocalDate localDate;

    public Date(LocalDate localDate) {
        this.localDate = localDate;
    }

    public Date(String s) {
        this.localDate = LocalDate.parse(s, dateFormatter);
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    @Override
    public String toString() {
        return localDateToString(this.getLocalDate());
    }

    static public String localDateToString(LocalDate localDate) {
        return (null == localDate ? nullDateString : localDate.format(dateFormatter));
    }

    static public Date nullDate() {
        return new Date(nullDateString);
    }

    public boolean equals(Date other) {
        return (null != other) && this.getLocalDate().equals(other.getLocalDate());
    }
}
