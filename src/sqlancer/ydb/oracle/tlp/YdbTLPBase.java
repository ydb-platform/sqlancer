package sqlancer.ydb.oracle.tlp;

import sqlancer.common.gen.ExpressionGenerator;
import sqlancer.common.oracle.TernaryLogicPartitioningOracleBase;
import sqlancer.common.oracle.TestOracle;
import sqlancer.ydb.YdbCommon;
import sqlancer.ydb.YdbProvider.YdbGlobalState;
import sqlancer.ydb.YdbSchema;
import sqlancer.ydb.YdbSchema.YdbTable;
import sqlancer.ydb.YdbSchema.YdbTables;
import sqlancer.ydb.ast.*;
import sqlancer.ydb.gen.YdbExpressionGenerator;
import sqlancer.ydb.oracle.YdbOracleCommon;

import java.util.List;

public class YdbTLPBase extends TernaryLogicPartitioningOracleBase<YdbExpression, YdbGlobalState> implements TestOracle {

    protected YdbSchema s;
    protected YdbTables targetTables;
    protected YdbExpressionGenerator gen;
    protected YdbSelect select;

    public YdbTLPBase(YdbGlobalState state) {
        super(state);
        YdbCommon.addCommonTypeCastErrors(errors);
        YdbCommon.addCommonParseErrors(errors);
    }

    @Override
    public void check() throws Exception {
        s = state.getSchema();
        targetTables = s.getRandomTableNonEmptyTables();
        List<YdbTable> tables = targetTables.getTables();
        generateSelectBase(tables);
    }

    protected void generateSelectBase(List<YdbTable> tables) {
        gen = new YdbExpressionGenerator(state);
        select = new YdbSelect();
        select.setSource(YdbOracleCommon.getRandomSource(gen, tables));
        select.setFetchColumns(YdbOracleCommon.getFetchColumns(select.getSource()));
        select.setWhereClause(null);
        gen.setColumns(select.getSource().getSourceColumns());
        initializeTernaryPredicateVariants();
    }

    @Override
    protected ExpressionGenerator<YdbExpression> getGen() {
        return gen;
    }



}
