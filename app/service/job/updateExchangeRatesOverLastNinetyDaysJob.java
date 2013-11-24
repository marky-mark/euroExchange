package service.job;

import play.jobs.Job;
import service.Context;

public class updateExchangeRatesOverLastNinetyDaysJob extends Job {

    private String code;

    public updateExchangeRatesOverLastNinetyDaysJob(String code) {
        this.code = code;
    }

    public Boolean doJobWithResult() {
        return Context.getExchangeRateService().updateExchangeRatesOverLastNinetyDays(code);
    }

}
