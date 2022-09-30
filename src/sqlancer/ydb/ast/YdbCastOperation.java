package sqlancer.ydb.ast;

import sqlancer.ydb.YdbType;

public class YdbCastOperation implements YdbExpression {

    private final YdbExpression expression;
    private final YdbType type;

    public YdbCastOperation(YdbExpression expression, YdbType type) {
        if (expression == null) {
            throw new AssertionError();
        }
        this.expression = expression;
        this.type = type;
    }

    @Override
    public YdbType getExpressionType() {
        return type;
    }

    public YdbExpression getExpression() {
        return expression;
    }

    public YdbType getType() {
        return type;
    }

}
