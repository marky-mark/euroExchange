package europeancentralbank;


import europeancentralbank.response.EuropeanCentralBankExchange;

public interface CurrencyApiClient {
    EuropeanCentralBankExchange getDailyCurrencies();
    EuropeanCentralBankExchange getRatesOverLast90Days();
}
