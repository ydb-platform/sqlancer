package sqlancer.ydb.ast;

import sqlancer.ydb.YdbSchema.YdbTable;

import java.util.List;
import java.util.stream.Collectors;

public class YdbAliasTable implements YdbAliasSource {

    YdbTable realTable;

    String alias;

    public String getAlias() {
        return alias;
    }

    public YdbTable getRealTable() {
        return realTable;
    }

    public YdbAliasTable(YdbTable realTable, String alias) {
        this.realTable = realTable;
        this.alias = alias;
    }

    @Override
    public List<YdbColumnNode> getSourceColumns() {
        return realTable.getColumns().stream().map(t -> new YdbRealColumn(this, t.getName(), t.getType()))
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return alias;
    }
}
