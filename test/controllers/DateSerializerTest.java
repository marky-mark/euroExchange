package controllers;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import play.test.UnitTest;

import java.lang.reflect.Type;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

public class DateSerializerTest extends UnitTest {

    private DateSerializer dateSerializer;

    @Before
    public void init() {
        dateSerializer = new DateSerializer();
    }

    @Test
    public void shouldConvertDateToSpecificFormat() {
        DateTime dateTime = new DateTime(2013, 12, 13, 7, 12);

        JsonElement jsonElement = dateSerializer.serialize(dateTime.toDate(),
                mock(Type.class),
                mock(JsonSerializationContext.class));

        assertThat(jsonElement.getAsJsonPrimitive().getAsString(), is("13-Dec-13"));

    }
}
