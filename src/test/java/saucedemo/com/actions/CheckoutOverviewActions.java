package saucedemo.com.actions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.steps.UIInteractionSteps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import saucedemo.com.enums.Product;
import saucedemo.com.pages.CheckoutOverviewPage;
import saucedemo.com.pages.components.OrderItems;
import saucedemo.com.properties.ApplicationConfig;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckoutOverviewActions extends UIInteractionSteps {
    private static final Logger log = LoggerFactory.getLogger(CheckoutOverviewActions.class);

    @Steps
    private CheckoutOverviewPage checkoutOverviewPage;

    public OrderItems items() {
        return new OrderItems($(checkoutOverviewPage.getCartList()));
    }

    @Step("Verify the checkout overview contains the following products: {0}")
    public void shouldContainProducts(List<Map<String, String>> rows) {
        assertThat(items().count()).isEqualTo(rows.size());
        for (Map<String, String> row : rows) {
            String title = row.containsKey("name") ? row.get("name") : row.get("product");
            Product product = Product.from(title);
            var actual = items().read(product.getId());
            assertThat(actual.name()).as("Order item name").isEqualTo(title);
            if (row.containsKey("quantity")) {
                assertThat(actual.quantity()).as("Order item quantity").isEqualTo(Integer.parseInt(row.get("quantity")));
            }
            String priceKey = row.containsKey("unitPrice") ? "unitPrice" : "price";
            if (row.containsKey(priceKey)) {
                BigDecimal expectedPrice = saucedemo.com.assertions.MoneyAssertions.parseDollarAmount(row.get(priceKey));
                assertThat(expectedPrice.compareTo(actual.unitPrice())).as("Order item unit price").isEqualTo(0);
            }
        }
        log.info("Checkout overview contains the expected products: {}", rows);
    }

    @Step("Verify the checkout overview has subtotal: {0}")
    public void should_have_subtotal(BigDecimal expected) {
        assertThat(items().subtotal().compareTo(expected)).isEqualTo(0);
        log.info("Checkout overview has expected subtotal: {}", expected);
    }

    @Step("Verify the checkout overview has subtotal matching items")
    public void should_have_subtotal_matching_items() {
        assertThat(items().subtotal().compareTo(displayedAmount(checkoutOverviewPage.getSubtotalLabel()))).isEqualTo(0);
        log.info("Checkout overview has subtotal matching items");
    }

    @Step("Verify the checkout overview has tax at configured rate")
    public void should_have_tax_at_configured_rate() {
        BigDecimal expected = items().subtotal().multiply(new BigDecimal(ApplicationConfig.value("app.checkout.taxRate", "0.08"))).setScale(2, RoundingMode.HALF_UP);
        assertThat(expected.compareTo(displayedAmount(checkoutOverviewPage.getTaxLabel()))).isEqualTo(0);
        log.info("Checkout overview has tax at configured rate: {}", expected);
    }

    @Step("Verify the checkout overview has consistent total")
    public void should_have_consistent_total() {
        BigDecimal expected = displayedAmount(checkoutOverviewPage.getSubtotalLabel()).add(displayedAmount(checkoutOverviewPage.getTaxLabel()));
        assertThat(expected.compareTo(displayedAmount(checkoutOverviewPage.getTotalLabel()))).isEqualTo(0);
        log.info("Checkout overview has consistent total: {}", expected);
    }

    @Step("Finish the checkout process")
    public void finish_checkout() {
        $(checkoutOverviewPage.getFinishButton()).waitUntilClickable().click();
        log.info("Clicked on the finish button to complete checkout");
    }

    @Step("Cancel the checkout process")
    public void cancel_checkout() {
        $(checkoutOverviewPage.getCancelButton()).waitUntilClickable().click();
        log.info("Clicked on the cancel button to cancel checkout");

    }

    @Step("Verify the checkout overview has total matching subtotal and tax")
    public void should_have_total_matching_subtotal_and_tax() {
        BigDecimal expected = displayedAmount(checkoutOverviewPage.getSubtotalLabel()).add(displayedAmount(checkoutOverviewPage.getTaxLabel()));
        assertThat(expected.compareTo(displayedAmount(checkoutOverviewPage.getTotalLabel()))).isEqualTo(0);
        log.info("Checkout overview has total matching subtotal and tax: {}", expected);
    }

    @Step("Verify the checkout overview has total matching items and tax")
    public void should_have_total_matching_items_and_tax() {
        BigDecimal expected = items().subtotal().add(displayedAmount(checkoutOverviewPage.getTaxLabel()));
        assertThat(expected.compareTo(displayedAmount(checkoutOverviewPage.getTotalLabel()))).isEqualTo(0);
        log.info("Checkout overview has total matching items and tax: {}", expected);
    }

    private BigDecimal displayedAmount(org.openqa.selenium.By locator) {
        log.info("Retrieving displayed amount from locator: {}", locator);
        return saucedemo.com.assertions.MoneyAssertions.parseDollarAmount($(locator).waitUntilVisible().getText());
    }
}
