package sqlancer.ydb.ast;

import sqlancer.common.ast.BinaryNode;
import sqlancer.ydb.YdbType;

public class YdbConcatOperation extends BinaryNode<YdbExpression> implements YdbExpression {

    public YdbConcatOperation(YdbExpression left, YdbExpression right) {
        super(left, right);
    }

    @Override
    public YdbType getExpressionType() {
        return YdbType.string();
    }

    @Override
    public String getOperatorRepresentation() {
        return "||";
    }

}
