package com.stockSync.backend.stock.service;

import com.stockSync.backend.common.exception.ResourceNotFoundException;
import com.stockSync.backend.stock.dto.WarehouseRequest;
import com.stockSync.backend.stock.dto.WarehouseResponse;
import com.stockSync.backend.stock.mapper.WarehouseMapper;
import com.stockSync.backend.stock.model.Warehouse;
import com.stockSync.backend.stock.repository.WarehouseRepository;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WarehouseServiceImplTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private WarehouseMapper warehouseMapper;

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

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
    public void createWarehouse_Success() {
        WarehouseRequest request = new WarehouseRequest();
        request.setName("Main Warehouse");

        Warehouse warehouse = new Warehouse();
        warehouse.setName("Main Warehouse");

        WarehouseResponse response = new WarehouseResponse();
        response.setName("Main Warehouse");

        when(warehouseMapper.toEntity(request)).thenReturn(warehouse);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);
        when(warehouseMapper.toResponse(warehouse)).thenReturn(response);

        WarehouseResponse result = warehouseService.createWarehouse(request);

        assertNotNull(result);
        assertEquals("Main Warehouse", result.getName());
        verify(warehouseRepository).save(any(Warehouse.class));
    }

    @Test
    public void getWarehouseById_WrongUser_ThrowsException() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(2L);
        User otherUser = new User();
        otherUser.setId(2L);
        warehouse.setUser(otherUser);

        when(warehouseRepository.findById(2L)).thenReturn(Optional.of(warehouse));

        assertThrows(ResourceNotFoundException.class, () -> warehouseService.getWarehouseById(2L));
    }

    @Test
    public void deleteWarehouse_Success() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setUser(mockUser);

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        warehouseService.deleteWarehouse(1L);

        verify(warehouseRepository).deleteById(1L);
    }
}
