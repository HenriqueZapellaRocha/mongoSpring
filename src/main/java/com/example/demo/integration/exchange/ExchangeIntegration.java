package com.example.demo.integration.exchange;



import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class ExchangeIntegration {

    private final RestTemplate restTemplate;

    public Double makeExchange( String from, String to ) {

        ExchangeResponse result = restTemplate.getForObject(
                "/pair/" + from + "/" + to, // Base URL is handled in configuration
                ExchangeResponse.class
        );

        return Objects.requireNonNull(result).conversion_rate();
    }
}
