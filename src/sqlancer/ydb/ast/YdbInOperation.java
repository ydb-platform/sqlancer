package sqlancer.ydb.ast;

import sqlancer.ydb.YdbType;

import java.util.List;

public class YdbInOperation implements YdbExpression {

    private final YdbExpression expr;
    private final List<YdbExpression> listElements;
    private final boolean isTrue;

    public YdbInOperation(YdbExpression expr, List<YdbExpression> listElements, boolean isTrue) {
        this.expr = expr;
        this.listElements = listElements;
        this.isTrue = isTrue;
    }

    public YdbExpression getExpr() {
        return expr;
    }

    public List<YdbExpression> getListElements() {
        return listElements;
    }

    public boolean isTrue() {
        return isTrue;
    }

    @Override
    public YdbType getExpressionType() {
        return YdbType.bool();
    }
}
