package controllers;

import exchangerate.Context;
import models.PlayExchangeRate;
import play.mvc.Controller;

import java.util.List;

public class ExchangeRateController extends Controller {

    public static final int NO_EXCHANGE_RATES_FOUND = 0;

    public static void getExchangeRate(String code) {

        List<PlayExchangeRate> exchangeRatesForCode = getExchangeRatesForCode(code);

        if (!exchangeRatesFound(exchangeRatesForCode)) {
            updateExchangeRatesOverLast90Days(code);
            exchangeRatesForCode = getExchangeRatesForCode(code);
        }

        renderJSON(Context.getGsonParser().toJson(exchangeRatesForCode));
    }

    public static void refreshExchangeRate(String code) {
        Context.getExchangeRateService().updateExchangeRatesWithLatest(code);
        List<PlayExchangeRate> exchangeRatesForCode = getExchangeRatesForCode(code);
        renderJSON(Context.getGsonParser().toJson(exchangeRatesForCode));
    }

    public static void refreshAllExchangeRates(String code) {
        Context.getExchangeRateService().updateAllExchangeRatesWithLatest();
        List<PlayExchangeRate> exchangeRatesForCode = getExchangeRatesForCode(code);
        renderJSON(Context.getGsonParser().toJson(exchangeRatesForCode));
    }

    private static void updateExchangeRatesOverLast90Days(String code) {
        Context.getExchangeRateService().updateExchangeRatesOverLastNinetyDaysIntoCassandra(code);
    }

    private static boolean exchangeRatesFound(List<PlayExchangeRate> exchangeRatesForCode) {
        return exchangeRatesForCode.size() > NO_EXCHANGE_RATES_FOUND;
    }

    private static List<PlayExchangeRate> getExchangeRatesForCode(String code) {
        return Context.getExchangeRateService().getExchangeRates(code);
    }

}
