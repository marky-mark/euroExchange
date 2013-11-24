package service;

import dao.ExchangeRateDao;
import dao.ExchangeRateDaoException;
import europeancentralbank.CurrencyApiClient;
import europeancentralbank.EuropeanCentralBankApiException;
import europeancentralbank.response.EuropeanCentralBankExchange;
import europeancentralbank.response.ExchangeRate;
import europeancentralbank.response.ExchangeRateTimes;
import models.PlayExchangeRate;
import org.joda.time.DateTime;
import play.Logger;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class EuropeanCentralBankExchangeRateService implements ExchangeRateService {

    public static final int NO_ENTRIES = 0;
    public static final int PERIOD_OF_EXCHANGE = 90;

    private CurrencyApiClient europeanCentralBankApi;
    private ExchangeRateDao exchangeRateDao;

    public EuropeanCentralBankExchangeRateService(CurrencyApiClient europaCurrencyApi,
                                                  ExchangeRateDao exchangeRateDao) {
        this.europeanCentralBankApi = europaCurrencyApi;
        this.exchangeRateDao = exchangeRateDao;
    }

    //this is just for integration test - so that it is possible to tear down db
    public ExchangeRateDao getExchangeRateDao() {
        return exchangeRateDao;
    }

    @Override
    public List<PlayExchangeRate> getExchangeRates(String code) {

        DateTime now = new DateTime();
        DateTime nintyDaysAgo = new DateTime().minusDays(PERIOD_OF_EXCHANGE);

        List<PlayExchangeRate> playExchangeRates =
                exchangeRateDao.findRatesForCodeBetweenDates(code, now.toDate(), nintyDaysAgo.toDate());

        Collections.sort(playExchangeRates);

        return playExchangeRates;
    }

    @Override
    public boolean updateExchangeRatesOverLastNinetyDays(String code) {
        try {
            EuropeanCentralBankExchange exchangeRates = europeanCentralBankApi.getRatesOverLast90Days();
            insertExchangeRatesIntoCassandra(exchangeRates, code);
        } catch (EuropeanCentralBankApiException e) {
            Logger.error("error cannot connect to central european bank api ", e);
            return false;
        }

        return true;
    }

    @Override
    public boolean updateExchangeRatesWithLatest(String code) {
        try {
            EuropeanCentralBankExchange exchangeRates = europeanCentralBankApi.getLatestRates();
            insertExchangeRatesIntoCassandra(exchangeRates, code);
        } catch (EuropeanCentralBankApiException e) {
            Logger.error("error cannot connect to central european bank api ", e);
            return false;
        }

        return true;
    }

    @Override
    public boolean updateAllExchangeRatesWithLatest() {
        return updateExchangeRatesOverLastNinetyDays(null) && updateExchangeRatesWithLatest(null);
    }

    //helper methods

    private boolean alreadyInCassandra(Date date, String code) {
        DateTime dayAfter = new DateTime(date).minusDays(1);
        DateTime dayBefore = new DateTime(date).plusDays(1);

        try {
            List<PlayExchangeRate> rates = exchangeRateDao.findRatesForCodeBetweenDates(code, dayBefore.toDate(), dayAfter.toDate());
            return rates.size() > NO_ENTRIES;
        } catch (ExchangeRateDaoException e) {
            Logger.error("Failed to insert into cassandra in service ", e);
        }

        return false;
    }

    private void insertExchangeRatesIntoCassandra(EuropeanCentralBankExchange exchangeRates, String code) {

        if (exchangeRates != null) {

            List<ExchangeRateTimes> exchangeRateTimesList = getExchangeRateTimes(exchangeRates);

            if (exchangeRateTimesList != null) {

                for (ExchangeRateTimes exchangeRateTimes : exchangeRateTimesList) {
                    Date time = exchangeRateTimes.getDate();
                    insertExchangeRateListIntoCassandra(exchangeRateTimes, time, code);
                }
            }
        } else {
            Logger.error("No exchange rates received from api");
        }
    }

    private void insertExchangeRateListIntoCassandra(ExchangeRateTimes exchangeRateTimes, Date time, String code) {
        if (exchangeRateTimes != null) {
            for (ExchangeRate exchangeRate : exchangeRateTimes.getExchangeRates()) {
                insertExchangeRateIntoCassandra(time, exchangeRate, code);
            }
        } else {
            Logger.error("Exchange rates times is null");
        }
    }

    //This is thread safe - only writes to db if it does not exist
    private synchronized void insertExchangeRateIntoCassandra(Date time, ExchangeRate exchangeRate, String code) {
        if (time != null
                && exchangeRate != null
                && exchangeRate.getCurrency() != null
                && exchangeRate.getRate() != null) {
            try {
                if (mayInsertIntoCassandra(time, exchangeRate, code)) {
                    exchangeRateDao.insert(time, exchangeRate.getCurrency(), exchangeRate.getRate());
                }
            } catch (ExchangeRateDaoException e) {
                Logger.error("Failed to insert into cassandra in service ", e);
            }
        } else {
            Logger.error("Some faulty data attempting to be stored to DB");
        }
    }

    //lazy loading - avoiding all data being entered into DB, code is null to let everything
    private boolean mayInsertIntoCassandra(Date time, ExchangeRate exchangeRate, String code) {
        return code == null || code.equals(exchangeRate.getCurrency()) && !alreadyInCassandra(time, code);
    }

    private List<ExchangeRateTimes> getExchangeRateTimes(EuropeanCentralBankExchange exchange) {
        if (exchangeRateRootContainsData(exchange)) {
            return exchange.getExchangeRateWrapper().getExchangeRateTimes();
        } else {
            Logger.error("Exchange wrapper data faulty");
        }

        return null;
    }

    private boolean exchangeRateRootContainsData(EuropeanCentralBankExchange exchange) {
        return exchange != null
                && exchange.getExchangeRateWrapper() != null
                && exchange.getExchangeRateWrapper().getExchangeRateTimes() != null;
    }
}
