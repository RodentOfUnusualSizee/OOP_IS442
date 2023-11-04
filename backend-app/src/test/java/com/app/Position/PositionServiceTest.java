package com.app.Position;

import com.app.ExternalAPIs.CompanyOverviewAPI.CompanyOverviewController;
import com.app.ExternalAPIs.CompanyOverviewAPI.CompanyOverviewDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.*;

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
    }

    @Test
    public void testSave() {
        when(companyOverviewController.getCompanyOverview("AAPL")).thenReturn(companyOverviewDTO);
        when(positionRepository.save(any(Position.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Position savedPosition = positionService.save(position);

        assertNotNull(savedPosition.getCreatedTimestamp());
        assertNotNull(savedPosition.getLastModifiedTimestamp());
        assertEquals("Technology", savedPosition.getStockSector());
        assertEquals("USA", savedPosition.getStockGeographicalLocation());

        verify(positionRepository).save(position);
    }

    @Test
    public void testSaveWithAPIException() {
        when(companyOverviewController.getCompanyOverview("AAPL")).thenThrow(new RuntimeException("API Failure"));

        Position savedPosition = positionService.save(position);

        assertNotNull(savedPosition.getCreatedTimestamp());
        assertNotNull(savedPosition.getLastModifiedTimestamp());
        assertEquals("Unidentified", savedPosition.getStockSector());
        assertEquals("Unidentified", savedPosition.getStockGeographicalLocation());

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

