package com.dwp;

/**
 * 获取当前操作系统类型工具类
 *
 * @author denngweiping
 * 2020-04-01
 */
public class OSUtil {

    private static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isMac() {
        return OS.indexOf("mac") != -1;
    }

    public static boolean isWindows() {
        return OS.indexOf("win") != -1;
    }

    public static boolean isLinux() {
        return OS.indexOf("linux") != -1 || OS.indexOf("unix") != -1 || OS.indexOf("centos") != -1;
    }
}
