package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.List;

public class SalesListPage {
    private final Page page;
    private final String baseUrl;

    public SalesListPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public void navigate() {
        page.navigate(baseUrl + "/ui/sales");
        page.waitForLoadState();
    }

    public boolean isDisplayed() {
        return page.url().contains("/ui/sales") && page.locator("h3:has-text('Sales')").isVisible();
    }

    public boolean hasSalesRows() {
        return page.locator("table tbody tr").count() > 0;
    }

    public boolean hasSortingLinks() {
        return page.locator("a[href*='sortField=plant.name']").count() > 0
                && page.locator("a[href*='sortField=quantity']").count() > 0
                && page.locator("a[href*='sortField=totalPrice']").count() > 0
                && page.locator("a[href*='sortField=soldAt']").count() > 0;
    }

    public boolean hasPaginationEvidence() {
        return page.locator("a[href*='page=']").count() > 0;
    }

    public boolean isSellPlantButtonVisible() {
        return page.locator("a[href='/ui/sales/new']").count() > 0
                && page.locator("a[href='/ui/sales/new']").isVisible();
    }

    public void clickSellPlant() {
        page.locator("a[href='/ui/sales/new']").click();
        page.waitForLoadState();
    }

    public boolean isSaleVisibleForPlant(String plantName) {
        return page.locator("table tbody tr").filter(new Locator.FilterOptions().setHasText(plantName)).count() > 0;
    }

    public boolean isDeleteActionVisibleForPlant(String plantName) {
        Locator row = page.locator("table tbody tr").filter(new Locator.FilterOptions().setHasText(plantName));
        return row.count() > 0 && row.locator("form[action^='/ui/sales/delete/'] button").count() > 0;
    }

    public boolean hasAnyDeleteAction() {
        return page.locator("form[action^='/ui/sales/delete/'] button").count() > 0;
    }

    public void deleteSaleForPlantAndDismiss(String plantName) {
        page.onceDialog(dialog -> dialog.dismiss());
        page.locator("table tbody tr")
                .filter(new Locator.FilterOptions().setHasText(plantName))
                .first()
                .locator("form[action^='/ui/sales/delete/'] button")
                .click();
        page.waitForLoadState();
    }

    public void deleteSaleForPlantAndAccept(String plantName) {
        page.onceDialog(dialog -> dialog.accept());
        page.locator("table tbody tr")
                .filter(new Locator.FilterOptions().setHasText(plantName))
                .first()
                .locator("form[action^='/ui/sales/delete/'] button")
                .click();
        page.waitForLoadState();
    }

    public List<String> visibleRows() {
        return page.locator("table tbody tr").allInnerTexts();
    }
}
