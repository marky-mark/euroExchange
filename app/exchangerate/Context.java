package exchangerate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controllers.DateSerializer;
import dao.ExchangeRateDao;
import dao.ExchangeRateDaoImpl;
import europeancentralbank.CurrencyApiClient;
import europeancentralbank.EuropeanCentralBankApiClient;
import europeancentralbank.supplier.DefaultRestResourceSupplier;
import play.Logger;
import play.Play;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

public class Context {

    private static ExchangeRateService exchangeRateService;
    public static final String EUROPEAN_BANK_API_PROPERTY = "european.bank.api";
    public static final String CASSANDRA_PORT_PROPERTY = "cassandra.port";

    private static Gson gsonParser;

    public static void initExchangeRate() {

        try {
            String europeanBankApiEndpoint = Play.configuration.getProperty(EUROPEAN_BANK_API_PROPERTY);
            URI europeanCentralBankUri = new URI(europeanBankApiEndpoint);
            DefaultRestResourceSupplier restResourceSupplier = new DefaultRestResourceSupplier(europeanCentralBankUri);
            CurrencyApiClient europeanBankApi = new EuropeanCentralBankApiClient(restResourceSupplier);

            ExchangeRateDao exchangeRateDao = new ExchangeRateDaoImpl();
            Integer cassandraPort = new Integer(Play.configuration.getProperty(CASSANDRA_PORT_PROPERTY));
            exchangeRateDao.init(cassandraPort);


            exchangeRateService = new EuropeanCentralBankExchangeRateService(europeanBankApi, exchangeRateDao);
        } catch (URISyntaxException e) {
            Logger.error("Invalid URI - cannot instantiate Exchange Rate Api or DAO", e);
        }
    }

    public static ExchangeRateService getExchangeRateService() {
        if (exchangeRateService == null) {
            initExchangeRate();
        }
        return exchangeRateService;
    }

    public static Gson getGsonParser() {
        if (gsonParser == null) {
            GsonBuilder gson = new GsonBuilder();
            gson.registerTypeAdapter(Date.class, new DateSerializer());
            return gson.create();
        }

        return gsonParser;
    }

}
