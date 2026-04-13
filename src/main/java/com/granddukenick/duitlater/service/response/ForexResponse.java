package com.granddukenick.duitlater.service.response;

import java.math.BigDecimal;
import java.util.Map;

public class ForexResponse {
    private BigDecimal amount;
    private String base;
    private String date;
    private Map<String, BigDecimal> rates;

    // Getters and setters
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getBase() { return base; }
    public void setBase(String base) { this.base = base; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Map<String, BigDecimal> getRates() { return rates; }
    public void setRates(Map<String, BigDecimal> rates) { this.rates = rates; }
}