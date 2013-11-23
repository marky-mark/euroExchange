package exchangerate;

import dao.ExchangeRateDao;

import java.util.Date;
import java.util.Map;

public interface ExchangeRateService {
    ExchangeRateDao getExchangeRateDao();
    Map<Date, Double> getExchangeRates(String code);
    void updateExchangeRatesOverLastNinetyDaysIntoCassandra();
    void updateExchangeRatesWithLatest();
}
