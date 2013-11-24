package controllers;

import play.libs.F;
import service.Context;
import models.PlayExchangeRate;
import play.mvc.Controller;
import service.job.RetreiveExchangeRatesJob;
import service.job.UpdateAllExchangeRatesJob;
import service.job.UpdateExchangeRatesWithLatestJob;
import service.job.updateExchangeRatesOverLastNinetyDaysJob;

import java.util.List;

public class ExchangeRateController extends Controller {

    public static final int NO_EXCHANGE_RATES_FOUND = 0;

    public static void getExchangeRate(String code) {

        List<PlayExchangeRate> exchangeRatesForCode = getExchangeRates(code);

        if (!exchangeRatesFound(exchangeRatesForCode)) {
            F.Promise<Boolean> performed = new updateExchangeRatesOverLastNinetyDaysJob(code).now();
            await(performed);
            exchangeRatesForCode = getExchangeRates(code);
        }

        renderJSON(Context.getGsonParser().toJson(exchangeRatesForCode));
    }

    public static void refreshExchangeRate(String code) {
        F.Promise<Boolean> performed = new UpdateExchangeRatesWithLatestJob(code).now();
        await(performed);
        List<PlayExchangeRate> exchangeRatesForCode = getExchangeRates(code);
        renderJSON(Context.getGsonParser().toJson(exchangeRatesForCode));
    }

    public static void refreshAllExchangeRates(String code) {
        F.Promise<Boolean> performed = new UpdateAllExchangeRatesJob().now();
        await(performed);
        List<PlayExchangeRate> exchangeRatesForCode = getExchangeRates(code);
        renderJSON(Context.getGsonParser().toJson(exchangeRatesForCode));
    }

    private static boolean exchangeRatesFound(List<PlayExchangeRate> exchangeRatesForCode) {
        return exchangeRatesForCode.size() > NO_EXCHANGE_RATES_FOUND;
    }

    private static List<PlayExchangeRate> getExchangeRates(String code) {
        F.Promise<List<PlayExchangeRate>> exchangeRatesForCodePromise = new RetreiveExchangeRatesJob(code).now();
        return await(exchangeRatesForCodePromise);
    }

}
