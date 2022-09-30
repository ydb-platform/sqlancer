package sqlancer.ydb.ast;

import sqlancer.Randomly;
import sqlancer.common.ast.BinaryOperatorNode;
import sqlancer.ydb.YdbType;
import sqlancer.ydb.ast.YdbBinaryLogicalOperation.BinaryLogicalOperator;

public class YdbBinaryLogicalOperation extends BinaryOperatorNode<YdbExpression, BinaryLogicalOperator> implements YdbExpression {

    public enum BinaryLogicalOperator implements BinaryOperatorNode.Operator {
        AND {
            @Override
            public YdbConstant apply(YdbConstant left, YdbConstant right) {
                YdbConstant leftBool = left.cast(YdbType.bool());
                YdbConstant rightBool = right.cast(YdbType.bool());
                if (leftBool.isNull()) {
                    if (rightBool.isNull()) {
                        return YdbConstant.createNullConstant();
                    } else {
                        if (rightBool.asBoolean()) {
                            return YdbConstant.createNullConstant();
                        } else {
                            return YdbConstant.createFalse();
                        }
                    }
                } else if (!leftBool.asBoolean()) {
                    return YdbConstant.createFalse();
                }
                assert leftBool.asBoolean();
                if (rightBool.isNull()) {
                    return YdbConstant.createNullConstant();
                } else {
                    return YdbConstant.createBooleanConstant(rightBool.isBoolean() && rightBool.asBoolean());
                }
            }
        },
        OR {
            @Override
            public YdbConstant apply(YdbConstant left, YdbConstant right) {
                YdbConstant leftBool = left.cast(YdbType.bool());
                YdbConstant rightBool = right.cast(YdbType.bool());
                if (leftBool.isBoolean() && leftBool.asBoolean()) {
                    return YdbConstant.createTrue();
                }
                if (rightBool.isBoolean() && rightBool.asBoolean()) {
                    return YdbConstant.createTrue();
                }
                if (leftBool.isNull() || rightBool.isNull()) {
                    return YdbConstant.createNullConstant();
                }
                return YdbConstant.createFalse();
            }
        };

        public abstract YdbConstant apply(YdbConstant left, YdbConstant right);

        public static BinaryLogicalOperator getRandom() {
            return Randomly.fromOptions(values());
        }

        @Override
        public String getTextRepresentation() {
            return toString();
        }
    }

    public YdbBinaryLogicalOperation(YdbExpression left, YdbExpression right, BinaryLogicalOperator op) {
        super(left, right, op);
    }

    @Override
    public YdbType getExpressionType() {
        return YdbType.bool();
    }

}
