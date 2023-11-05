package com.app;

import com.app.Stock.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for MainApp.
 */
@SpringBootTest
public class MainAppTests {

    @Autowired
    private ApplicationContext context;

    /**
     * Test if the application context loads properly.
     */
    @Test
    public void contextLoads() {
        assertThat(context).isNotNull();
    }

    /**
     * Test if the StockService bean is created and can be autowired successfully.
     */
    @Test
    public void stockServiceBeanIsLoaded() {
        StockService stockService = context.getBean(StockService.class);
        assertThat(stockService).isNotNull();
    }

    /**
     * Test if the RestTemplate bean is created correctly.
     */
    @Test
    public void restTemplateBeanIsLoaded() {
        RestTemplate restTemplate = context.getBean(RestTemplate.class);
        assertThat(restTemplate).isNotNull();
    }
}