package sqlancer.ydb.ast;

import sqlancer.LikeImplementationHelper;
import sqlancer.common.ast.BinaryNode;
import sqlancer.ydb.YdbType;

public class YdbLikeOperation extends BinaryNode<YdbExpression> implements YdbExpression {

    public YdbLikeOperation(YdbExpression left, YdbExpression right) {
        super(left, right);
    }

    @Override
    public YdbType getExpressionType() {
        return YdbType.bool();
    }

    @Override
    public String getOperatorRepresentation() {
        return "LIKE";
    }

}