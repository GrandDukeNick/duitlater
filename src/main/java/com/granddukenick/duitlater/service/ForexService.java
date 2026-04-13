// service/ForexService.java
package com.granddukenick.duitlater.service;

import com.granddukenick.duitlater.service.response.ForexResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
@Slf4j
public class ForexService {

    private final RestClient restClient;

    @Value("${forex.api.url:https://api.frankfurter.dev/v1}")
    private String forexApiUrl;

    public ForexService() {
        this.restClient = RestClient.create();
    }

    /**
     * Get exchange rate between two currencies
     * @param fromCurrency Source currency (e.g., "USD")
     * @param toCurrency Target currency (e.g., "MYR")
     * @return Exchange rate as BigDecimal
     */
    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return BigDecimal.ONE;
        }

        log.info("Fetching exchange rate from {} to {}", fromCurrency, toCurrency);

        try {
            String url = forexApiUrl + "/latest?from=" + fromCurrency + "&to=" + toCurrency;

            ForexResponse response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(ForexResponse.class);

            log.info("RAW FOREX API RESPONSE: {}", response.toString());

            if (response != null && response.getRates() != null && response.getRates().containsKey(toCurrency.toUpperCase())) {
                BigDecimal rate = response.getRates().get(toCurrency.toUpperCase());
                log.info("Exchange rate: 1 {} = {} {}", fromCurrency, rate, toCurrency);
                return rate;
            } else {
                throw new RuntimeException("Transaction cancelled. Exchange rate not available for " + fromCurrency + " to " + toCurrency);
            }

        } catch (RestClientException e) {
            log.error("Forex API call failed: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch exchange rate: " + e.getMessage());
        }
    }
}