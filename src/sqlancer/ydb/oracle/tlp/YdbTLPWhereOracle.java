package sqlancer.ydb.oracle.tlp;

import com.yandex.ydb.table.query.DataQueryResult;
import com.yandex.ydb.table.values.Value;
import sqlancer.Randomly;
import sqlancer.ydb.YdbComparatorHelper;
import sqlancer.ydb.YdbProvider.YdbGlobalState;
import sqlancer.ydb.YdbVisitor;
import sqlancer.ydb.query.YdbSelectQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class YdbTLPWhereOracle extends YdbTLPBase {

    public YdbTLPWhereOracle(YdbGlobalState state) {
        super(state);
    }

    @Override
    public void check() throws Exception {
        super.check();
        whereCheck();
    }

    protected void whereCheck() throws Exception {
        YdbSelectQuery adapter = new YdbSelectQuery(select, errors);
        List<List<Value<?>>> fullResultSet = YdbComparatorHelper.getResultSet(adapter, state);

        List<List<Value<?>>> compoundResultSet = new ArrayList<>();


        select.setWhereClause(predicate);
        compoundResultSet.addAll(YdbComparatorHelper.getResultSet(adapter, state));

        select.setWhereClause(negatedPredicate);
        compoundResultSet.addAll(YdbComparatorHelper.getResultSet(adapter, state));

        select.setWhereClause(isNullPredicate);
        compoundResultSet.addAll(YdbComparatorHelper.getResultSet(adapter, state));

        select.setWhereClause(predicate);
        System.out.println(
                "full_set_size = " + fullResultSet.size()
                + ", compound_set_size = " + compoundResultSet.size()
                + " -> " + YdbVisitor.asString(select)
        );
        YdbComparatorHelper.assumeResultSetsAreEqual(fullResultSet, compoundResultSet, adapter);
    }
}
