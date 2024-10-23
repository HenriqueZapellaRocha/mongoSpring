package com.example.demo.integration.exchange;



import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.Objects;


@Service
public class ExchangeIntegration {

    RestTemplate querry = new RestTemplate();

    public Double makeExchange( String from, String to ) {

        if ( from.equals( to ) )
            return 1.0;


        Map<String, ExchangeResponse> result = querry.getForObject(
                "https://economia.awesomeapi.com.br/json/last/" + from + "-" + to
                , Map.class);

        return Double.parseDouble( Objects.requireNonNull(result).get(from+to).bid() );
    }
}
