package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;

public class EditPlantPage {
    private Page page;

    // Locators
    private Locator categoryDropdown;
    private Locator saveButton;

    public EditPlantPage(Page page) {
        this.page = page;
        // Using the id visible in the DOM screenshot
        this.categoryDropdown = page.locator("select#categoryId");
        this.saveButton = page.locator("button:has-text('Save')");
    }

    public void selectCategory(String categoryName) {
        // Select by the visible label text
        categoryDropdown.selectOption(new SelectOption().setLabel(categoryName));
    }

    public void clickSave() {
        saveButton.click();
    }
}