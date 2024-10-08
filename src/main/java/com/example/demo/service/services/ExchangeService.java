package com.example.demo.service.services;



import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.Objects;


@Service
public class ExchangeService {

    public Double makeExchange( String from, String to ) {

        if ( from.equals( to ) )
            return 1.0;

        RestTemplate querry = new RestTemplate();

        @SuppressWarnings( "unchecked" )
        Map<String, Map<String, String>> result = querry.getForObject(
                "https://economia.awesomeapi.com.br/json/last/" + from + "-" + to
                , Map.class );


        Map<String, String> exchange = Objects.requireNonNull( result )
                                              .get( from.toUpperCase() + to.toUpperCase() );
        return Double.parseDouble( exchange.get( "bid" ) );
    }
}
