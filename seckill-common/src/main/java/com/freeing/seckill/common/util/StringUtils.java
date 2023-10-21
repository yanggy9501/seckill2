package com.freeing.seckill.common.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
    /**
     * 空字符串
     */
    private static final String EMPTY_STR = "";

    /**
     * 下划线
     */
    private static final char UNDERLINE = '_';

    /**
     * 判断一个字符串是否为非空串
     *
     * @param str String
     * @return true：非空串 false：空串
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 截取字符串
     *
     * @param str   字符串
     * @param start 开始
     * @return 结果
     */
    public static String substring(final String str, int start) {
        if (str == null) {
            return EMPTY_STR;
        }

        if (start < 0) {
            start = str.length() + start;
        }

        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return EMPTY_STR;
        }

        return str.substring(start);
    }

    /**
     * 判断不相等
     *
     * @param strA
     * @param strB
     * @return
     */
    public static boolean notEquals(String strA, String strB) {
        return !equals(strA, strB);
    }

    /**
     * 截取字符串
     * input: str="aaa.bbb", endStr='.'
     * output: "aaa"
     *
     * @param str    字符串
     * @param endChar 结束字符
     * @return 结果
     */
    public static String substring(final String str, char endChar) {
        return substring(str, 0, str.indexOf(endChar));
    }

    /**
     * 截取符合正则表达式的字符串
     *
     * @param str 字符串
     * @param regex 正则表达式
     * @return 结果
     */
    public static String substringByRegex(final String str, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group();
        }
        return EMPTY_STR;
    }

    /**
     * 截取字符串
     *
     * @param str   字符串
     * @param start 开始
     * @param end   结束
     * @return 结果
     */
    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return EMPTY_STR;
        }

        if (end < 0) {
            end = str.length() + end;
        }
        if (start < 0) {
            start = str.length() + start;
        }

        if (end > str.length()) {
            end = str.length();
        }

        if (start > end) {
            return EMPTY_STR;
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }
        return str.substring(start, end);
    }

    /**
     * 驼峰转下划线命名
     */
    public static String toUnderScoreCase(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        // 前置字符是否大写
        boolean preCharIsUpperCase;
        // 当前字符是否大写
        boolean curreCharIsUpperCase;
        // 下一字符是否大写
        boolean nextCharIsUpperCase = true;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (i > 0) {
                preCharIsUpperCase = Character.isUpperCase(str.charAt(i - 1));
            } else {
                preCharIsUpperCase = false;
            }

            curreCharIsUpperCase = Character.isUpperCase(c);

            if (i < (str.length() - 1)) {
                nextCharIsUpperCase = Character.isUpperCase(str.charAt(i + 1));
            }

            if (preCharIsUpperCase && curreCharIsUpperCase && !nextCharIsUpperCase) {
                sb.append(UNDERLINE);
            } else if ((i != 0 && !preCharIsUpperCase) && curreCharIsUpperCase) {
                sb.append(UNDERLINE);
            }
            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    /**
     * 是否包含字符串
     *
     * @param str  验证字符串
     * @param strs 字符串组
     * @return 包含返回true
     */
    public static boolean inStringIgnoreCase(String str, String... strs) {
        if (str != null && strs != null) {
            for (String s : strs) {
                if (str.equalsIgnoreCase(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean inString(String str, String... strs) {
        if (str != null && strs != null) {
            for (String s : strs) {
                if (str.equals(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean inStringIgnoreCase(String str, List<String> stringList) {
        if (stringList == null) {
            return false;
        }
        return inStringIgnoreCase(str, stringList.toArray(new String[0]));
    }

    public static boolean inString(String str, List<String> stringList) {
        if (stringList == null) {
            return false;
        }
        return inString(str, stringList.toArray(new String[0]));
    }

    /**
     * 将下划线大写方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。
     * 例如：HELLO_WORLD->HelloWorld
     *
     * @param str 转换前的下划线大写方式命名的字符串
     * @return 转换后的驼峰式命名的字符串
     */
    public static String convertToCamelCase(String str) {
        StringBuilder result = new StringBuilder();
        // 快速检查
        if (str == null || str.isEmpty()) {
            // 没必要转换
            return "";
        } else if (!str.contains(String.valueOf(UNDERLINE))) {
            // 不含下划线，仅将首字母大写
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        // 用下划线将原始字符串分割
        String[] camels = str.split("_");
        for (String camel : camels) {
            // 跳过原始字符串中开头、结尾的下换线或双重下划线
            if (camel.isEmpty()) {
                continue;
            }
            // 首字母大写
            result.append(camel.substring(0, 1).toUpperCase());
            result.append(camel.substring(1).toLowerCase());
        }
        return result.toString();
    }

    /**
     * 驼峰式命名法 例如：user_name -> userName
     */
    public static String toCamelCase(String str) {
        if (str == null) {
            return null;
        }
        str = str.toLowerCase();
        StringBuilder sb = new StringBuilder(str.length());
        boolean upperCase = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            if (c == UNDERLINE) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 安全比较两个字符串
     *
     * @param cs1
     * @param cs2
     * @return boolean
     */
    public static boolean safeEquals(final CharSequence cs1, final CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        if (cs1.length() != cs2.length()) {
            return false;
        }
        boolean result = true;
        // 遍历完在出结果，避免统计时长猜测结果
        for (int i = 0; i < cs1.length(); i++) {
            if (cs1.charAt(i) != cs2.charAt(i)) {
                result = false;
            }
        }
        return result;
    }

    /**
     * 将指定字符串 text 的 start-end的子字符串替换为 replacement
     * ps：包含结束位置
     *
     * @param text 字符串
     * @param replacement replacement
     * @param start 开始下标
     * @param end 结束下标
     * @param ignoreException 是否忽略异常
     * @return String
     */
    public static String replace(final String text, final String replacement, int start, int end, boolean ignoreException) {
        // 检查合法性
        if (text == null) {
            return null;
        }
        if (start > end || start < 0 || end >= text.length()) {
            if (ignoreException) {
                if (start < 0) {
                    start = 0;
                }
                if (end >= text.length()) {
                    end = text.length() - 1;
                }
            } else {
                throw new IndexOutOfBoundsException("Illegal index of " + text + " with " + start + "-" + end);
            }
        }
        return text.substring(0, start) + replacement + text.substring(end + 1);
    }
}
