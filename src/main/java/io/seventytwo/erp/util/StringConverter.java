package io.seventytwo.erp.util;

public class StringConverter {

    private StringConverter() {
    }

    public static String snakeToCamelCase(String input) {
        if (input == null) {
            return null;
        }
        String lowerCaseString = input.toLowerCase();
        if (lowerCaseString.contains("_")) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (String s : lowerCaseString.split("_")) {
                if (first) {
                    sb.append(s.charAt(0));
                    first = false;
                } else {
                    sb.append(Character.toUpperCase(s.charAt(0)));
                }
                if (s.length() > 1) {
                    sb.append(s.substring(1).toLowerCase());
                }
            }
            return sb.toString();
        } else {
            return lowerCaseString;
        }
    }

    public static String camelToSnakeCase(String input) {
        if (input == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (Character.isUpperCase(c)) {
                sb.append("_").append(c);
            } else {
                sb.append(c);
            }
        }
        return sb.toString().toUpperCase();
    }

}
