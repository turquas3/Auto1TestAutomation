package tests;

import com.autohero.main.test.base.BaseTest;
import com.autohero.main.test.component.SearchPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SearchTest extends BaseTest {

    @Test
    public void search2015validateSortResults()
    {
        driver.get("https://www.autohero.com/de/search/");
        SearchPage searchPage = new SearchPage(driver);
        searchPage.extendFirstRegistration();
        searchPage.selectRegistrationYear("2014"); //Found a bug here. data-qa-selection has different id's than visible text. data-qa-selection="2014" pointing 2015
        searchPage.selectSortingType("offerPrice.amountMinorUnits.desc"); // I assume this is caused by adding 2019 to dropdown list.
        Assert.assertTrue(searchPage.validateAdItems());
    }

}
