package saucedemo.com.enums;

import java.util.Arrays;

public enum Product {
    BIKE_LIGHT(0, "Sauce Labs Bike Light", "sauce-labs-bike-light", "$9.99"),
    BOLT_T_SHIRT(1, "Sauce Labs Bolt T-Shirt", "sauce-labs-bolt-t-shirt", "$15.99"),
    ONESIE(2, "Sauce Labs Onesie", "sauce-labs-onesie", "$7.99"),
    RED_T_SHIRT(3, "Test.allTheThings() T-Shirt (Red)", "test.allthethings()-t-shirt-(red)", "$15.99"),
    BACKPACK(4, "Sauce Labs Backpack", "sauce-labs-backpack", "$29.99"),
    FLEECE_JACKET(5, "Sauce Labs Fleece Jacket", "sauce-labs-fleece-jacket", "$49.99");
    private final int id;
    private final String title, slug, price;

    Product(int id, String title, String slug, String price) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.price = price;
    }

    public static Product from(String text) {
        String key = text == null ? "" : text.trim();
        return Arrays.stream(values()).filter(p -> p.title.equalsIgnoreCase(key) || p.slug.equalsIgnoreCase(key)
                        || p.name().equalsIgnoreCase(key.replace('-', '_').replace(' ', '_')))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown SauceDemo product: " + text));
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public String getPrice() {
        return price;
    }


}
