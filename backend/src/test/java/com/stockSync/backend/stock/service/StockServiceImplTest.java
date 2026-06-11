package com.stockSync.backend.stock.service;

import com.stockSync.backend.common.exception.BadRequestException;
import com.stockSync.backend.stock.dto.StockRequest;
import com.stockSync.backend.stock.dto.StockResponse;
import com.stockSync.backend.stock.dto.StockTransferRequest;
import com.stockSync.backend.stock.mapper.StockMapper;
import com.stockSync.backend.stock.model.Product;
import com.stockSync.backend.stock.model.Stock;
import com.stockSync.backend.stock.model.Warehouse;
import com.stockSync.backend.stock.repository.ProductRepository;
import com.stockSync.backend.stock.repository.StockMovementRepository;
import com.stockSync.backend.stock.repository.StockRepository;
import com.stockSync.backend.stock.repository.WarehouseRepository;
import com.stockSync.backend.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockServiceImplTest {

    @Mock
    private StockRepository stockRepository;
    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private StockMovementRepository stockMovementRepository;
    @Mock
    private StockMapper stockMapper;

    @InjectMocks
    private StockServiceImpl stockService;

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
    public void addStock_Success() {
        StockRequest request = new StockRequest();
        request.setProductId(1L);
        request.setWarehouseId(2L);
        request.setQuantity(10);

        Product product = new Product();
        product.setId(1L);
        product.setStock(5L);
        product.setUser(mockUser);

        Warehouse warehouse = new Warehouse();
        warehouse.setId(2L);
        warehouse.setUser(mockUser);

        Stock stock = new Stock(product, warehouse, 0);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(2L)).thenReturn(Optional.of(warehouse));
        when(stockRepository.findByProductIdAndWarehouseIdAndUserId(1L, 2L, 1L))
                .thenReturn(Optional.of(stock));

        when(stockRepository.save(any(Stock.class))).thenReturn(stock);
        when(stockMapper.toResponse(any(Stock.class))).thenReturn(new StockResponse());

        stockService.addStock(request);

        assertEquals(10, stock.getQuantity());
        assertEquals(15L, product.getStock());
        verify(stockRepository).save(stock);
        verify(stockMovementRepository).save(any());
        verify(productRepository).save(product);
    }

    @Test
    public void processSale_InsufficientStock_ThrowsException() {
        StockRequest request = new StockRequest();
        request.setProductId(1L);
        request.setWarehouseId(2L);
        request.setQuantity(10);

        Product product = new Product();
        product.setId(1L);
        product.setUser(mockUser);

        Warehouse warehouse = new Warehouse();
        warehouse.setId(2L);
        warehouse.setUser(mockUser);

        Stock stock = new Stock(product, warehouse, 5); // Solo 5 disponibles

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(2L)).thenReturn(Optional.of(warehouse));
        when(stockRepository.findByProductIdAndWarehouseIdAndUserId(1L, 2L, 1L))
                .thenReturn(Optional.of(stock));

        assertThrows(BadRequestException.class, () -> stockService.processSale(request));
    }

    @Test
    public void transferStock_Success() {
        StockTransferRequest request = new StockTransferRequest();
        request.setProductId(1L);
        request.setSourcewarehouseId(2L);
        request.setDestinationWarehouseId(3L);
        request.setQuantity(5);

        Product product = new Product();
        product.setId(1L);
        product.setUser(mockUser);

        Warehouse sourceWh = new Warehouse();
        sourceWh.setId(2L);
        sourceWh.setUser(mockUser);

        Warehouse destWh = new Warehouse();
        destWh.setId(3L);
        destWh.setUser(mockUser);

        Stock sourceStock = new Stock(product, sourceWh, 10);
        Stock destStock = new Stock(product, destWh, 2);

        when(stockRepository.findByProductIdAndWarehouseIdAndUserId(1L, 2L, 1L))
                .thenReturn(Optional.of(sourceStock));
        when(stockRepository.findByProductIdAndWarehouseIdAndUserId(1L, 3L, 1L))
                .thenReturn(Optional.of(destStock));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(2L)).thenReturn(Optional.of(sourceWh));
        when(warehouseRepository.findById(3L)).thenReturn(Optional.of(destWh));

        stockService.transferStock(request);

        assertEquals(5, sourceStock.getQuantity());
        assertEquals(7, destStock.getQuantity());

        verify(stockRepository).save(sourceStock);
        verify(stockRepository).save(destStock);
        verify(stockMovementRepository).save(any());
    }

    @Test
    public void transferStock_AccessDenied() {
        StockTransferRequest request = new StockTransferRequest();
        request.setProductId(1L);
        request.setSourcewarehouseId(2L);
        request.setDestinationWarehouseId(3L);
        request.setQuantity(5);

        Stock sourceStock = new Stock();
        sourceStock.setQuantity(10);
        when(stockRepository.findByProductIdAndWarehouseIdAndUserId(1L, 2L, 1L))
                .thenReturn(Optional.of(sourceStock));

        Product product = new Product();
        product.setId(1L);
        product.setUser(mockUser);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Warehouse sourceWh = new Warehouse();
        sourceWh.setId(2L);
        sourceWh.setUser(mockUser);
        when(warehouseRepository.findById(2L)).thenReturn(Optional.of(sourceWh));

        Warehouse destWh = new Warehouse();
        destWh.setId(3L);
        User otherUser = new User();
        otherUser.setId(99L);
        destWh.setUser(otherUser); // Diferente usuario
        when(warehouseRepository.findById(3L)).thenReturn(Optional.of(destWh));

        assertThrows(AccessDeniedException.class, () -> stockService.transferStock(request));
    }
}
