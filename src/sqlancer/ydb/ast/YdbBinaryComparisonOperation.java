package sqlancer.ydb.ast;

import sqlancer.Randomly;
import sqlancer.common.ast.BinaryOperatorNode;
import sqlancer.common.ast.BinaryOperatorNode.Operator;
import sqlancer.ydb.YdbType;
import sqlancer.ydb.ast.YdbBinaryComparisonOperation.YdbBinaryComparisonOperator;

public class YdbBinaryComparisonOperation  extends BinaryOperatorNode<YdbExpression, YdbBinaryComparisonOperator> implements YdbExpression {

    public enum YdbBinaryComparisonOperator implements Operator {
        EQUALS("=") {
            @Override
            public YdbConstant getExpectedValue(YdbConstant leftVal, YdbConstant rightVal) {
                return leftVal.isEquals(rightVal);
            }
        },
        NOT_EQUALS("!=") {
            @Override
            public YdbConstant getExpectedValue(YdbConstant leftVal, YdbConstant rightVal) {
                YdbConstant isEquals = leftVal.isEquals(rightVal);
                if (isEquals.isBoolean()) {
                    return YdbConstant.createBooleanConstant(!isEquals.asBoolean());
                }
                return isEquals;
            }
        },
        LESS("<") {

            @Override
            public YdbConstant getExpectedValue(YdbConstant leftVal, YdbConstant rightVal) {
                return leftVal.isLessThan(rightVal);
            }
        },
        LESS_EQUALS("<=") {

            @Override
            public YdbConstant getExpectedValue(YdbConstant leftVal, YdbConstant rightVal) {
                YdbConstant lessThan = leftVal.isLessThan(rightVal);
                if (lessThan.isBoolean() && !lessThan.asBoolean()) {
                    return leftVal.isEquals(rightVal);
                } else {
                    return lessThan;
                }
            }
        },
        GREATER(">") {
            @Override
            public YdbConstant getExpectedValue(YdbConstant leftVal, YdbConstant rightVal) {
                YdbConstant equals = leftVal.isEquals(rightVal);
                if (equals.isBoolean() && equals.asBoolean()) {
                    return YdbConstant.createFalse();
                } else {
                    YdbConstant applyLess = leftVal.isLessThan(rightVal);
                    if (applyLess.isNull()) {
                        return YdbConstant.createNullConstant();
                    }
                    return YdbPrefixOperation.PrefixOperator.NOT.getExpectedValue(applyLess);
                }
            }
        },
        GREATER_EQUALS(">=") {

            @Override
            public YdbConstant getExpectedValue(YdbConstant leftVal, YdbConstant rightVal) {
                YdbConstant equals = leftVal.isEquals(rightVal);
                if (equals.isBoolean() && equals.asBoolean()) {
                    return YdbConstant.createTrue();
                } else {
                    YdbConstant applyLess = leftVal.isLessThan(rightVal);
                    if (applyLess.isNull()) {
                        return YdbConstant.createNullConstant();
                    }
                    return YdbPrefixOperation.PrefixOperator.NOT.getExpectedValue(applyLess);
                }
            }

        };

        private final String textRepresentation;

        @Override
        public String getTextRepresentation() {
            return textRepresentation;
        }

        YdbBinaryComparisonOperator(String textRepresentation) {
            this.textRepresentation = textRepresentation;
        }

        public abstract YdbConstant getExpectedValue(YdbConstant leftVal, YdbConstant rightVal);

        public static YdbBinaryComparisonOperator getRandom() {
            return Randomly.fromOptions(YdbBinaryComparisonOperator.values());
        }

    }

    public YdbBinaryComparisonOperation(YdbExpression left, YdbExpression right,
            YdbBinaryComparisonOperator op) {
        super(left, right, op);
    }

    @Override
    public YdbType getExpressionType() {
        return YdbType.bool();
    }

}