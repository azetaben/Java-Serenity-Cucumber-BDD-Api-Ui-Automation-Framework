package saucedemo.com.actions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.junit.jupiter.api.Assertions;
import saucedemo.com.enums.Product;
import saucedemo.com.pages.CheckoutOverviewPage;
import saucedemo.com.pages.components.OrderItems;
import saucedemo.com.properties.ApplicationConfig;
import saucedemo.com.support.FrameworkLogger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public class CheckoutOverviewActions extends UIInteractionSteps {
    @Steps
    private CheckoutOverviewPage checkoutOverviewPage;

    public OrderItems items() {
        return new OrderItems($(checkoutOverviewPage.getCartList()));
    }

    @Step("Verify the checkout overview contains the following products: {0}")
    public void shouldContainProducts(List<Map<String, String>> rows) {
        FrameworkLogger.actionStarted(CheckoutOverviewActions.class, "Verify the checkout overview contains the following products");
        Assertions.assertEquals(rows.size(), items().count());
        for (Map<String, String> row : rows) {
            String title = row.containsKey("name") ? row.get("name") : row.get("product");
            Product product = Product.from(title);
            var actual = items().read(product.getId());
            Assertions.assertEquals(title, actual.name(), "Order item name");
            if (row.containsKey("quantity")) {
                Assertions.assertEquals(Integer.parseInt(row.get("quantity")), actual.quantity(), "Order item quantity");
            }
            String priceKey = row.containsKey("unitPrice") ? "unitPrice" : "price";
            if (row.containsKey(priceKey)) {
                BigDecimal expectedPrice = saucedemo.com.assertions.MoneyAssertions.parseDollarAmount(row.get(priceKey));
                Assertions.assertEquals(0, expectedPrice.compareTo(actual.unitPrice()), "Order item unit price");
            }
        }
    }

    @Step("Verify the checkout overview has subtotal: {0}")
    public void should_have_subtotal(BigDecimal expected) {
        FrameworkLogger.actionStarted(CheckoutOverviewActions.class, "Verify the checkout overview has subtotal: " + expected);
        Assertions.assertEquals(0, items().subtotal().compareTo(expected));
    }

    @Step("Verify the checkout overview has subtotal matching items")
    public void should_have_subtotal_matching_items() {
        FrameworkLogger.actionStarted(CheckoutOverviewActions.class, "Verify the checkout overview has subtotal matching items");
        Assertions.assertEquals(0, items().subtotal().compareTo(displayedAmount(checkoutOverviewPage.getSubtotalLabel())));
    }

    @Step("Verify the checkout overview has tax at configured rate")
    public void should_have_tax_at_configured_rate() {
        FrameworkLogger.actionStarted(CheckoutOverviewActions.class, "Verify the checkout overview has tax at configured rate");
        BigDecimal expected = items().subtotal().multiply(new BigDecimal(ApplicationConfig.value("app.checkout.taxRate", "0.08"))).setScale(2, RoundingMode.HALF_UP);
        Assertions.assertEquals(0, expected.compareTo(displayedAmount(checkoutOverviewPage.getTaxLabel())));
    }

    @Step("Verify the checkout overview has consistent total")
    public void should_have_consistent_total() {
        FrameworkLogger.actionStarted(CheckoutOverviewActions.class, "Verify the checkout overview has consistent total");
        BigDecimal expected = displayedAmount(checkoutOverviewPage.getSubtotalLabel()).add(displayedAmount(checkoutOverviewPage.getTaxLabel()));
        Assertions.assertEquals(0, expected.compareTo(displayedAmount(checkoutOverviewPage.getTotalLabel())));
    }

    @Step("Finish the checkout process")
    public void finish_checkout() {
        FrameworkLogger.actionStarted(CheckoutOverviewActions.class, "Finish the checkout process");
        $(checkoutOverviewPage.getFinishButton()).waitUntilClickable().click();
    }

    @Step("Cancel the checkout process")
    public void cancel_checkout() {
        FrameworkLogger.actionStarted(CheckoutOverviewActions.class, "Cancel the checkout process");
        $(checkoutOverviewPage.getCancelButton()).waitUntilClickable().click();
    }

    @Step("Verify the checkout overview has total matching subtotal and tax")
    public void should_have_total_matching_subtotal_and_tax() {
        FrameworkLogger.actionStarted(CheckoutOverviewActions.class, "Verify the checkout overview has total matching subtotal and tax");
        BigDecimal expected = displayedAmount(checkoutOverviewPage.getSubtotalLabel()).add(displayedAmount(checkoutOverviewPage.getTaxLabel()));
        Assertions.assertEquals(0, expected.compareTo(displayedAmount(checkoutOverviewPage.getTotalLabel())));
    }

    @Step("Verify the checkout overview has total matching items and tax")
    public void should_have_total_matching_items_and_tax() {
        FrameworkLogger.actionStarted(CheckoutOverviewActions.class, "Verify the checkout overview has total matching items and tax");
        BigDecimal expected = items().subtotal().add(displayedAmount(checkoutOverviewPage.getTaxLabel()));
        Assertions.assertEquals(0, expected.compareTo(displayedAmount(checkoutOverviewPage.getTotalLabel())));
    }

    private BigDecimal displayedAmount(org.openqa.selenium.By locator) {
        return saucedemo.com.assertions.MoneyAssertions.parseDollarAmount($(locator).waitUntilVisible().getText());
    }
}
