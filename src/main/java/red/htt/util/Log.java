package red.htt.util;


import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author yui
 */
public final class Log {

    private static final Logger LOGGER = Logger.getLogger("print by su");

    public static void log(Object o) {
        System.out.println(o);
    }

    public static void mark() {
        System.out.println("============================================================================");
        System.out.println("============================================================================");
        System.out.println("============================================================================");
    }

    public static void mark(int lines) {
        for (int i = 0; i < lines; i++) {
            System.out.println("============================================================================");
        }
    }


    public static void println(Object o) {
        System.out.println(o);
    }

    public static void println(char c) {
        System.out.println(c);
    }

    public static void print(Object o) {
        System.out.print(o);
    }

    public static void print(char c) {
        System.out.print(c);
    }

    public static void println() {
        System.out.println();
    }

    public static void println(Object... args) {
        StringBuilder sb = new StringBuilder();
        for (Object arg : args) {
            sb.append(arg).append(", ");
        }
        System.out.println(sb.substring(0, sb.length() - 2));
    }

    @Deprecated
    private static void println(String sep, Object... args) {
        StringBuilder sb = new StringBuilder();
        for (Object arg : args) {
            sb.append(arg).append(sep);
        }
        System.out.println(sb.substring(0, sb.length() - 2));
    }


    /**
     * %s  字符串类型  "mingrisoft" <br>
     * %c  字符类型 'm' <br>
     * %b  布尔类型 true <br>
     * %d  整数类型（十进制） 99 <br>
     * %x  整数类型（十六进制） FF <br>
     * %o  整数类型（八进制） 77 <br>
     * %f  浮点类型 99.99 <br>
     * %a  十六进制浮点类型 FF.35AE <br>
     * %e  指数类型 9.38e+5 <br>
     * %g  通用浮点类型（f和e类型中较短的） <br>
     * %h  散列码 <br>
     * %%  百分比类型 <br>
     * %n 换行符 <br>
     * %tx 日期与时间类型（x代表不同的日期与时间转换符
     */
    public static void printf(String format, Object... args) {
        System.out.printf(format, args);
    }

    public static void info(Object o) {
        if (o == null) {
            LOGGER.info("null");
        } else {
            LOGGER.info(o.toString());
        }
    }

    public static void warning(Object o) {
        if (o == null) {
            LOGGER.log(Level.WARNING, "null");
        } else {
            LOGGER.log(Level.WARNING, o.toString());
        }
    }


    public static void printArray(Object[] array, String sep) {
        sep = sep == null ? ", " : sep;
        StringBuilder sb = new StringBuilder();
        for (Object o : array) {
            sb.append(o.toString()).append(sep);
        }
        String string = sb.toString();
        if (string.endsWith(sep)) {
            Log.println(string.substring(0, string.length() - 2));
        }
    }

    public static void printArray(Object[] array) {
        printArray(array, ", ");
    }

    public static void printArray(int[] array) {
        for (int i : array) {
            System.out.println(i);
        }
    }

    /**
     * 打印集合类型
     *
     * @param collection 集合
     * @param sep        分隔符
     */
    public static <T> void printCollection(Collection<T> collection, String sep) {
        if (collection == null) {
            Log.println();
            return;
        }
        sep = sep == null ? ", " : sep;
        Iterator<T> iterator = collection.iterator();
        StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            sb.append(iterator.next().toString()).append(sep);
        }
        String string = sb.toString();
        if (string.endsWith(sep)) {
            Log.println(string.substring(0, string.length() - 2));
        }
    }

    /**
     * 打印集合类型, 使用默认分隔符: ", "
     *
     * @param collection 集合
     */
    public static <T> void printCollection(Collection<T> collection) {
        Log.printCollection(collection, ", ");
    }

    /**
     * 打印map, 使用默认分隔符: ", "
     */
    public static void printMap(Map map) {
        if (map == null) {
            Log.println();
            return;
        }
        StringBuilder sb = new StringBuilder("{");
        for (Object key : map.keySet()) {
            sb.append(key).append(" : ").append(map.get(key)).append(", ");
        }
        String string = sb.toString();
        if (string.endsWith(", ")) {
            string = string.substring(0, string.length() - 2);
        }
        Log.println(string + "}");
    }

}
