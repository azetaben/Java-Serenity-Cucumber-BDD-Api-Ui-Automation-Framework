package saucedemo.com.models;

import java.math.BigDecimal;

public record OrderLine(String name, int quantity, BigDecimal unitPrice) {
    public BigDecimal lineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
