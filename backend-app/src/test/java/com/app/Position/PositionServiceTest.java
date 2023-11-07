package com.app.Position;

import com.app.ExternalAPIs.CompanyOverviewAPI.CompanyOverviewController;
import com.app.ExternalAPIs.CompanyOverviewAPI.CompanyOverviewDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PositionServiceTest {

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private CompanyOverviewController companyOverviewController;

    @InjectMocks
    private PositionService positionService;

    private Position position;
    private CompanyOverviewDTO companyOverviewDTO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        position = new Position();
        position.setPositionID(1);
        position.setStockSymbol("AAPL");

        companyOverviewDTO = new CompanyOverviewDTO();
        companyOverviewDTO.setSector("Technology");
        companyOverviewDTO.setCountry("USA");

        positionRepository = mock(PositionRepository.class);
        companyOverviewController = mock(CompanyOverviewController.class);
        positionService = new PositionService();
        ReflectionTestUtils.setField(positionService, "positionRepository", positionRepository);
        ReflectionTestUtils.setField(positionService, "companyOverviewController", companyOverviewController);
    }

    @Test
    public void testSave() {
        when(companyOverviewController.getCompanyOverview("AAPL")).thenReturn(companyOverviewDTO);
        when(positionRepository.save(any(Position.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Position savedPosition = positionService.save(position);

        assertNotNull(savedPosition.getLastModifiedTimestamp());
        assertEquals("Technology", savedPosition.getStockSector());
        assertEquals("USA", savedPosition.getStockGeographicalLocation());

        verify(positionRepository).save(position);
    }

    @Test
    public void testSaveWithAPIException() {
         // Arrange
        when(companyOverviewController.getCompanyOverview(anyString()))
                .thenThrow(new RuntimeException("API Failure"));
        
        Position position = new Position();
        // Assuming positionID is 0 for a new position (which should trigger the creation timestamp setting)
        position.setPositionID(0); // Since it's a new position
        position.setStockSymbol("AAPL");
        position.setPrice(150.0f);
        position.setPosition("LONG");
        position.setQuantity(10);
        position.setPositionAddDate(new Date()); // Use a fixed Date value for consistent testing

        when(positionRepository.save(any(Position.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Position savedPosition = positionService.save(position);

        // Assert
        assertNotNull(savedPosition.getCreatedTimestamp(), "Created timestamp should be set");
        assertNotNull(savedPosition.getLastModifiedTimestamp(), "Last modified timestamp should be set");

        assertEquals("Unidentified", savedPosition.getStockSector(), 
            "Stock sector should be 'Unidentified' due to API exception");
        assertEquals("Unidentified", savedPosition.getStockGeographicalLocation(), 
            "Stock geographical location should be 'Unidentified' due to API exception");

        // Verify other fields have been set correctly
        assertEquals(0, savedPosition.getPositionID());
        assertEquals("AAPL", savedPosition.getStockSymbol());
        assertEquals(150.0f, savedPosition.getPrice(), 0.0, "Price should match the input");
        assertEquals("LONG", savedPosition.getPosition());
        assertEquals(10, savedPosition.getQuantity());
        assertNotNull(savedPosition.getPositionAddDate(), "Position add date should be set");

        // Verify interaction
        verify(positionRepository).save(position);
    }

    @Test
    public void testFindById() {
        Optional<Position> expectedPosition = Optional.of(position);
        when(positionRepository.findById(1)).thenReturn(expectedPosition);

        Optional<Position> foundPosition = positionService.findById(1);

        assertTrue(foundPosition.isPresent());
        assertEquals(expectedPosition.get(), foundPosition.get());
        verify(positionRepository).findById(1);
    }

    @Test
    public void testDeleteById() {
        doNothing().when(positionRepository).deleteById(1);

        positionService.deleteById(1);

        verify(positionRepository).deleteById(1);
    }

    @Test
    public void testFindAll() {
        List<Position> positionList = Collections.singletonList(position);
        when(positionRepository.findAll()).thenReturn(positionList);

        List<Position> foundPositions = positionService.findAll();

        assertFalse(foundPositions.isEmpty());
        assertEquals(positionList.size(), foundPositions.size());
        assertEquals(position, foundPositions.get(0));
        verify(positionRepository).findAll();
    }

    @Test
    public void testGetStockAdditionalInformationAPI() {
        when(companyOverviewController.getCompanyOverview("AAPL")).thenReturn(companyOverviewDTO);

        Map<String, String> stockAdditionalInfo = positionService.getStockAdditionalInformationAPI(position);

        assertEquals("Technology", stockAdditionalInfo.get("stockSector"));
        assertEquals("USA", stockAdditionalInfo.get("stockGeographicalLocation"));
    }
}

