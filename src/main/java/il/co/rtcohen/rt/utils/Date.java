package il.co.rtcohen.rt.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Date  implements Comparable<Date> {
    final static public DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    final static public DateTimeFormatter dateFormatterForUrls = DateTimeFormatter.ofPattern("yyyyMMdd");
    final static public DateTimeFormatter shortDateFormatter = DateTimeFormatter.ofPattern("dd/MM");
    final static private String nullDateString = "1901-01-01";

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

    public String toShortString() {
        LocalDate localDate = this.getLocalDate();
        return (null == localDate ? "" : localDate.format(shortDateFormatter));
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

    @Override
    public int compareTo(Date other) {
        return this.getLocalDate().compareTo(other.getLocalDate());
    }
}
