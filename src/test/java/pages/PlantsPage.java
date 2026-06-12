package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import java.util.ArrayList;
import java.util.List;

public class PlantsPage {
    private Page page;

    // Locators
    private Locator searchInput;
    private Locator searchButton;
    private Locator table;
    private Locator tableRows; // Added for row iteration
    private Locator categoryDropdown;
    private Locator emptyStateCell;
    private Locator addPlantButton;
    private Locator nameColumnHeader;


    public PlantsPage(Page page) {
        this.page = page;
        this.searchInput = page.locator("input[placeholder='Search plant']");
        this.searchButton = page.locator("button:has-text('Search')");
        this.table = page.locator("table");
        this.tableRows = page.locator("table tbody tr"); // Added row locator
        this.categoryDropdown = page.locator("select");
        // Targets the specific <td> containing the empty state text
        this.emptyStateCell = page.locator("table tbody tr td.text-center.text-muted");
        this.addPlantButton = page.locator("a:has-text('Add a Plant')");
        this.nameColumnHeader = page.locator("th a:has-text('Name')");
    }

    public void navigateToPlantPage() {
        // Replace with your actual route
        page.navigate("http://localhost:8080/ui/plants");
    }

    public void enterPlantName(String plantName) {
        searchInput.fill(plantName);
    }

    public void clickSearch() {
        searchButton.click();
    }

    public Locator getTable() {
        return table;
    }

    public void selectCategory(String categoryName) {
        // Playwright handles dropdowns natively with selectOption
        categoryDropdown.selectOption(categoryName);
    }

    public List<String> getAllDisplayedCategories(int categoryColumnIndex) {
        List<String> categories = new ArrayList<>();
        List<Locator> rows = this.tableRows.all();
        //page.pause();
        for (Locator row : rows) {
            String category = row.locator("td:nth-child(" + categoryColumnIndex + ")").textContent().trim();
            categories.add(category);
        }

        return categories;
    }

    public boolean doesCategoryExist(String categoryName) {
        // Extract all text from the <option> tags inside the dropdown
        //page.pause();
        List<String> availableCategories = categoryDropdown.locator("option").allInnerTexts();
        return availableCategories.stream()
                .anyMatch(category -> category.trim().equals(categoryName.trim()));
    }

    public void clickNameColumnHeader() {
        nameColumnHeader.click();
        // Give the table a moment to re-render the sorted data
        page.waitForLoadState();
    }

    public List<String> getAllDisplayedPlantNames() {
        List<String> names = new ArrayList<>();
        List<Locator> rows = this.tableRows.all();

        for (Locator row : rows) {
            String name = row.locator("td:nth-child(1)").innerText().trim();
            if (!name.isEmpty() && !name.equalsIgnoreCase("No plants found")) {
                names.add(name);
            }
        }
        return names;
    }

    public boolean hasLowStockRecords(int stockColumnIndex, int threshold) {
        List<Locator> rows = this.tableRows.all();
        for (Locator row : rows) {
            String cellText = row.locator("td:nth-child(" + stockColumnIndex + ")").textContent().trim();
            // Extract only the numbers from the cell text
            String numberOnly = cellText.replaceAll("[^0-9]", "");
            if (!numberOnly.isEmpty()) {
                int quantity = Integer.parseInt(numberOnly);
                if (quantity < threshold) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Verifies that every row with a stock quantity below the threshold contains the required badge.
     */
    public boolean verifyLowBadgeForLowStock(int stockColumnIndex, String badgeText, int threshold) {
        List<Locator> rows = this.tableRows.all();
        boolean foundLowStock = false;

        for (Locator row : rows) {
            Locator stockCell = row.locator("td:nth-child(" + stockColumnIndex + ")");

            // Use innerText() to get visible text, avoiding hidden DOM artifacts
            String cellText = stockCell.innerText().trim();
            if (cellText.isEmpty()) continue;

            // Extract the first sequence of numbers to get the quantity safely
            // (e.g., splits "3 Low" and just grabs "3")
            String[] parts = cellText.split("\\D+");

            if (parts.length > 0 && !parts[0].isEmpty()) {
                int quantity = Integer.parseInt(parts[0]);

                // If stock is below the threshold (e.g., < 5)
                if (quantity < threshold) {
                    foundLowStock = true;

                    // Specifically target the span with the 'badge' class shown in your DOM
                    Locator badgeSpan = stockCell.locator("span.badge");

                    // 1. Fail if the badge element is missing entirely
                    if (badgeSpan.count() == 0) {
                        return false;
                    }
                    // 2. Fail if the badge text doesn't match what we expect (case-insensitive)
                    String actualBadgeText = badgeSpan.innerText().trim();
                    if (!actualBadgeText.equalsIgnoreCase(badgeText)) {
                        return false;
                    }
                }
            }
        }
        return foundLowStock;
    }

    /**
     * Retrieves the text from the empty state row in the table.
     * Returns null if the element is not found or visible.
     */
    public String getEmptyTableMessage() {
        if (emptyStateCell.isVisible()) {
            return emptyStateCell.innerText().trim();
        }
        return "No plants found";
    }

    public boolean isAddPlantButtonVisible() {
        return addPlantButton.isVisible();
    }

    public void clickAddPlantButton() {
        addPlantButton.click();
    }

    public boolean isAddPlantPageDisplayed() {
        try {
            // waitForURL ensures Playwright waits for the navigation to complete before checking
            // We use a glob pattern (**) to handle full URLs (e.g., http://localhost:8080/ui/plants/add)
            page.waitForURL("**/ui/plants/add");
            return page.url().contains("/ui/plants/add");
        } catch (Exception e) {
            System.err.println("Timed out waiting for Add Plant page URL: " + e.getMessage());
            return false;
        }
    }

    public void deletePlantAndConfirm(String plantName) {
        // 1. Set up the dialog handler BEFORE clicking the button.
        // using 'onceDialog' ensures this listener is only used for the very next dialog that appears.
        page.onceDialog(dialog -> {
            // Automatically click "OK" on the browser confirm popup
            dialog.accept();
        });

        // 2. Find the row containing the specific plant
        Locator targetRow = this.tableRows.filter(new Locator.FilterOptions().setHasText(plantName));

        // 3. Find the delete button within that specific row and click it
        // The screenshot shows the button has the class btn-outline-danger
        Locator deleteButton = targetRow.locator("button.btn-outline-danger");
        deleteButton.click();
    }

    public boolean isPlantVisibleInTable(String plantName) {
        // Wait for the table to stabilize (e.g., after a page reload or DOM update following deletion)
        page.waitForLoadState();

        Locator matchingRow = this.tableRows.filter(new Locator.FilterOptions().setHasText(plantName));
        return matchingRow.count() > 0;
    }

    /**
     * Clicks the Edit button (pencil icon) for a specific plant row
     */
    public void clickEditButtonForPlant(String plantName) {
        Locator targetRow = this.tableRows.filter(new Locator.FilterOptions().setHasText(plantName));
        // Uses the title="Edit" attribute visible in the DOM screenshot
        Locator editButton = targetRow.locator("a[title='Edit']");
        editButton.click();
    }

    /**
     * Retrieves the category text for a specific plant by its name
     */
    public String getCategoryForPlant(String plantName) {
        // Wait for table to reload/stabilize after the redirect
        page.waitForLoadState();
        Locator targetRow = this.tableRows.filter(new Locator.FilterOptions().setHasText(plantName));
        // Category is the 2nd column based on your previous 'getAllDisplayedCategories(2)' implementation
        return targetRow.locator("td:nth-child(2)").innerText().trim();
    }

}