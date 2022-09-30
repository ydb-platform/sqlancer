package sqlancer.ydb;


import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import sqlancer.DBMSSpecificOptions;
import sqlancer.OracleFactory;
import sqlancer.common.oracle.TestOracle;
import sqlancer.ydb.YdbOptions.YdbOracleFactory;
import sqlancer.ydb.YdbProvider.YdbGlobalState;
import sqlancer.ydb.oracle.tlp.YdbTLPWhereOracle;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Parameters(separators = "=", commandDescription = "" +
        "Ydb (" +
            "default port: " + YdbOptions.DEFAULT_PORT + ", " +
            "default host: " + YdbOptions.DEFAULT_HOST + ", " +
            "default root: " + YdbOptions.DEFAULT_ROOT_DIR +
        ")")
public class YdbOptions implements DBMSSpecificOptions<YdbOracleFactory> {
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 2135;
    public static final String DEFAULT_ROOT_DIR = "/slice/db";

    @Parameter(names = "--oracle")
    public List<YdbOracleFactory> oracle = Arrays.asList(YdbOracleFactory.TLPWhere);

    @Parameter(names = { "--test-joins" }, description = "Allow the generation of JOIN clauses", arity = 1)
    public boolean testJoins = true;

    @Parameter(names = "--endpoint", description = "Specifies the URL for connecting to the Ydb server", arity = 1)
    public String endpoint = String.format(
            "grpc://%s:%d",
            YdbOptions.DEFAULT_HOST,
            YdbOptions.DEFAULT_PORT
    );

    @Parameter(names = "--root", description = "Specifies the root database", arity = 1)
    public String rootDir = DEFAULT_ROOT_DIR;

    @Parameter(names = "--token", description = "Access Token", arity = 1)
    public String accessToken = "";

    public boolean hasToken() {
        return !accessToken.isEmpty();
    }

    public String getConnectionURL() {
        return endpoint + "/?database=" + rootDir;
    }

    public enum YdbOracleFactory implements OracleFactory<YdbGlobalState> {
        TLPWhere {
            @Override
            public TestOracle create(YdbGlobalState globalState) throws SQLException {
                return new YdbTLPWhereOracle(globalState);
            }
        },
        TLPDistinct {
            @Override
            public TestOracle create(YdbGlobalState globalState) throws SQLException {
                return null;
//                return new YdbTLPDistinctOracle(globalState);
            }
        },
        TLPGroupBy {
            @Override
            public TestOracle create(YdbGlobalState globalState) throws SQLException {
                return null;
//                return new YdbTLPGroupByOracle(globalState);
            }
        },
        TLPAggregate {
            @Override
            public TestOracle create(YdbGlobalState globalState) throws SQLException {
                return null;
//                return new YdbTLPAggregateOracle(globalState);
            }
        },
        TLPHaving {
            @Override
            public TestOracle create(YdbGlobalState globalState) throws SQLException {
                return null;
//                return new YdbTLPHavingOracle(globalState);
            }
        };

    }

    @Override
    public List<YdbOracleFactory> getTestOracleFactory() {
        return oracle;
    }
}
