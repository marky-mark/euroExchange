package exchangerate;

import akka.actor.ActorSystem;
import dao.ExchangeRateDao;
import dao.ExchangeRateDaoImpl;
import europeancentralbank.CurrencyApiClient;
import europeancentralbank.EuropeanCentralBankApiClient;
import europeancentralbank.supplier.DefaultRestResourceSupplier;
import play.Logger;
import play.Play;

import java.net.URI;
import java.net.URISyntaxException;

public class Context {

    private static ActorSystem system;
    private static ExchangeRateService exchangeRateService;
    public static final String EUROPEAN_BANK_API_PROPERTY = "european.bank.api";
    public static final String CASSANDRA_PORT_PROPERTY = "cassandra.port";

    public static void init() {
        initialiseActor();
        initExchangeService();
    }

    private static void initialiseActor() {
        system = ActorSystem.create("EuroExchange");
    }

    public static ActorSystem getActorSystem() {

        if (system == null) {
            initialiseActor();
        }

        return system;
    }

    public static ExchangeRateService getExchangeRateService() {
        if (exchangeRateService == null) {
            initExchangeService();
        }

        return exchangeRateService;
    }

    public static void initExchangeService() {

        try {
            String europeanBankApiEndpoint = Play.configuration.getProperty(EUROPEAN_BANK_API_PROPERTY);
            URI europeanCentralBankUri = new URI(europeanBankApiEndpoint);
            DefaultRestResourceSupplier restResourceSupplier = new DefaultRestResourceSupplier(europeanCentralBankUri);
            CurrencyApiClient europeanBankApi = new EuropeanCentralBankApiClient(restResourceSupplier);

            ExchangeRateDao exchangeRateDao = new ExchangeRateDaoImpl();
            Integer cassandraPort = new Integer(Play.configuration.getProperty(CASSANDRA_PORT_PROPERTY));
            exchangeRateDao.init(cassandraPort);


            exchangeRateService = new EuropeanCentralBankExchangeRateServiceImpl(europeanBankApi, exchangeRateDao);
        } catch (URISyntaxException e) {
            Logger.error("Invalid URI - cannot instantiate Exchange Rate Api or DAO", e);
        }
    }

}
