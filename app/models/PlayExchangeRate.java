package models;

import java.util.Date;

public class PlayExchangeRate implements Comparable {

    //Note: should really be using joda dateTime! Gson is used instead of jackson in play framework
    //      so a serialiser needs to be added.
    //Reason for not Joda Time: This application uses Date for parsing in XML and writing to DB
    //Best to keep it all the same until replace ALL Date objects to dateTIme
    private Date date;
    private Double rate;

    public PlayExchangeRate(Date date) {
        this.date = date;
    }

    public PlayExchangeRate(Date date, Double rate) {
        this.date = date;
        this.rate = rate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayExchangeRate)) return false;

        PlayExchangeRate that = (PlayExchangeRate) o;

        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (rate != null ? !rate.equals(that.rate) : that.rate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + (rate != null ? rate.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Object o) {

        //TODO: null cases

        if (o instanceof PlayExchangeRate) {
            return date.compareTo(((PlayExchangeRate) o).getDate());
        }

        return 1;
    }
}
