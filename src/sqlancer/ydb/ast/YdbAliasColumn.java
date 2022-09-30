package sqlancer.ydb.ast;

import sqlancer.ydb.YdbSchema;
import sqlancer.ydb.YdbType;

public class YdbAliasColumn implements YdbColumnNode {

    YdbExpression realExpression;

    String alias;

    public YdbExpression getRealExpression() {
        return realExpression;
    }

    public String getAlias() {
        return alias;
    }

    public YdbAliasColumn(YdbExpression realExpression, String alias) {
        this.realExpression = realExpression;
        this.alias = alias;
    }

    @Override
    public YdbType getExpressionType() {
        return realExpression.getExpressionType();
    }

    @Override
    public String getName() {
        return alias;
    }

    @Override
    public YdbType getType() {
        return realExpression.getExpressionType();
    }

}
