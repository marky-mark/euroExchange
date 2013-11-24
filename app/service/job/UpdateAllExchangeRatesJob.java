package service.job;

import service.Context;
import play.jobs.Job;

public class UpdateAllExchangeRatesJob extends Job {
    public Boolean doJobWithResult() {
        return Context.getExchangeRateService().updateAllExchangeRatesWithLatest();
    }
}