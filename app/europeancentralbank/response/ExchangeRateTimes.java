package europeancentralbank.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExchangeRateTimes", namespace = "http://www.ecb.int/vocabulary/2002-08-01/eurofxref", propOrder = {
        "exchangeRates"
})
public class ExchangeRateTimes {

    @XmlElement(name = "Cube", namespace = "http://www.ecb.int/vocabulary/2002-08-01/eurofxref", required = true)
    protected List<ExchangeRate> exchangeRates;
    @XmlAttribute
    @XmlSchemaType(name = "date")
    protected Date time;

    public List<ExchangeRate> getExchangeRates() {
        if (exchangeRates == null) {
            exchangeRates = new ArrayList<ExchangeRate>();
        }
        return this.exchangeRates;
    }

    public Date getDate() {
        return time;
    }

    public void setDate(Date value) {
        this.time = value;
    }


}
