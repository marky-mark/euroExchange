package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import play.mvc.Http;
import play.test.FunctionalTest;

import java.util.List;

import static org.hamcrest.core.Is.is;

//Problem is that the asynchronous calls, does not wait for response..the below works in intellij
//but not using "play auto-test"...
@Ignore
public class ExchangeRateTest extends FunctionalTest {

    @Test
    public void shouldFetchExchangeRatesInJson() {
        Http.Response response = asyncGET("/exchange-rate/USD");
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
        Http.Response response = asyncGET("/exchange-rate/USD/refresh");
        assertIsOk(response);
        assertContentType("application/json", response);
        assertCharset(play.Play.defaultWebEncoding, response);
        Gson gson = new GsonBuilder().create();
        List data = gson.fromJson(getContent(response), List.class);
        assertThat(data.size() > 0, is(true));
        //TODO: Test the content
    }

    @Test
    public void shouldRefreshAllAndGetExchangeRates() {
        Http.Response response = asyncGET("/exchange-rate/USD/refresh/all");
        assertThat(response.status, is(200));
        assertContentType("application/json", response);
        assertCharset(play.Play.defaultWebEncoding, response);
        Gson gson = new GsonBuilder().create();
        List data = gson.fromJson(getContent(response), List.class);
        assertThat(data.size() > 0, is(true));
        //TODO: Test the content
    }

    Http.Response asyncGET(String url) {

        Http.Response result = GET(url);

        while (result.status == 200 && StringUtils.isEmpty(result.out.toString())) {
            sleep(1);
        }

        return result;

    }

    //TODO: test non existing currency codes

}
