package europeancentralbank.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExchangeRate", namespace = "http://www.ecb.int/vocabulary/2002-08-01/eurofxref")
public class ExchangeRate {

    @XmlAttribute
    protected String currency;
    @XmlAttribute
    protected Double rate;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String value) {
        this.currency = value;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double value) {
        this.rate = value;
    }

}