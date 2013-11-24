package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;
import play.mvc.Http;
import play.test.FunctionalTest;

import java.util.List;

import static org.hamcrest.core.Is.is;

public class ExchangeRateTest extends FunctionalTest {

    @Test
    public void shouldFetchExchangeRatesInJson() {
        Http.Response response = GET("/exchange-rate/USD");
        assertIsOk(response);
        assertContentType("application/json", response);
        assertCharset(play.Play.defaultWebEncoding, response);
        Gson gson = new GsonBuilder().create();
        List data = gson.fromJson(getContent(response), List.class);
        assertThat(data.size() > 0, is(true));
        //TODO: Test the content
    }

    @Test
    public void shouldRefreshAndGetExchangeRates() {
        Http.Response response = GET("/exchange-rate/USD/refresh");
        assertIsOk(response);
        assertContentType("application/json", response);
        assertCharset(play.Play.defaultWebEncoding, response);
        Gson gson = new GsonBuilder().create();
        List data = gson.fromJson(getContent(response), List.class);
        assertThat(data.size() > 0, is(true));
        //TODO: Test the content
    }

    //TODO: test non existing currency codes

}
