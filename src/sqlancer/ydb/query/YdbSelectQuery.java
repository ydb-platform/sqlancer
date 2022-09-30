package sqlancer.ydb.query;

import com.yandex.ydb.table.SessionRetryContext;
import com.yandex.ydb.table.TableClient;
import com.yandex.ydb.table.query.DataQueryResult;
import com.yandex.ydb.table.rpc.grpc.GrpcTableRpc;
import com.yandex.ydb.table.transaction.TxControl;
import sqlancer.GlobalState;
import sqlancer.common.query.ExpectedErrors;
import sqlancer.ydb.YdbConnection;
import sqlancer.ydb.YdbQueryAdapter;
import sqlancer.ydb.YdbToStringVisitor;
import sqlancer.ydb.YdbVisitor;
import sqlancer.ydb.ast.YdbSelect;

public class YdbSelectQuery extends YdbQueryAdapter {

    DataQueryResult resultRows;
    YdbSelect selectQuery;
    ExpectedErrors errors;

    public YdbSelectQuery(YdbSelect selectQuery, ExpectedErrors errors) {
        this.selectQuery = selectQuery;
        this.errors = errors;
    }

    public DataQueryResult getResultSet() {
        return resultRows;
    }


    @Override
    public <G extends GlobalState<?, ?, YdbConnection>> boolean execute(G globalState, String... fills) throws Exception {
        SessionRetryContext ctx = globalState.getConnection().sessionRetryContext;
        resultRows = ctx.supplyResult(session -> {
            return session.executeDataQuery(YdbVisitor.asString(selectQuery), TxControl.serializableRw().setCommitTx(true));
        }).join().expect("select query error");
        return true;
    }
    
    @Override
    public String getLogString() {
        return YdbVisitor.asString(selectQuery);
    }
    
    @Override
    public boolean couldAffectSchema() {
        return false;
    }

    @Override
    public ExpectedErrors getExpectedErrors() {
        return errors;
    }
}
