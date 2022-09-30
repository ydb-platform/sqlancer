package sqlancer.ydb.gen;

import com.yandex.ydb.table.values.Value;
import sqlancer.Randomly;
import sqlancer.common.query.ExpectedErrors;
import sqlancer.mongodb.MongoDBProvider;
import sqlancer.mongodb.MongoDBQueryAdapter;
import sqlancer.ydb.YdbProvider;
import sqlancer.ydb.YdbProvider.YdbGlobalState;
import sqlancer.ydb.YdbQueryAdapter;
import sqlancer.ydb.YdbSchema;
import sqlancer.ydb.YdbSchema.YdbColumn;
import sqlancer.ydb.YdbValue;
import sqlancer.ydb.query.YdbUpsertQuery;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class YdbUpsertGenerator {
    private StringBuilder sb;
    private static ExpectedErrors errors;

    static {
        errors = ExpectedErrors.from();
    }

    public YdbUpsertGenerator() {
        this.sb = new StringBuilder();
    }

    public static YdbQueryAdapter getQuery(YdbGlobalState ydbGlobalState) {
        return new YdbUpsertGenerator().create(ydbGlobalState);
    }

    public YdbUpsertQuery create(YdbGlobalState globalState) {
        YdbSchema.YdbTable table = globalState.getSchema().getRandomTable(t -> !t.isView());
        List<YdbColumn> columns = table.getNonEmptySubsetWithAllPrimaryColumns();

        sb.append("UPSERT INTO ");
        sb.append("`");
        sb.append(table.getFullPath());
        sb.append("`");
        sb.append("(");
        sb.append(columns.stream().map(c -> c.getName()).collect(Collectors.joining(", ")));
        sb.append(")");
        sb.append(" VALUES ");
        insertColumns(columns);

        return new YdbUpsertQuery(sb.toString(), errors);
    }

    protected void insertColumns(List<YdbColumn> columns) {
        for (int nrRows = 0; nrRows < Randomly.smallNumber() + 1; nrRows++) {
            if (nrRows != 0) {
                sb.append(", ");
            }
            sb.append("(");
            for (int nrColumn = 0; nrColumn < columns.size(); nrColumn++) {
                if (nrColumn != 0) {
                    sb.append(", ");
                }
                Value<?> constant = YdbValue.generateConstant(columns.get(nrColumn).getType().getYdbType());
                sb.append(YdbValue.generateStringFrom(constant));
            }
            sb.append(")");
        }
    }

}
