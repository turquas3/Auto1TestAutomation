package com.autohero.main.test.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CustomWait {

    private final static Logger logger = LogManager.getLogger();
    private static int TIMEOUT_WAIT_SECONDS = 10;

    public static void waitUntilElementVisible(WebDriver driver, WebElement element)
    {
        try
        {
            WebElement myDynamicElement = (new WebDriverWait(driver, TIMEOUT_WAIT_SECONDS))
                    .until(ExpectedConditions.visibilityOf(element));
        }
        catch(TimeoutException e)
        {
            logger.error(e.getMessage());
            throw e;
        }
    }

    public static void waitUntilElementIsStale(WebDriver driver, WebElement element)
    {
        try
        {
            new WebDriverWait(driver, TIMEOUT_WAIT_SECONDS).until(ExpectedConditions.stalenessOf(element));
        }
        catch(TimeoutException e)
        {
            logger.error(e.getMessage());
            throw e;
        }
    }
}
