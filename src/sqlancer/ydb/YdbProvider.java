package sqlancer.ydb;

import com.google.auto.service.AutoService;

import com.yandex.ydb.table.SchemeClient;
import com.yandex.ydb.table.rpc.grpc.GrpcSchemeRpc;
import sqlancer.*;
import sqlancer.common.log.LoggableFactory;
import sqlancer.common.query.Query;
import sqlancer.mongodb.MongoDBProvider;
import sqlancer.ydb.YdbProvider.YdbGlobalState;
import sqlancer.ydb.gen.YdbTableGenerator;
import sqlancer.ydb.gen.YdbUpsertGenerator;
import sqlancer.ydb.query.YdbUpsertQuery;

import java.util.stream.Collectors;

import static java.lang.System.exit;

@AutoService(DatabaseProvider.class)
public class YdbProvider extends ProviderAdapter<YdbGlobalState, YdbOptions, YdbConnection> {

    public YdbProvider() {
        super(YdbGlobalState.class, YdbOptions.class);
    }

    public enum Action implements AbstractAction<YdbProvider.YdbGlobalState> {
        UPSERT(YdbUpsertGenerator::getQuery);

        private final YdbQueryProvider<YdbProvider.YdbGlobalState> queryProvider;

        Action(YdbQueryProvider<YdbProvider.YdbGlobalState> queryProvider) {
            this.queryProvider = queryProvider;
        }

        @Override
        public Query<YdbConnection> getQuery(YdbProvider.YdbGlobalState globalState) throws Exception {
            return queryProvider.getQuery(globalState);
        }
    }

    public static int mapActions(YdbProvider.YdbGlobalState globalState, YdbProvider.Action a) {
        Randomly r = globalState.getRandomly();
        switch (a) {
            case UPSERT:
                return r.getInteger(0, globalState.getOptions().getMaxNumberInserts());
            default:
                throw new AssertionError(a);
        }
    }

    public static class YdbGlobalState extends GlobalState<YdbOptions, YdbSchema, YdbConnection> {

        private YdbOptions ydbOptions;

        @Override
        protected void executeEpilogue(Query<?> q, boolean success, ExecutionTimer timer) throws Exception {
            boolean logExecutionTime = getOptions().logExecutionTime();
            if (success && getOptions().printSucceedingStatements()) {
                System.out.println(q.getLogString());
            }
            if (logExecutionTime) {
                getLogger().writeCurrent("// " + timer.end().asString());
            }
            if (q.couldAffectSchema()) {
                updateSchema();
            }
        }

        public void setYdbOptions(YdbOptions options) {
            ydbOptions = options;
        }

        public YdbOptions getYdbOptions() {
            return ydbOptions;
        }

        public String getOracleName() {
            return String.join("_", ydbOptions.oracle.stream().map(o -> o.toString()).collect(Collectors.toList()));
        }

        @Override
        public String getDatabaseName() {
            return ydbOptions.rootDir + "/" + super.getDatabaseName();
        }

        public String getInternalDatabasePath() {
            return super.getDatabaseName();
        }

        @Override
        protected YdbSchema readSchema() {
            return new YdbSchema(getConnection(), getDatabaseName());
        }

    }

    @Override
    public YdbConnection createDatabase(YdbGlobalState globalState) throws Exception {
        YdbOptions ydbOptions = globalState.getDbmsSpecificOptions();
        globalState.setYdbOptions(ydbOptions);

        YdbConnection connection = new YdbConnection(ydbOptions);

        YdbDatabaseDeleter deleter = new YdbDatabaseDeleter(connection);
        deleter.deleteFolder(globalState.getDatabaseName());

        try {
            connection.schemeClient.makeDirectory(globalState.getDatabaseName()).join().expect("create directory error");
        } catch (Exception e) {}

        return connection;
    }

    @Override
    public String getDBMSName() {
        return "ydb";
    }

    @Override
    public LoggableFactory getLoggableFactory() {
        return new YdbLoggableFactory();
    }

    @Override
    protected void checkViewsAreValid(YdbGlobalState globalState) {

    }

    @Override
    public void generateDatabase(YdbGlobalState globalState) throws Exception {
        for (int i = 0; i < Randomly.fromOptions(4, 5, 6); i++) {
            boolean success;
            do {
                YdbQueryAdapter query = new YdbTableGenerator().getQuery(globalState);
                success = globalState.executeStatement(query);
            } while (!success);
        }
        StatementExecutor<YdbProvider.YdbGlobalState, YdbProvider.Action> se = new StatementExecutor<>(globalState, YdbProvider.Action.values(),
                YdbProvider::mapActions, (q) -> {
            if (globalState.getSchema().getDatabaseTables().isEmpty()) {
                throw new IgnoreMeException();
            }
        });
        se.executeStatements();
    }

}