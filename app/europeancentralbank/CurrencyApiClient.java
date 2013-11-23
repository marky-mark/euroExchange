package europeancentralbank;


import europeancentralbank.response.EuropeanCentralBankExchange;

public interface CurrencyApiClient {
    EuropeanCentralBankExchange getLatestRates();
    EuropeanCentralBankExchange getRatesOverLast90Days();
}
