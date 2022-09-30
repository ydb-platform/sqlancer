package sqlancer.ydb.ast;

import sqlancer.ydb.YdbType;

public interface YdbExpression {

    YdbType getExpressionType();

}
