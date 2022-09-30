package sqlancer.ydb.ast;

import sqlancer.ydb.YdbType;

public class YdbExpressionAlias implements YdbExpression {

    YdbExpression expression;

    String alias;

    public YdbExpression getExpression() {
        return expression;
    }

    public String getAlias() {
        return alias;
    }

    public YdbExpressionAlias(YdbExpression expression, String alias) {
        this.expression = expression;
        this.alias = alias;
    }

    @Override
    public YdbType getExpressionType() {
        return expression.getExpressionType();
    }

}
