package com.autohero.main.test.component;

import com.autohero.main.test.common.CustomWait;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.Assertion;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SearchPage {

    private final WebDriver driver;
    private final Logger logger = LogManager.getLogger();
    private int registerYearData;

    @FindBy(css = "div[data-qa-selector=\"filter-year\"]")
    private WebElement filterYearDivElement;

    @FindBy(css = "select[name=\"yearRange.min\"]")
    private WebElement yearRangeMinDropDownElement;

    @FindBy(css = "select[name=\"sort\"]")
    private WebElement sortingDropDownElement;

    @FindBy(css = "div[data-qa-selector=\"ad-items\"]")
    private WebElement adListItems;

    @FindBy(css = "ul[class=\"pagination\"]")
    private WebElement paginationSection;

    public SearchPage(WebDriver driver){
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    /**
     * This function gets an accordion type of element and clicks on it to expand.
     * @param accordion
     */
    private void extendOrCollapseAccordion(WebElement accordion)
    {
        CustomWait.waitUntilElementVisible(driver, accordion);
        logger.info("Expanding accordion " + accordion.getAttribute("data-qa-selector"));
        accordion.click();
    }

    /**
     *  Extends the First Registration Date accordion. yearRange.min (Erstzulassung ab)
     */
    public void extendFirstRegistration(){
        extendOrCollapseAccordion(filterYearDivElement);
    }

    /**
     * This method gets an WebElement as drop down menu clicks on it, selects an value by data selector
     * clicks again to collapse drop down menu
     * @param dropDownMenu drop down menu as WebElement
     * @param dataSelector data-qa-selector-value as string.
     */
    private void selectSingleFromDropDown(WebElement dropDownMenu, String dataSelector)
    {
        CustomWait.waitUntilElementVisible(driver, dropDownMenu);
        logger.info("Dropdown selection in progress...");
        dropDownMenu.click();
        WebElement optionElement = driver.findElement(By.cssSelector("[data-qa-selector-value=\"" + dataSelector + "\"]"));
        optionElement.click();
        dropDownMenu.click();
        logger.info("Dropdown option has been set to: " + optionElement.getText());
    }

    /**
     * Selects the registration date from drop down list by selectSingleFromDropDown method.
     * @param data desired option's string value of data-qa-selector-value
     */
    public void selectRegistrationYear(String data)
    {
        selectSingleFromDropDown(yearRangeMinDropDownElement,data);
        registerYearData = Integer.parseInt(data) + 1; //Since there is a bug with data-qa-selector values I have added 1 here to make it work properly.
    }

    /**
     * Selects the sorting type from drop down list by selectSingleFromDropDown method.
     * @param data desired option's string value of data-qa-selector-value
     */
    public void selectSortingType(String data)
    {
        selectSingleFromDropDown(sortingDropDownElement, data);
    }

    /**
     * Validates the if price sorting ascending and first register year. This method returns false if any of test condition fails.
     * tempLastPrice: Since it is page iterated holds the last ad item's value (designed initially to compare the last price of page N and first price of page N+1).
     * pageIndexForLoggingPurposes: Holds the page number.
     * @return Returns "false" for any failure, returns "true" for successful completion.
     */
    public boolean validateAdItems()
    {
        double tempLastPrice = -1;
        int pageIndexForLoggingPurposes = 1;
        do {
            logger.info("Processing page: " + pageIndexForLoggingPurposes++);
            if(getRegisterDateListAndValidate(adListItems.findElements(By.cssSelector("ul[data-qa-selector=\"spec-list\"]"))) == false)
                return false;
            tempLastPrice = checkPriceIfDescending(
                    adListItems.findElements(By.cssSelector("div[data-qa-selector=\"price\"]"))
                    ,tempLastPrice);
            if(tempLastPrice == -2) {
                return false;
            }
        }
        while(searchGetNextPageIfExists());
        return true;
    }

    /**
     * Gets the price list for each page and checks if price descending by each element.
     * @param prices Single Page's ad prices list as List<WebElement>
     * @param lastPrice Previous page's last price value which has been hold in validateAdItems.
     * @return Return "true" for successful validation for single page's ad prices, returns "false" if any ascend sorting failure.
     */
    private double checkPriceIfDescending(List<WebElement> prices, double lastPrice)
    {
        for(int i = 0; i < prices.size(); i++)
        {
            double newElementsPriceToCompare = Double.parseDouble(prices.get(i).getText().split(" ")[0].replace(".",""));
            if(lastPrice != -1)
            {
                if(lastPrice < newElementsPriceToCompare)
                {
                    logger.error("Price descending failure detected.");
                    return -2; //To pass function as failed.
                }
                else
                    lastPrice = newElementsPriceToCompare; //New element's price will be last element to compare in next iteration
            }
            else
                lastPrice = newElementsPriceToCompare;
        }
        return lastPrice;
    }

    /**
     *
     * @param listOfSpecsList Single Page's specs list as List<WebElement> which has been taken under data-qa-selector="specs"
     * @return Return "true" for successful validation for single page's year specs, returns "false" if any year spec under filtered value.
     */
    private boolean getRegisterDateListAndValidate(List<WebElement> listOfSpecsList)
    {
        for(int i = 0; i < listOfSpecsList.size(); i++)
        {
            String firstRegistrationDate = listOfSpecsList.get(i).findElements(By.cssSelector("li[data-qa-selector=\"spec\"]")).get(0).getText().split("/")[1];
            int firstRegDateAsDouble = Integer.parseInt(firstRegistrationDate);
            if(firstRegDateAsDouble < registerYearData) {
                logger.error("Register year of ad item does not match with the filtered value.");
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if Next Page button is enabled. If enabled scrolls to the end of page and clicks to the Next Page button.
     * @return returns true if successfully next page button clicked, returns false if next page button is disabled.
     */
    private boolean searchGetNextPageIfExists()
    {
        List<WebElement> paginationElements = paginationSection.findElements(By.cssSelector("li"));
        WebElement paginationNextButton = paginationElements.get(paginationElements.size() - 2);
        if(paginationNextButton.getAttribute("class").equals("disabled"))
            return false;
        else {
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, document.body.scrollHeight)");
            paginationNextButton.findElement(By.cssSelector("a")).click();
            CustomWait.waitUntilElementIsStale(driver, driver.findElement(By.cssSelector("div[data-qa-selector=\"title\"]")));
            return true;
        }
    }


}
