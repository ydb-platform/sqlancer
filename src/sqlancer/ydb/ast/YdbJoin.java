package sqlancer.ydb.ast;

import java.util.ArrayList;
import java.util.List;

public class YdbJoin implements YdbSource {

    public enum JoinType {
        CROSS, LEFT, RIGHT, FULL
    }

    List<YdbAliasTable> joinTables;
    List<JoinType> joinTypes;
    List<YdbExpression> joinConditions;

    public List<YdbAliasTable> getJoinTables() {
        return joinTables;
    }

    public List<JoinType> getJoinTypes() {
        return joinTypes;
    }

    public List<YdbExpression> getJoinConditions() {
        return joinConditions;
    }

    public YdbJoin(List<YdbAliasTable> joinTables, List<JoinType> joinTypes, List<YdbExpression> joinConditions) {
        this.joinTables = joinTables;
        this.joinTypes = joinTypes;
        this.joinConditions = joinConditions;
    }

    @Override
    public List<YdbColumnNode> getSourceColumns() {
        List<YdbColumnNode> columns = new ArrayList<>();
        for (YdbAliasTable table : joinTables) {
            columns.addAll(table.getSourceColumns());
        }
        return columns;
    }

    @Override
    public String getName() {
        return null;
    }

}
