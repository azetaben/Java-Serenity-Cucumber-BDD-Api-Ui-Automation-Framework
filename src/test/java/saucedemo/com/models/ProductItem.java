package saucedemo.com.models;

import saucedemo.com.enums.Product;

public record ProductItem(int productId, String title, String slug, String price) {

    public static ProductItem fromReference(String reference) {
        Product product = Product.from(reference);
        return new ProductItem(product.getId(), product.getTitle(), product.getSlug(), product.getPrice());
    }
}
