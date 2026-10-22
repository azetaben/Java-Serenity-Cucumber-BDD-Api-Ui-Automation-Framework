package saucedemo.com.pages.components;

import net.serenitybdd.core.pages.WebElementFacade;
import saucedemo.com.assertions.MoneyAssertions;
import saucedemo.com.models.OrderLine;
import saucedemo.com.support.FrameworkLogger;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Composed page component: one implementation of cart/checkout row extraction.
 */
public record OrderItems(WebElementFacade container) {
    public OrderItems(WebElementFacade container) {
        this.container = Objects.requireNonNull(container, "Order container is required");
    }

    private static OrderLine read(WebElementFacade row) {
        return new OrderLine(row.find(ProductLocators.NAME).getText().trim(),
                Integer.parseInt(row.find(ProductLocators.QUANTITY).getText().trim()),
                MoneyAssertions.parseDollarAmount(row.find(ProductLocators.PRICE).getText()));
    }

    public int count() {
        FrameworkLogger.pageOperationStarted(OrderItems.class, "count");
        return rows().size();
    }

    public List<String> names() {
        FrameworkLogger.pageOperationStarted(OrderItems.class, "names");
        return rows().stream().map(row -> row.find(ProductLocators.NAME).getText().trim()).toList();
    }

    public OrderLine read(int productId) {
        FrameworkLogger.pageOperationStarted(OrderItems.class, "read");
        return read(row(productId));
    }

    public String displayedPrice(int productId) {
        FrameworkLogger.pageOperationStarted(OrderItems.class, "displayedPrice");
        return row(productId).find(ProductLocators.PRICE).getText().trim();
    }

    public BigDecimal subtotal() {
        FrameworkLogger.pageOperationStarted(OrderItems.class, "subtotal");
        return rows().stream().map(OrderItems::read).map(OrderLine::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<WebElementFacade> rows() {
        return container.waitUntilVisible().thenFindAll(ProductLocators.ROWS);
    }

    private WebElementFacade row(int productId) {
        container.waitUntilVisible();
        return container.find(ProductLocators.row(productId)).waitUntilVisible();
    }
}
