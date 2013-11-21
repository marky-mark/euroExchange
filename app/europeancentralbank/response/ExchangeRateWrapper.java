package europeancentralbank.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExchangeRateWrapper", namespace = "http://www.ecb.int/vocabulary/2002-08-01/eurofxref", propOrder = {
        "exchangeRateTimes"
})
public class ExchangeRateWrapper {

    @XmlElement(name = "Cube", namespace = "http://www.ecb.int/vocabulary/2002-08-01/eurofxref", required = true)
    protected List<ExchangeRateTimes> exchangeRateTimes;

    public List<ExchangeRateTimes> getExchangeRateTimes() {
        if (exchangeRateTimes == null) {
            exchangeRateTimes = new ArrayList<ExchangeRateTimes>();
        }
        return this.exchangeRateTimes;
    }

}