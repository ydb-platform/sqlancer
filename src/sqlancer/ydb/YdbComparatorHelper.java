package sqlancer.ydb;

import com.yandex.ydb.table.query.DataQueryResult;
import com.yandex.ydb.table.result.ResultSetReader;
import com.yandex.ydb.table.values.Value;
import sqlancer.IgnoreMeException;
import sqlancer.common.query.ExpectedErrors;
import sqlancer.ydb.query.YdbSelectQuery;
import sqlancer.ydb.YdbProvider.YdbGlobalState;

import javax.xml.crypto.Data;
import java.util.*;

public final class YdbComparatorHelper {

    private YdbComparatorHelper() {
    }

    private static List<List<Value<?>>> getRowList(DataQueryResult result) {
        List<List<Value<?>>> rowList = new ArrayList<>();

        for (int rset_index = 0; result != null && rset_index < result.getResultSetCount(); ++rset_index) {
            ResultSetReader rs = result.getResultSet(rset_index);
            while (rs.next()) {
                List<Value<?>> valueList = new ArrayList<>();
                for (int i = 0; i < rs.getColumnCount(); ++i) {
                    valueList.add(rs.getColumn(i).getValue());
                }
                rowList.add(valueList);
            }
        }

        return rowList;
    }

    public static List<List<Value<?>>> getResultSet(YdbSelectQuery adapter, YdbGlobalState state) throws Exception {
        ExpectedErrors errors = adapter.getExpectedErrors();
        try {
            adapter.execute(state);
            DataQueryResult result = adapter.getResultSet();
            return getRowList(result);
        } catch (Exception e) {
            if (e instanceof IgnoreMeException) {
                throw e;
            }
            if (e.getMessage() == null) {
                throw new AssertionError(adapter.getLogString(), e);
            }
            if (errors.errorIsExpected(e.getMessage())) {
                throw new IgnoreMeException();
            }
            throw new AssertionError(adapter.getLogString(), e);
        }
    }

    public static void assumeCountIsEqual(List<List<Value<?>>> resultSet, List<List<Value<?>>> secondResultSet, YdbSelectQuery originalQuery) {
        int originalSize = resultSet.size();
        if (secondResultSet.isEmpty()) {
            if (originalSize == 0) {
                return;
            } else {
                String assertMessage = String.format("The Count of the result set mismatches!\n %s",
                        originalQuery.getLogString());
                throw new AssertionError(assertMessage);
            }
        }
        int withCount = secondResultSet.size();
        if (originalSize != withCount) {
            String assertMessage = String.format("The Count of the result set mismatches!\n %s",
                    originalQuery.getLogString());
            throw new AssertionError(assertMessage);
        }
    }

    public static void assumeResultSetsAreEqual(List<List<Value<?>>> firstResultSet, List<List<Value<?>>> secondResultSet, YdbSelectQuery originalQuery) {
        if (firstResultSet.size() != secondResultSet.size()) {
            String assertionMessage = String.format("The Size of the result sets mismatch (%d and %d)!\n%s",
                    firstResultSet.size(), secondResultSet.size(), originalQuery.getLogString());
            throw new AssertionError(assertionMessage);
        }

        Set<List<Value<?>>> firstRowSet = new HashSet<>();
        Set<List<Value<?>>> secondRowSet = new HashSet<>();

        firstRowSet.addAll(firstResultSet);
        secondRowSet.addAll(secondResultSet);

        if (!firstRowSet.equals(secondRowSet)) {
            String assertMessage = String.format("The Content of the result sets mismatch!\n %s \n %s\n %s",
                    firstRowSet.toString(), secondRowSet.toString(), originalQuery.getLogString());
            throw new AssertionError(assertMessage);
        }
    }
}
