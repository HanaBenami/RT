package il.co.rtcohen.rt.utils;

import il.co.rtcohen.rt.dal.bl.hashavshevet.HashavshevetSyncSingleRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private StringUtils() {
    }

    public static boolean areEquals(String a, String b) {
        return (isEmpty(a) && (isEmpty(b))) || (!isEmpty(a) && !isEmpty(b) && removeSpaces(a).equals(removeSpaces(b)));
    }

    public static boolean isEmpty(String str) {
        return (null == str) || (removeSpaces(str).isEmpty());
    }

    public static String removeSpaces(String str) {
        return str.replaceAll("\\s", "");
    }

    public static String reduceSpaces(String str) {
        return (null == str ? null : str.replaceAll("^\\s+|\\s+$", "").replaceAll("\\s+", " "));
    }

    public static String removeNumbers(String str) {
        return (null == str ? null : reduceSpaces(str.replaceAll("\\d+", "")));
    }

    public static Pair<String, List<String>> extractNumbersWithDashes(String fullStr, Integer maxNumbersToExtract) {
        ArrayList<String> matches = new ArrayList<>();
        String remainStr = fullStr;
        if (null != fullStr) {
            String regex = "(\\d+-\\d+)(-\\d+)*";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(fullStr);
            while (matcher.find() && (null == maxNumbersToExtract || 0 < maxNumbersToExtract)) {
                String match = matcher.group();
                matches.add(match);
                remainStr = remainStr.replace(match, "");
                if (null != maxNumbersToExtract) {
                    maxNumbersToExtract--;
                }
            }
            remainStr = reduceSpaces(remainStr);
        }
        return new Pair<>(remainStr, matches);
    }
}
