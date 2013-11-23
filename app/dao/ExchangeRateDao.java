package dao;

import models.PlayExchangeRate;

import java.util.Date;
import java.util.List;

public interface ExchangeRateDao {
    void init(int port);
    void dropTable();
    void insert(Date timestamp, String code, Double rate);
    List<PlayExchangeRate> findRatesForCodeBetweenDates(String code, Date lessThan, Date moreThan);
}
