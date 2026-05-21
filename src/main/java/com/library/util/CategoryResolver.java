package com.library.util;

import com.library.repository.CategoryRepository;

/** Resolves category id from form select or custom "Other" name. */
public final class CategoryResolver {
    public static final String OTHER_VALUE = "other";

    private CategoryResolver() {}

    public static int resolveCategoryId(String categoryIdParam, String customCategoryName, CategoryRepository repo) {
        if (categoryIdParam == null || categoryIdParam.isBlank()) {
            return -1;
        }
        if (OTHER_VALUE.equalsIgnoreCase(categoryIdParam.trim())) {
            String name = trim(customCategoryName);
            if (name == null) {
                return -1;
            }
            return repo.findOrCreateByName(name);
        }
        try {
            int id = Integer.parseInt(categoryIdParam.trim());
            return repo.findById(id) != null ? id : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String trim(String value) {
        if (value == null) {
            return null;
        }
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }
}
