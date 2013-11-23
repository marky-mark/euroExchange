package exchangerate;

import dao.ExchangeRateDao;
import models.PlayExchangeRate;

import java.util.List;

public interface ExchangeRateService {
    ExchangeRateDao getExchangeRateDao();
    List<PlayExchangeRate> getExchangeRates(String code);
    void updateExchangeRatesOverLastNinetyDaysIntoCassandra();
    void updateExchangeRatesWithLatest();
}
