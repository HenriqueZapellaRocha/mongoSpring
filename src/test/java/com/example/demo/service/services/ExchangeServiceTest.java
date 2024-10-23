package com.example.demo.service.services;


import com.example.demo.integration.exchange.ExchangeIntegration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@ActiveProfiles( "test" )
class ExchangeServiceTest {

    @Autowired
    private ExchangeIntegration exchangeIntegration;

    @Test
    void exchageServiceTest_MakeExchange_ReturnCorrectExchange() {
        String from = "USD";
        String to = "EUR";
        RestTemplate querry = new RestTemplate();

        @SuppressWarnings( "unchecked" )
        Map<String, Map<String, String>> result = querry.getForObject(
                "https://economia.awesomeapi.com.br/json/last/" + from + "-" + to
                , Map.class );

        Double epsiolon = 1e-3;
        
        Map<String, String> Json = Objects.requireNonNull( result ).get( from.toUpperCase() + to.toUpperCase() );

        Double exchange1 = Double.parseDouble( Json.get( "bid" ) );
        Double exchange2 = exchangeIntegration.makeExchange(from, to);
        Double test = exchange1 - exchange2;

        assertTrue(test < epsiolon);
    }

    @Test
    void exchageServiceTest_MakeExchange_ReturnError() {
        String from = "USD";
        String to = "ZZZ";

        assertThrows(
                HttpClientErrorException.class,
                () -> exchangeIntegration.makeExchange( from, to )
        );

        String from2 = "ZZZ";
        String to2 = "USD";

        assertThrows(
                HttpClientErrorException.class,
                () -> exchangeIntegration.makeExchange( from2, to2 )
        );
    }

}