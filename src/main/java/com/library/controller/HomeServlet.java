package com.library.controller;

import com.library.model.Categories;
import com.library.model.Cheatsheets;
import com.library.model.Tags;
import com.library.repository.CategoryRepository;
import com.library.repository.CheatsheetRepository;
import com.library.repository.TagRepository;
import com.library.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private final CategoryRepository categoryRepo = new CategoryRepository();
    private final CheatsheetRepository cheatsheetRepo = new CheatsheetRepository();
    private final TagRepository tagRepo = new TagRepository();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        WebUtils.prepareUserContext(request);

        String q = trimParam(request.getParameter("q"));
        String categoryIdParam = trimParam(request.getParameter("categoryId"));
        String tagIdParam = trimParam(request.getParameter("tagId"));

        if (q != null) {
            loadSearch(request, q);
        } else if (categoryIdParam != null && tagIdParam != null) {
            int categoryId = parseId(categoryIdParam);
            int tagId = parseId(tagIdParam);
            if (categoryId < 0 || tagId < 0 || !loadTagStep(request, categoryId, tagId)) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }
        } else if (categoryIdParam != null) {
            int categoryId = parseId(categoryIdParam);
            if (categoryId < 0 || !loadCategoryStep(request, categoryId)) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }
        } else {
            loadCategoriesStep(request);
        }

        request.getRequestDispatcher("home.jsp").forward(request, response);
    }

    private void loadCategoriesStep(HttpServletRequest request) {
        request.setAttribute("browseStep", "categories");
        List<Categories> categories = categoryRepo.findAll();
        request.setAttribute("categoryList", categories);

        Map<Integer, Integer> sheetCounts = new HashMap<>();
        for (Categories cat : categories) {
            sheetCounts.put(cat.getId(), categoryRepo.countSheets(cat.getId()));
        }
        request.setAttribute("categorySheetCounts", sheetCounts);
    }

    private boolean loadCategoryStep(HttpServletRequest request, int categoryId) {
        Categories category = categoryRepo.findById(categoryId);
        if (category == null) {
            return false;
        }
        request.setAttribute("browseStep", "category");
        request.setAttribute("category", category);
        request.setAttribute("tagList", tagRepo.findByCategoryId(categoryId));
        request.setAttribute("sheetList", cheatsheetRepo.findByCategoryId(categoryId));
        return true;
    }

    private boolean loadTagStep(HttpServletRequest request, int categoryId, int tagId) {
        Categories category = categoryRepo.findById(categoryId);
        Tags tag = tagRepo.findById(tagId);
        if (category == null || tag == null) {
            return false;
        }
        request.setAttribute("browseStep", "tag");
        request.setAttribute("category", category);
        request.setAttribute("tag", tag);
        request.setAttribute("tagList", tagRepo.findByCategoryId(categoryId));
        request.setAttribute("sheetList", cheatsheetRepo.findByCategoryIdAndTagId(categoryId, tagId));
        return true;
    }

    private void loadSearch(HttpServletRequest request, String q) {
        request.setAttribute("browseStep", "search");
        request.setAttribute("searchKeyword", q);
        request.setAttribute("sheetList", cheatsheetRepo.search(q));
    }

    private static String trimParam(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static int parseId(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
