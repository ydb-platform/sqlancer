package sqlancer.ydb.ast;

import sqlancer.ydb.YdbSchema;
import sqlancer.ydb.YdbType;

public interface YdbColumnNode extends YdbExpression {

    String getName();

    YdbType getType();

}
