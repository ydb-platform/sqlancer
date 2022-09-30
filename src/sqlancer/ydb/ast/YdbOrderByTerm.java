package sqlancer.ydb.ast;

import sqlancer.Randomly;
import sqlancer.ydb.YdbType;

public class YdbOrderByTerm implements YdbExpression {

    private final YdbColumnNode column;
    private final YdbOrder order;

    public enum YdbOrder {
        ASC, DESC;

        public static YdbOrder getRandomOrder() {
            return Randomly.fromOptions(YdbOrder.values());
        }
    }

    public YdbOrderByTerm(YdbColumnNode column, YdbOrder order) {
        this.column = column;
        this.order = order;
    }

    public YdbOrder getOrder() {
        return order;
    }

    public YdbColumnNode getColumn() {
        return column;
    }

    @Override
    public YdbType getExpressionType() {
        return null;
    }

}
