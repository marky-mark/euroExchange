package controllers;

import models.PlayExchangeRate;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import play.libs.F;
import play.mvc.Controller;
import service.Context;
import service.EuropeanCentralBankExchangeRateService;
import service.job.RetreiveExchangeRatesJob;
import service.job.UpdateAllExchangeRatesJob;
import service.job.UpdateExchangeRatesWithLatestJob;
import service.job.updateExchangeRatesOverLastNinetyDaysJob;

import java.util.List;

public class ExchangeRateController extends Controller {

    public static final int PERIOD_OF_EXCHANGE_RATES = 90;

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
        return exchangeRatesForCode.size() >= numberOfWeekDaysOverPeriod(EuropeanCentralBankExchangeRateService.PERIOD_OF_EXCHANGE);
    }

    private static List<PlayExchangeRate> getExchangeRates(String code) {
        F.Promise<List<PlayExchangeRate>> exchangeRatesForCodePromise = new RetreiveExchangeRatesJob(code).now();
        return await(exchangeRatesForCodePromise);
    }


    private static int numberOfWeekDaysOverPeriod(int days) {
        DateTime now = new DateTime();
        DateTime iterator = new DateTime().minusDays(days);
        int count = 0;

        while (iterator.compareTo(now) < 0) {
            if (iterator.getDayOfWeek() != DateTimeConstants.SATURDAY
                && iterator.getDayOfWeek() != DateTimeConstants.SUNDAY) {
                count++;
            }
            iterator = iterator.plusDays(1);
        }

        return count;
    }

}
