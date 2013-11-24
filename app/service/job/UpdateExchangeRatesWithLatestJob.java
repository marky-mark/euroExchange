package service.job;

import play.jobs.Job;
import service.Context;

public class UpdateExchangeRatesWithLatestJob extends Job {

    private String code;

    public UpdateExchangeRatesWithLatestJob(String code) {
        this.code = code;
    }

    public Boolean doJobWithResult() {
        return Context.getExchangeRateService().updateExchangeRatesWithLatest(code);
    }
}
