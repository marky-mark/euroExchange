package europeancentralbank;

public class EuropeanCentralBankApiException extends RuntimeException {

    private String path;

    public EuropeanCentralBankApiException(String path, Throwable e) {
        super(e);
        this.path = path;
    }

    public EuropeanCentralBankApiException(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
