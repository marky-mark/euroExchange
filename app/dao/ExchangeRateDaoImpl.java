package dao;

import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.CqlResult;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.serializers.DateSerializer;
import com.netflix.astyanax.serializers.IntegerSerializer;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;
import play.Logger;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Adjusted code from netflix examples
 * https://github.com/Netflix/astyanax/blob/master/astyanax-examples/src/main/java/com/netflix/astyanax/examples/AstCQLClient.java
 * <p/>
 * Please Note: Create namespace before executing
 * CREATE KEYSPACE euroexchangerate WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
 * <p/>
 * Would prefer to use an ORM for database interactions, Kundera looks good but not supported for play < 2.0
 * https://github.com/impetus-opensource/Kundera/wiki/Getting-Started-in-5-minutes
 */
public class ExchangeRateDaoImpl implements ExchangeRateDao {

    public static final String KEYSPACE_NAME = "euro_exchange_rate";
    private AstyanaxContext<Keyspace> context;
    private Keyspace keyspace;
    private ColumnFamily<Integer, String> exchangeRateColumnFamily;

    /**
     * Setup database and connections
     */
    public void init() {
        initDatabaseConnection();
        createColumnFamily();
        createSecondaryIndex();
    }

    /**
     * Initialises the netflix connection to the database
     */
    private void initDatabaseConnection() {
        Logger.debug("initializing database");

        context = new AstyanaxContext.Builder()
                .forCluster("Exchange Rate Cluster")
                .forKeyspace(KEYSPACE_NAME)
                .withAstyanaxConfiguration(new AstyanaxConfigurationImpl()
                        .setDiscoveryType(NodeDiscoveryType.RING_DESCRIBE)
                )
                .withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl("MyConnectionPool")
                        .setPort(9160)
                        .setMaxConnsPerHost(1)
                        .setSeeds("127.0.0.1:9160")
                )
                .withAstyanaxConfiguration(new AstyanaxConfigurationImpl()
                        .setCqlVersion("3.0.0")
                        .setTargetCassandraVersion("1.2")
                )
                .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
                .buildKeyspace(ThriftFamilyFactory.getInstance());

        context.start();
        keyspace = context.getEntity();

        exchangeRateColumnFamily = ColumnFamily.newColumnFamily(
                ExchangeRateDaoConstants.TABLE_NAME,
                IntegerSerializer.get(),
                StringSerializer.get());
    }

    /**
     * create the column family to store the exchange rates in
     */
    private void createColumnFamily() {
        Logger.debug("CQL: creating tables");
        try {
            @SuppressWarnings("unused")
            OperationResult<CqlResult<Integer, String>> resultExchangeRateCreate = keyspace
                    .prepareQuery(exchangeRateColumnFamily)
                    .withCql(ExchangeRateDaoConstants.CREATE_STATEMENT)
                    .execute();
        } catch (ConnectionException e) {
            Logger.error("failed to create CF", e);
            throw new RuntimeException("failed to create CF", e);
        }
    }

    /**
     * Need to search by exchange rate code e.g. USD so need to create a secondary index for this
     */
    private void createSecondaryIndex() {
        Logger.debug("CQL: creating secondary index");
        try {
            OperationResult<CqlResult<Integer, String>> result = keyspace
                    .prepareQuery(exchangeRateColumnFamily)
                    .withCql(ExchangeRateDaoConstants.CREATE_SECONDARY_INDEX_STATEMENT)
                    .execute();
        } catch (ConnectionException e) {
            Logger.error("failed to create CF", e);
            throw new RuntimeException("failed to create CF", e);
        }
    }

    /**
     * insert into exchange rate table
     *
     * @param date Date of the exchange rate
     * @param code exchange rate code e.g. USD
     * @param rate the rate e.g. 1.201
     */
    public void insert(Date date, String code, Double rate) {

        removeTimeFromDate(date);

        try {
            @SuppressWarnings("unused")
            OperationResult<CqlResult<Integer, String>> result = keyspace
                    .prepareQuery(exchangeRateColumnFamily)
                    .withCql(ExchangeRateDaoConstants.INSERT_STATEMENT)
                    .asPreparedStatement()
                    .withByteBufferValue(date, DateSerializer.get())
                    .withStringValue(code)
                    .withDoubleValue(rate)
                    .execute();
        } catch (ConnectionException e) {
            Logger.error("failed to write data to C*", e);
            throw new RuntimeException("failed to write data to C*", e);
        }
        Logger.debug("insert ok");
    }

    /**
     * This should really be using joda time
     *
     * @param timestamp without time
     */
    private void removeTimeFromDate(Date timestamp) {
        timestamp.setHours(0);
        timestamp.setMinutes(0);
        timestamp.setSeconds(0);
    }

    public Map<Date, Double> findRatesForCodeBetweenDates(String code, Date lessThan, Date moreThan) {

        Logger.debug("finding exchange rates between dates");

        removeTimeFromDate(lessThan);
        removeTimeFromDate(moreThan);

        Map<Date, Double> rates = new TreeMap<Date, Double> ();

        try {

            OperationResult<CqlResult<Integer, String>> result
                    = keyspace.prepareQuery(exchangeRateColumnFamily)
                    .withCql(ExchangeRateDaoConstants.FIND_EXCHANGE_RATES_FOR_CODE_BETWEEN_DATES_STATEMENT)
                    .asPreparedStatement()
                    .withStringValue(code)
                    .withByteBufferValue(lessThan, DateSerializer.get())
                    .withByteBufferValue(moreThan, DateSerializer.get())
                    .execute();

            for (Row<Integer, String> row : result.getResult().getRows()) {
                ColumnList<String> cols = row.getColumns();
                Double rate = cols.getDoubleValue(ExchangeRateDaoConstants.RATE, null);
                Date date = cols.getDateValue(ExchangeRateDaoConstants.DATE, null);
                rates.put(date, rate);
            }
        } catch (ConnectionException e) {
            Logger.error("failed to read from C*", e);
            throw new RuntimeException("failed to read from C*", e);
        }

        return rates;
    }

}
