package sqlancer.ydb.query;

import com.yandex.ydb.table.SessionRetryContext;
import com.yandex.ydb.table.TableClient;
import com.yandex.ydb.table.description.TableColumn;
import com.yandex.ydb.table.description.TableDescription;
import com.yandex.ydb.table.rpc.grpc.GrpcTableRpc;
import com.yandex.ydb.table.values.OptionalType;
import sqlancer.GlobalState;
import sqlancer.common.query.ExpectedErrors;
import sqlancer.ydb.YdbConnection;
import sqlancer.ydb.YdbQueryAdapter;

import java.util.List;

public class YdbCreateTableQuery extends YdbQueryAdapter {

    String fullPath;
    TableDescription tableDesc;
    ExpectedErrors errors;

    public YdbCreateTableQuery(String tablePath, TableDescription tableDesc, ExpectedErrors errors) {
        this.fullPath = tablePath;
        this.tableDesc = tableDesc;
        this.errors = errors;
    }

    @Override
    public String getLogString() {
        StringBuilder sb = new StringBuilder();

        sb.append("CREATE TABLE ");

        sb.append("`");
        sb.append(fullPath);
        sb.append("`");

        sb.append(" (");
        for (TableColumn column : tableDesc.getColumns()) {
            sb.append(column.getName());
            sb.append(" ");
            OptionalType type = (OptionalType) column.getType();
            sb.append(type.getItemType().toString());
            sb.append(", ");
        }
        sb.append("PRIMARY KEY");
        sb.append("(");
        List<String> primaryKeys = tableDesc.getPrimaryKeys();
        for (int i = 0; i < primaryKeys.size(); ++i) {
            sb.append(primaryKeys.get(i));
            if (i + 1 < primaryKeys.size()) {
                sb.append(", ");
            }
        }
        sb.append(")");
        sb.append(")");

        return sb.toString();
    }

    @Override
    public <G extends GlobalState<?, ?, YdbConnection>> boolean execute(G globalState, String... fills) throws Exception {
        boolean createStatus = true;
        try {
            SessionRetryContext ctx = globalState.getConnection().sessionRetryContext;
            ctx.supplyStatus(session -> {
                return session.createTable(fullPath, tableDesc);
            }).join().expect("create table error");
        } catch (Exception e) {
            createStatus = false;
            e.printStackTrace();
        }
        return createStatus;
    }

    @Override
    public boolean couldAffectSchema() { return true; }

    @Override
    public ExpectedErrors getExpectedErrors() { return errors; }
}
