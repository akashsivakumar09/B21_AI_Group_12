package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;

public class AddPlantPage {
    private Page page;

    private Locator quantityInput;
    private Locator saveButton;
    private Locator cancelButton;
    private Locator quantityError;
    private Locator validationErrors;

    public AddPlantPage(Page page) {
        this.page = page;
        this.quantityInput = page.locator("input[name='quantity']");
        this.saveButton = page.locator("button[type='submit'], button:has-text('Save')");
        this.cancelButton = page.locator("a:has-text('Cancel'), a:has-text('Back')");
        this.quantityError = page.locator("input[name='quantity'] ~ .invalid-feedback, .error-message");
        this.validationErrors = page.locator(".invalid-feedback, .error-message");
    }

    public void enterQuantity(String quantity) {
        quantityInput.fill(quantity);
    }

    public void clickSave() {
        saveButton.click();
    }

    public void clickCancel() {
        cancelButton.click();
    }

    public boolean areValidationMessagesDisplayed() {
        return validationErrors.first().isVisible();
    }

    public String getQuantityErrorMessage() {
        return quantityError.innerText().trim();
    }
}
