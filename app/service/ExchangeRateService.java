package service;

import models.PlayExchangeRate;

import java.util.List;

public interface ExchangeRateService {
    List<PlayExchangeRate> getExchangeRates(String code);
    boolean updateExchangeRatesOverLastNinetyDays(String code);
    boolean updateExchangeRatesWithLatest(String code);
    boolean updateAllExchangeRatesWithLatest();
}
