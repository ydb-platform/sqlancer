package sqlancer.ydb.ast;

import sqlancer.Randomly;
import sqlancer.common.ast.BinaryOperatorNode;
import sqlancer.common.ast.BinaryOperatorNode.Operator;
import sqlancer.ydb.YdbType;
import sqlancer.ydb.ast.YdbBinaryArithmeticOperation.YdbBinaryOperator;

import java.util.function.BinaryOperator;

public class YdbBinaryArithmeticOperation extends BinaryOperatorNode<YdbExpression, YdbBinaryOperator>
        implements YdbExpression {

    public enum YdbBinaryOperator implements Operator {

        ADDITION("+") {
            @Override
            public YdbConstant apply(YdbConstant left, YdbConstant right) {
                return applyBitOperation(left, right, (l, r) -> l + r);
            }

        },
        SUBTRACTION("-") {
            @Override
            public YdbConstant apply(YdbConstant left, YdbConstant right) {
                return applyBitOperation(left, right, (l, r) -> l - r);
            }
        },
        MULTIPLICATION("*") {
            @Override
            public YdbConstant apply(YdbConstant left, YdbConstant right) {
                return applyBitOperation(left, right, (l, r) -> l * r);
            }
        },
        DIVISION("/") {

            @Override
            public YdbConstant apply(YdbConstant left, YdbConstant right) {
                return applyBitOperation(left, right, (l, r) -> r == 0 ? -1 : l / r);

            }

        },
        MODULO("%") {
            @Override
            public YdbConstant apply(YdbConstant left, YdbConstant right) {
                return applyBitOperation(left, right, (l, r) -> r == 0 ? -1 : l % r);

            }
        };

        private String textRepresentation;

        private static YdbConstant applyBitOperation(YdbConstant left, YdbConstant right, BinaryOperator<Long> op) {
            if (left.isNull() || right.isNull()) {
                return YdbConstant.createNullConstant();
            } else {
                long leftVal = left.cast(YdbType.int64()).asInt();
                long rightVal = right.cast(YdbType.int64()).asInt();
                long value = op.apply(leftVal, rightVal);

                YdbType lType = left.getExpressionType();
                YdbType rType = right.getExpressionType();

                if (lType.typeClass == YdbType.Class.DOUBLE || rType.typeClass == YdbType.Class.DOUBLE) {
                    return YdbConstant.createDoubleConstant(value);
                } else if (lType.typeClass == YdbType.Class.FLOAT || rType.typeClass == YdbType.Class.FLOAT) {
                    return YdbConstant.createFloatConstant(value);
                } else {
                    YdbType.Class resultClass = YdbType.getResultClassInIntBinOp(lType.typeClass, rType.typeClass);
                    return YdbConstant.createIntConstant(value, resultClass);
                }
            }
        }

        YdbBinaryOperator(String textRepresentation) {
            this.textRepresentation = textRepresentation;
        }

        @Override
        public String getTextRepresentation() {
            return textRepresentation;
        }

        public abstract YdbConstant apply(YdbConstant left, YdbConstant right);

        public static YdbBinaryOperator getRandom() {
            return Randomly.fromOptions(values());
        }

    }

    public YdbBinaryArithmeticOperation(YdbExpression left, YdbExpression right,
            YdbBinaryOperator op) {
        super(left, right, op);
    }

    @Override
    public YdbType getExpressionType() {
        YdbType lType = getLeft().getExpressionType();
        YdbType rType = getRight().getExpressionType();

        if (lType.typeClass == YdbType.Class.DOUBLE || rType.typeClass == YdbType.Class.DOUBLE) {
            return YdbType.float64();
        } else if (lType.typeClass == YdbType.Class.FLOAT || rType.typeClass == YdbType.Class.FLOAT) {
            return YdbType.float32();
        } else {
            YdbType.Class resultClass = YdbType.getResultClassInIntBinOp(lType.typeClass, rType.typeClass);
            return YdbType.type(resultClass);
        }
    }

}