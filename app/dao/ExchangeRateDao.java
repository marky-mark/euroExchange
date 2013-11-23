package dao;

import java.util.Date;
import java.util.Map;

public interface ExchangeRateDao {
    void init(int port);
    void dropTable();
    void insert(Date timestamp, String code, Double rate);
    Map<Date, Double> findRatesForCodeBetweenDates(String code, Date lessThan, Date moreThan);
}
