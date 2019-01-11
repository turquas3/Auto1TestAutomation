package com.autohero.main.test.base;

import com.autohero.main.test.common.OSCommon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public abstract class BaseTest {

    /**
     * Logger Object
     */
    private static final Logger logger = LogManager.getLogger();

    protected static WebDriver driver;

    /**
     * Initialization of browser depending on operating system.
     */
    @BeforeTest
    public void initializeBrowser() {
        logger.info("Initializing browser..");
        if (OSCommon.isUnix())
            System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/linux/chromedriver");
        else if (OSCommon.isWindows())
            System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/windows/chromedriver.exe");
        else if (OSCommon.isMac())
            System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/mac/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("disable-infobars");
        //download settings for Chrome
        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();

        chromePrefs.put("profile.default_content_settings.popups", 0);
        options.setExperimentalOption("prefs", chromePrefs);
        driver = new ChromeDriver(options);
        if (OSCommon.isMac()) {
            driver.manage().window().setPosition(new Point(-1000, 1));
            driver.manage().window().fullscreen();
        } else
            driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @AfterTest(alwaysRun = true)
    public void closeDriver() {

        logger.info("Finalizing tests.");

        if (driver != null) {
            driver.close();
            driver.quit();
            logger.info("Driver is closed");
        }
    }
}
