package exchangerate;

import akka.actor.ActorSystem;
import dao.ExchangeRateDao;
import dao.ExchangeRateDaoImpl;
import europeancentralbank.CurrencyApiClient;
import europeancentralbank.EuropeanCentralBankApiClient;
import europeancentralbank.supplier.DefaultRestResourceSupplier;
import play.mvc.Before;

import java.net.URI;
import java.net.URISyntaxException;

public class Context {

    private static ActorSystem system;
    private static ExchangeRateService exchangeRateService;

    public static final int CASSANDRA_PORT = 9160;

    public static String EUROPEAN_BANK_ENDPOINT = "http://www.ecb.europa.eu/stats/eurofxref";

    @Before
    public static void init() throws URISyntaxException {
        system = ActorSystem.create("EuroExchange");

        ExchangeRateDao exchangeRateDao = new ExchangeRateDaoImpl();
        exchangeRateDao.init(CASSANDRA_PORT);

        DefaultRestResourceSupplier restResourceSupplier = new DefaultRestResourceSupplier(new URI(EUROPEAN_BANK_ENDPOINT));
        CurrencyApiClient europeanBankApi = new EuropeanCentralBankApiClient(restResourceSupplier);

        exchangeRateService = new EuropeanCentralBankExchangeRateServiceImpl(europeanBankApi, exchangeRateDao);
    }

    public static ActorSystem getActorSystem() {
        return system;
    }

    public static ExchangeRateService getExchangeRateService() {
        return exchangeRateService;
    }

}
