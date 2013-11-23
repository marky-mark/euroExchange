package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;
import play.mvc.Http;
import play.test.FunctionalTest;

import java.util.List;

public class ExchangeRateTest extends FunctionalTest {

    @Test
    public void shouldFetchExchangeRatesInJson() {
        Http.Response response = GET("/exchange-rate/USD");
        assertIsOk(response);
        assertContentType("application/json", response);
        assertCharset(play.Play.defaultWebEncoding, response);
        Gson gson = new GsonBuilder().create();
        List data = gson.fromJson(getContent(response), List.class);
        //TODO: Test the content
    }

}
