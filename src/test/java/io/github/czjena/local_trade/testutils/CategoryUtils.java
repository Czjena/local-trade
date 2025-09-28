package io.github.czjena.local_trade.testutils;

import io.github.czjena.local_trade.model.Category;

public class CategoryUtils {
    public static Category createCategory() {
        Category category = new Category();
        category.setId(1);
        category.setName("test");
        category.setDescription("test");
        category.setParentCategory("test");
        return category;
    }
}
