package com.example.demo.integration.exchange;



import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class ExchangeIntegration {

    private final RestTemplate restTemplate;

    public Double makeExchange( String from, String to ) {

        if ( from.equals( to ) )
            return 1.0;


        Map<String, ExchangeResponse> result = restTemplate.getForObject(
                "/last/" + from + "-" + to, // Base URL is handled in configuration
                Map.class
        );

        return Double.parseDouble( Objects.requireNonNull(result).get(from+to).bid() );
    }
}
