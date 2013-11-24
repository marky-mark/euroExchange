package exchangerate;

import models.PlayExchangeRate;

import java.util.List;

public interface ExchangeRateService {
    List<PlayExchangeRate> getExchangeRates(String code);
    void updateExchangeRatesOverLastNinetyDaysIntoCassandra(String code);
    void updateExchangeRatesWithLatest(String code);
    void updateAllExchangeRatesWithLatest();
}
