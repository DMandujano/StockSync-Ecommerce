package com.stockSync.backend.stock.service;

import com.stockSync.backend.common.exception.ConflictException;
import com.stockSync.backend.common.exception.ResourceNotFoundException;
import com.stockSync.backend.stock.dto.CategoryRequest;
import com.stockSync.backend.stock.dto.CategoryResponse;
import com.stockSync.backend.stock.mapper.CategoryMapper;
import com.stockSync.backend.stock.model.Category;
import com.stockSync.backend.stock.model.Product;
import com.stockSync.backend.stock.repository.CategoryRepository;
import com.stockSync.backend.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private User mockUser;

    @BeforeEach
    public void setup() {
        mockUser = new User();
        mockUser.setId(1L);

        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getPrincipal()).thenReturn(mockUser);

        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void createCategory_Success() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Electronics");

        Category category = new Category();
        category.setName("Electronics");

        CategoryResponse response = new CategoryResponse();
        response.setName("Electronics");

        when(categoryRepository.existsByNameAndUserId("Electronics", mockUser.getId())).thenReturn(false);
        when(categoryMapper.toEntity(request)).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(response);

        CategoryResponse result = categoryService.createCategory(request);

        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    public void createCategory_Conflict_ThrowsException() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Electronics");

        when(categoryRepository.existsByNameAndUserId("Electronics", mockUser.getId())).thenReturn(true);

        assertThrows(ConflictException.class, () -> categoryService.createCategory(request));

        verify(categoryRepository, never()).save(any());
    }

    @Test
    public void getCategoryById_WrongUser_ThrowsException() {
        Category category = new Category();
        category.setId(2L);
        User otherUser = new User();
        otherUser.setId(2L); // Diferente usuario
        category.setUser(otherUser);

        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(2L));
    }

    @Test
    public void deleteCategory_HasProducts_ThrowsConflictException() {
        Category category = new Category();
        category.setId(2L);
        category.setUser(mockUser);
        
        List<Product> products = new ArrayList<>();
        products.add(new Product());
        category.setProducts(products); // Simular que tiene productos

        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));

        assertThrows(ConflictException.class, () -> categoryService.deleteCategory(2L));
        verify(categoryRepository, never()).delete(any());
    }
}
