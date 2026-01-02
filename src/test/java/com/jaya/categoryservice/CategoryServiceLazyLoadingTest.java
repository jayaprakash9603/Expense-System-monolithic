package com.jaya.categoryservice;

import com.jaya.categoryservice.models.Category;
import com.jaya.categoryservice.repository.CategoryRepository;
import com.jaya.categoryservice.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategoryServiceLazyLoadingTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void getAllForUser_initializesExpenseIds() {
        // Arrange
        Integer userId = 99999;

        Category c = new Category();
        c.setName("Test Category");
        c.setDescription("test");
        c.setUserId(userId);

        HashMap<Integer, Set<Integer>> expenseIds = new HashMap<>();
        expenseIds.put(userId, new HashSet<>(Set.of(1, 2, 3)));
        c.setExpenseIds(expenseIds);

        c.setUserIds(new HashSet<>(Set.of(userId)));
        c.setEditUserIds(new HashSet<>(Set.of(userId)));

        categoryRepository.save(c);

        // Act
        List<Category> categories = categoryService.getAllForUser(userId);

        // Assert
        assertNotNull(categories);
        Category found = categories.stream()
                .filter(cat -> "Test Category".equals(cat.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(found);
        assertDoesNotThrow(() -> found.getExpenseIds().size());
        assertTrue(found.getExpenseIds().containsKey(userId));
    }
}
