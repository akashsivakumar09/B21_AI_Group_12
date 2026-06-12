package pages;

import com.microsoft.playwright.Page;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SellPlantPage {
    private final Page page;
    private final String baseUrl;

    public SellPlantPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public void navigate() {
        page.navigate(baseUrl + "/ui/sales/new");
        page.waitForLoadState();
    }

    public boolean isDisplayed() {
        return page.url().contains("/ui/sales/new") && page.locator("h3:has-text('Sell Plant')").isVisible();
    }

    public List<String> plantOptions() {
        return page.locator("select[name='plantId'] option").allInnerTexts();
    }

    public boolean dropdownContainsStockValues() {
        return plantOptions().stream().anyMatch(text -> text.contains("Stock:"));
    }

    public void selectPlant(int plantId) {
        page.locator("select[name='plantId']").selectOption(String.valueOf(plantId));
    }

    public void clearPlantSelection() {
        page.locator("select[name='plantId']").selectOption("");
    }

    public void enterQuantity(String quantity) {
        page.locator("input[name='quantity']").fill(quantity);
    }

    public void clickSell() {
        page.locator("button:has-text('Sell')").click();
        page.waitForLoadState();
    }

    public void clickCancel() {
        page.locator("a:has-text('Cancel')").click();
        page.waitForLoadState();
    }

    public boolean hasMessage(String message) {
        return page.locator("body").innerText().contains(message);
    }

    public boolean isQuantityInputValid() {
        return (Boolean) page.locator("input[name='quantity']").evaluate("element => element.checkValidity()");
    }

    public String quantityValidationMessage() {
        return (String) page.locator("input[name='quantity']").evaluate("element => element.validationMessage");
    }

    public int stockForPlant(int plantId) {
        String optionText = page.locator("select[name='plantId'] option[value='" + plantId + "']").innerText();
        Matcher matcher = Pattern.compile("Stock: (\\d+)").matcher(optionText);
        if (!matcher.find()) {
            throw new IllegalStateException("Stock value not found in option text: " + optionText);
        }
        return Integer.parseInt(matcher.group(1));
    }
}
