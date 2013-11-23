package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exchangerate.Context;
import models.PlayExchangeRate;
import play.mvc.After;
import play.mvc.Before;
import play.mvc.Controller;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

public class ExchangeRateController extends Controller {

    public static final int NO_EXCHANGE_RATES_FOUND = 0;

    private static Gson gsonParser;

    @Before
    public static void init() throws URISyntaxException {
        Context.initExchangeService();

        gsonParser = createGsonParser();
    }

    private static Gson createGsonParser() {
        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(Date.class, new DateSerializer());
        return gson.create();
    }

    @After
    public static void tearDown() {
        Context.getExchangeRateService().getExchangeRateDao().dropTable();
    }

    public static void getExchangeRate(String code) {

        List<PlayExchangeRate> exchangeRatesForCode = getExchangeRatesForCode(code);

        if (!exchangeRatesFound(exchangeRatesForCode)) {
            updateExchangeRatesOverLast90Days();
            exchangeRatesForCode = getExchangeRatesForCode(code);
        }

        renderJSON(gsonParser.toJson(exchangeRatesForCode));
    }

    private static void updateExchangeRatesOverLast90Days() {
        Context.getExchangeRateService().updateExchangeRatesOverLastNinetyDaysIntoCassandra();
    }

    private static boolean exchangeRatesFound(List<PlayExchangeRate> exchangeRatesForCode) {
        return exchangeRatesForCode.size() > NO_EXCHANGE_RATES_FOUND;
    }

    private static List<PlayExchangeRate> getExchangeRatesForCode(String code) {
        return Context.getExchangeRateService().getExchangeRates(code);
    }

}
