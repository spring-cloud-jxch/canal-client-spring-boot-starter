package io.github.jxch.canal.client.util;

public class CanalStrings {

    public static String camelToSnake(String str) {
        return str.replaceAll("([A-Z])", "_$1").toLowerCase();
    }

    public static String snakeToCamel(String str) {
        StringBuilder result = new StringBuilder();
        boolean toUpper = false;
        for (char c : str.toCharArray()) {
            if (c == '_') {
                toUpper = true;
            } else {
                if (toUpper) {
                    result.append(Character.toUpperCase(c));
                    toUpper = false;
                } else {
                    result.append(c);
                }
            }
        }
        return result.toString();
    }

}
