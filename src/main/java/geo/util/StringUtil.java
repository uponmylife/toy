package geo.util;

public class StringUtil {
    public static String onlyDigit(String str) {
        return str.replaceAll("[^0-9]", "");
    }
}
