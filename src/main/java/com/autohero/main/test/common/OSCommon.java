package com.autohero.main.test.common;

import org.openqa.selenium.WebDriver;

public class OSCommon {

    private static String OS = System.getProperty("os.name").toLowerCase();

    //system detecting functions
    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (OS.indexOf("nux") >= 0);
    }

}
