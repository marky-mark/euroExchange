package europeancentralbank.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Taken from the URL below
 * http://code.google.com/p/seamlets/source/browse/trunk/bieban/bieban/ejb/src/main/java/de/bieban/xml/gesmes/?r=392
 * This is usually genrated by an XSD using jaxb
 * Renamed classes and some tidy up
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "europeanCentralBankExchange", propOrder = {
        "subject",
        "sender",
        "exchangeRateWrapper"
})
public class EuropeanCentralBankExchange {

    @XmlElement(required = true)
    protected String subject;
    @XmlElement(name = "Sender", required = true)
    protected SenderType sender;
    @XmlElement(name = "Cube", namespace = "http://www.ecb.int/vocabulary/2002-08-01/eurofxref", required = true)
    protected ExchangeRateWrapper exchangeRateWrapper;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String value) {
        this.subject = value;
    }

    public SenderType getSender() {
        return sender;
    }

    public void setSender(SenderType value) {
        this.sender = value;
    }

    public ExchangeRateWrapper getExchangeRateWrapper() {
        return exchangeRateWrapper;
    }

    public void setExchangeRateWrapper(ExchangeRateWrapper value) {
        this.exchangeRateWrapper = value;
    }

}