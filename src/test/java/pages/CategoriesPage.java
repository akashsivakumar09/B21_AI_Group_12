package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import java.util.List;
import java.util.ArrayList;

public class CategoriesPage {
    private Page page;

    // Locators
    private Locator addCategoryButton;
    private Locator nameInput;
    private Locator parentSelect;
    private Locator saveButton;
    private Locator cancelButton;
    private Locator categoriesTableRows;
    private Locator successMessage;
    private Locator nameError;

    public CategoriesPage(Page page) {
        this.page = page;
        this.addCategoryButton = page.locator("a:has-text('Add A Category'), a:has-text('Add Category')");
        this.nameInput = page.locator("input[name='name']");
        this.parentSelect = page.locator("select[name='parentId']");
        this.saveButton = page.locator("button[type='submit'], button:has-text('Save')");
        this.cancelButton = page.locator("a:has-text('Cancel'), a:has-text('Back')");
        this.categoriesTableRows = page.locator("table tbody tr");
        this.successMessage = page.locator(".alert-success, .success-message");
        this.nameError = page.locator("input[name='name'] ~ .invalid-feedback, .error-message");
    }

    public void navigateToCategories() {
        page.navigate("http://localhost:8080/ui/categories");
    }

    public boolean isAddCategoryButtonVisible() {
        return addCategoryButton.isVisible();
    }

    public void clickAddCategoryButton() {
        addCategoryButton.click();
    }

    public void enterCategoryName(String name) {
        nameInput.fill(name);
    }

    public void selectParentCategory(String parentName) {
        parentSelect.selectOption(new com.microsoft.playwright.options.SelectOption().setLabel(parentName));
    }

    public void clickSave() {
        saveButton.click();
    }

    public void clickCancel() {
        cancelButton.click();
    }

    public boolean isSuccessMessageVisible() {
        return successMessage.isVisible();
    }

    public String getSuccessMessageText() {
        return successMessage.innerText().trim();
    }

    public boolean isNameErrorVisible() {
        return nameError.isVisible();
    }

    public String getNameErrorText() {
        return nameError.innerText().trim();
    }

    public boolean isCategoryVisibleInTable(String categoryName) {
        page.waitForLoadState();
        Locator matchingRow = categoriesTableRows.filter(new Locator.FilterOptions().setHasText(categoryName));
        return matchingRow.count() > 0;
    }

    public List<String> getParentSelectOptions() {
        return parentSelect.locator("option").allInnerTexts();
    }
}
