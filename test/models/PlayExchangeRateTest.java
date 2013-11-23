package models;

import org.joda.time.DateTime;
import org.junit.Test;
import play.test.UnitTest;

import static org.hamcrest.core.Is.is;

public class PlayExchangeRateTest extends UnitTest {

    @Test
    public void shouldBeLessThan() {

        DateTime dateTime1 = new DateTime();
        dateTime1 = dateTime1.minusDays(1);
        PlayExchangeRate exchangeRate1 = new PlayExchangeRate(dateTime1.toDate());

        DateTime DateTime2 = new DateTime();
        PlayExchangeRate exchangeRate2 = new PlayExchangeRate(DateTime2.toDate());

        assertThat(exchangeRate1.compareTo(exchangeRate2), is(-1));
    }

    @Test
    public void shouldBeTheSame() {

        DateTime dateTime1 = new DateTime();
        PlayExchangeRate exchangeRate1 = new PlayExchangeRate(dateTime1.toDate());

        DateTime DateTime2 = new DateTime();
        PlayExchangeRate exchangeRate2 = new PlayExchangeRate(DateTime2.toDate());

        assertThat(exchangeRate1.compareTo(exchangeRate2), is(0));
    }

    @Test
    public void shouldBeMoreThan() {

        DateTime dateTime1 = new DateTime();
        dateTime1 = dateTime1.minusDays(1);
        PlayExchangeRate exchangeRate1 = new PlayExchangeRate(dateTime1.toDate());

        DateTime DateTime2 = new DateTime();
        PlayExchangeRate exchangeRate2 = new PlayExchangeRate(DateTime2.toDate());

        assertThat(exchangeRate2.compareTo(exchangeRate1), is(1));
    }

    //TODO: cases where date is null comparison
}
