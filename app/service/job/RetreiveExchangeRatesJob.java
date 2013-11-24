package service.job;

import models.PlayExchangeRate;
import play.jobs.Job;
import service.Context;

import java.util.List;

public class RetreiveExchangeRatesJob extends Job {

    private String code;

    public RetreiveExchangeRatesJob(String code) {
        this.code = code;
    }

    public List<PlayExchangeRate> doJobWithResult() {
        return Context.getExchangeRateService().getExchangeRates(code);
    }
}
