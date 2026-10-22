package saucedemo.com.contentdesign;

import java.util.Locale;
import java.util.Map;

public final class PageUrls {
    private static final Map<String, String> PATHS = Map.ofEntries(
            Map.entry("login", "/"), Map.entry("home", "/"),
            Map.entry("inventory", "/inventory.html"), Map.entry("products", "/inventory.html"),
            Map.entry("inventory item", "/inventory-item.html"), Map.entry("product details", "/inventory-item.html"),
            Map.entry("your cart", "/cart.html"), Map.entry("cart", "/cart.html"),
            Map.entry("checkout information", "/checkout-step-one.html"),
            Map.entry("checkout overview", "/checkout-step-two.html"),
            Map.entry("checkout complete", "/checkout-complete.html"));

    private PageUrls() {
    }

    public static String pathFor(String name) {
        String key = name.trim().toLowerCase(Locale.ROOT).replace('_', ' ').replace('-', ' ');
        String path = PATHS.get(key);
        if (path == null) throw new IllegalArgumentException("Unknown SauceDemo page: " + name);
        return path;
    }
}
