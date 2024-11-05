package com.example.demo.service.services;


import com.example.demo.integration.exchange.ExchangeIntegration;
import com.example.demo.integration.exchange.ExchangeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@SpringBootTest
@ActiveProfiles( "test" )
class ExchangeIntegrationTest {


    @Mock
    private RestTemplate restTemplate;

    @Autowired
    private ExchangeIntegration exchangeIntegration;

    @Value("${api.base.url}")
    String baseUrl;

    @Value("${api.key}")
    String exchangeId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void exchageServiceTest_MakeExchange_ReturnCorrectExchange() {
        //when
        ExchangeResponse response = new ExchangeResponse( 5.44 );
        when( restTemplate.getForObject( anyString(), any() ) )
                .thenReturn( response );
        //perform
        Double exchangeValue = exchangeIntegration.makeExchange("USD", "BRL");
        //expect
        assertEquals(5.44, response.conversion_rate());
    }

    @Test
    void exchageServiceTest_MakeExchange_ReturnError() {
        //when
        String from = "USD";
        String to = "ZZZ";
        //perform //expect
        assertThrows(
                HttpClientErrorException.class,
                () -> exchangeIntegration.makeExchange( from, to )
        );
        //when 
        String from2 = "ZZZ";
        String to2 = "USD";
        //perform //expect
        assertThrows(
                HttpClientErrorException.class,
                () -> exchangeIntegration.makeExchange( from2, to2 )
        );
    }

}