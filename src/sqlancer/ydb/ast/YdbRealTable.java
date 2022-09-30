package sqlancer.ydb.ast;

import sqlancer.ydb.YdbSchema.YdbTable;

import java.util.List;
import java.util.stream.Collectors;

public class YdbRealTable implements YdbRealSource {

    YdbTable table;

    public YdbTable getTable() {
        return table;
    }

    public YdbRealTable(YdbTable table) {
        this.table = table;
    }

    @Override
    public List<YdbColumnNode> getSourceColumns() {
        return table.getColumns().stream().map(t -> new YdbRealColumn(this, t.getName(), t.getType()))
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return "`" + table.getFullPath() + "`";
    }

}
