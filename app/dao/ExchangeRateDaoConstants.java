package dao;

public class ExchangeRateDaoConstants {
    public static final String TABLE_NAME = "exchange_rates";
    public static final String DATE = "date";
    public static final String CODE = "code";
    public static final String RATE = "rate";
    public static final String UUID = "uuid";

    public static final String CREATE_STATEMENT =
            String.format("CREATE TABLE %s ( %s TIMEUUID, %s TIMESTAMP, %s VARCHAR, %s DOUBLE, PRIMARY KEY (%s))",
                    TABLE_NAME, UUID, DATE, CODE, RATE, UUID);

    public static final String CREATE_SECONDARY_INDEX_STATEMENT =
            String.format("CREATE INDEX exchange_rate_code ON %s(%s);",
                    TABLE_NAME, CODE);

    public static final String INSERT_STATEMENT =
            String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (now(), ?, ?, ?);",
                    TABLE_NAME, UUID, DATE, CODE, RATE);

    public static final String FIND_EXCHANGE_RATES_FOR_CODE_BETWEEN_DATES_STATEMENT =
            String.format("SELECT * FROM %s WHERE %s = ? AND %s < ? AND %s > ? ALLOW FILTERING;",
                    TABLE_NAME, CODE, DATE, DATE);
}
