package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;

public class AddPlantPage {
    private Page page;
    // Locators
    private Locator saveButton;
    private Locator errorMessages;
    private Locator quantityInput;
    private Locator quantityErrorMessage;
    private Locator cancelButton;

    public AddPlantPage(Page page) {
        this.page = page;
        // Locates the Save button by text
        this.saveButton = page.locator("button:has-text('Save')");
        // standard Bootstrap error classes. Adjust if your app uses a different class for errors (e.g., ".error-msg")
        this.errorMessages = page.locator(".text-danger");
        this.quantityInput = page.locator("input#quantity");
        this.quantityErrorMessage = page.locator("input#quantity ~ .text-danger");
        this.cancelButton = page.locator("a:has-text('Cancel')");

    }
    /**
     * Clicks the Save button on the Add Plant form.
     */
    public void clickSave() {
        saveButton.click();
    }
    /**
     * Checks if any validation error messages are displayed on the page.
     */
    public boolean areValidationMessagesDisplayed() {
        // Wait a brief moment for validation messages to appear in the DOM
        page.waitForLoadState();

        // Returns true if at least one error message is found and is visible
        return errorMessages.count() > 0 && errorMessages.nth(0).isVisible();
    }

    public void enterQuantity(String quantity) {
        quantityInput.fill(quantity);
    }

    public String getQuantityErrorMessage() {
        page.waitForLoadState();
        if (quantityErrorMessage.isVisible()) {
            return quantityErrorMessage.innerText().trim();
        }
        return null;
    }

    public void clickCancel() {
        cancelButton.click();
    }
}