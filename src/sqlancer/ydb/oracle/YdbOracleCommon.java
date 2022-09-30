package sqlancer.ydb.oracle;

import com.beust.ah.A;
import sqlancer.Randomly;
import sqlancer.ydb.YdbProvider;
import sqlancer.ydb.YdbProvider.YdbGlobalState;
import sqlancer.ydb.YdbSchema;
import sqlancer.ydb.YdbSchema.YdbTable;
import sqlancer.ydb.YdbSchema.YdbTables;
import sqlancer.ydb.YdbType;
import sqlancer.ydb.ast.*;
import sqlancer.ydb.gen.YdbExpressionGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class YdbOracleCommon {

    private static AtomicInteger nextColumnAlias = new AtomicInteger(0);

    private static AtomicInteger nextTableAlias = new AtomicInteger(0);

    private static AtomicInteger nextSubqueryAlias = new AtomicInteger(0);

    public static YdbSource getRandomSource(YdbExpressionGenerator generator, List<YdbTable> tables) {
        if (Randomly.fromOptions(0, 1) == 0) {
            return generateAliasSource(generator, tables);
        }
        return generateRealSource(tables);
    }
    
    public static List<YdbColumnNode> getFetchColumns(YdbSource source) {
        if (!(source instanceof YdbJoin) && Randomly.getBooleanWithRatherLowProbability()) {
            return Arrays.asList(new YdbRealColumn(null, "*", null));
        }
        List<YdbColumnNode> fetchColumns = Randomly.nonEmptySubset(source.getSourceColumns()).stream()
                .map(t -> new YdbAliasColumn(t, String.format("a%d", nextColumnAlias.getAndIncrement())))
                .collect(Collectors.toList());
        return fetchColumns;
    }

    public static YdbSource generateRealSource(List<YdbTable> tables) {
        if (Randomly.getBooleanWithRatherLowProbability()) {
            return generateJoin(tables);
        }
        return generateRealTable(tables);
    }

    public static YdbRealTable generateRealTable(List<YdbTable> tables) {
        return new YdbRealTable(Randomly.fromList(tables));
    }

    static YdbExpression generateJoinCondition(List<YdbAliasTable> joinTables, YdbAliasTable newTable) {
        for (YdbColumnNode newColumn : newTable.getSourceColumns()) {
            for (YdbAliasTable table : joinTables) {
                for (YdbColumnNode oldColumn : table.getSourceColumns()) {
                    if (newColumn.getType().typeClass == oldColumn.getType().typeClass) {
                        return new YdbBinaryComparisonOperation(newColumn, oldColumn,
                                Randomly.fromOptions(YdbBinaryComparisonOperation.YdbBinaryComparisonOperator.EQUALS));
                    }
                }
            }
        }
        return null;
    }

    public static YdbJoin generateJoin(List<YdbTable> tables) {
        List<YdbAliasTable> joinTables = new ArrayList<>();
        List<YdbJoin.JoinType> joinTypes = new ArrayList<>();
        List<YdbExpression> joinConditions = new ArrayList<>();

        joinTables.add(generateAliasTable(tables));

        for (int i = 0; i < Randomly.fromOptions(1, 2); ++i) {
            YdbAliasTable newTable = generateAliasTable(tables);
            joinConditions.add(generateJoinCondition(joinTables, newTable));
            joinTables.add(newTable);
            if (joinConditions.get(i) != null) {
                joinTypes.add(Randomly.fromOptions(YdbJoin.JoinType.LEFT, YdbJoin.JoinType.RIGHT, YdbJoin.JoinType.FULL));
            } else {
                joinTypes.add(YdbJoin.JoinType.CROSS);
            }
        }

        return new YdbJoin(joinTables, joinTypes, joinConditions);
    }

    public static YdbSource generateAliasSource(YdbExpressionGenerator generator, List<YdbTable> tables) {
        if (Randomly.getBooleanWithSmallProbability()) {
            return generateSubquery(generator, tables);
        }
        return generateAliasTable(tables);
    }

    public static YdbAliasTable generateAliasTable(List<YdbTable> tables) {
        return new YdbAliasTable(Randomly.fromList(tables), String.format("t%d", nextTableAlias.getAndIncrement()));
    }

    public static YdbSubquery generateSubquery(YdbExpressionGenerator generator, List<YdbTable> tables) {
        YdbSelect select = new YdbSelect();

        List<YdbColumnNode> oldColumns = generator.getColumns();

        select.setSource(getRandomSource(generator, tables));
        select.setFromOptions(Randomly.fromOptions(YdbSelect.SelectType.values()));

        generator.setColumns(select.getSource().getSourceColumns());

        List<YdbColumnNode> columns = new ArrayList<>();
        for (int i = 0; i < Randomly.smallNumber() + 1; ++i) {
            YdbExpression expression = generator.generateExpression(0);
            String alias = String.format("a%d", nextColumnAlias.getAndIncrement());
            columns.add(new YdbAliasColumn(expression, alias));
        }
        select.setFetchColumns(columns);

        select.setWhereClause(generator.generatePredicate());

        if (Randomly.fromOptions(0, 1) == 0) {
            select.setLimitClause(generator.generateConstant(generator.getRandomly(), YdbType.uint64()));
            if (Randomly.fromOptions(0, 1) == 0) {
                select.setOffsetClause(generator.generateConstant(generator.getRandomly(), YdbType.uint64()));
            }
        }

        generator.setColumns(select.getFetchColumns());

        select.setOrderByClause(generator.generateOrderBy());

        generator.setColumns(oldColumns);

        return new YdbSubquery(select, String.format("sub%d", nextSubqueryAlias.getAndIncrement()));
    }

}
