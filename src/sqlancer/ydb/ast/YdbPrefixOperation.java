package sqlancer.ydb.ast;


import sqlancer.IgnoreMeException;
import sqlancer.ydb.YdbType;

import sqlancer.common.ast.BinaryOperatorNode.Operator;

public class YdbPrefixOperation implements YdbExpression {

    public enum PrefixOperator implements Operator {
        NOT("NOT", YdbType.bool()) {

            @Override
            public YdbType getExpressionType() {
                return YdbType.bool();
            }

            @Override
            protected YdbConstant getExpectedValue(YdbConstant expectedValue) {
                if (expectedValue.isNull()) {
                    return YdbConstant.createNullConstant();
                } else {
                    return YdbConstant.createBooleanConstant(!expectedValue.cast(YdbType.bool()).asBoolean());
                }
            }
        },
        UNARY_PLUS_INT8("+", YdbType.int8()) {

            @Override
            public YdbType getExpressionType() {
                return YdbType.int8();
            }

            @Override
            protected YdbConstant getExpectedValue(YdbConstant expectedValue) {
                return expectedValue;
            }

        },
        UNARY_PLUS_INT16("+", YdbType.int16()) {

            @Override
            public YdbType getExpressionType() {
                return YdbType.int16();
            }

            @Override
            protected YdbConstant getExpectedValue(YdbConstant expectedValue) {
                return expectedValue;
            }

        },
        UNARY_PLUS_INT32("+", YdbType.int32()) {

            @Override
            public YdbType getExpressionType() {
                return YdbType.int32();
            }

            @Override
            protected YdbConstant getExpectedValue(YdbConstant expectedValue) {
                return expectedValue;
            }

        },
        UNARY_PLUS_INT64("+", YdbType.int64()) {

            @Override
            public YdbType getExpressionType() {
                return YdbType.int64();
            }

            @Override
            protected YdbConstant getExpectedValue(YdbConstant expectedValue) {
                return expectedValue;
            }

        },
        UNARY_PLUS_UINT8("+", YdbType.uint8()) {

            @Override
            public YdbType getExpressionType() {
                return YdbType.uint8();
            }

            @Override
            protected YdbConstant getExpectedValue(YdbConstant expectedValue) {
                return expectedValue;
            }

        },
        UNARY_PLUS_UINT16("+", YdbType.uint16()) {

            @Override
            public YdbType getExpressionType() {
                return YdbType.uint16();
            }

            @Override
            protected YdbConstant getExpectedValue(YdbConstant expectedValue) {
                return expectedValue;
            }

        },
        UNARY_PLUS_UINT32("+", YdbType.uint32()) {

            @Override
            public YdbType getExpressionType() {
                return YdbType.uint32();
            }

            @Override
            protected YdbConstant getExpectedValue(YdbConstant expectedValue) {
                return expectedValue;
            }

        },
        UNARY_PLUS_UINT64("+", YdbType.uint64()) {

            @Override
            public YdbType getExpressionType() {
                return YdbType.uint64();
            }

            @Override
            protected YdbConstant getExpectedValue(YdbConstant expectedValue) {
                return expectedValue;
            }

        },
        UNARY_MINUS_INT8("-", YdbType.int8()) {

            @Override
            public YdbType getExpressionType() {
                return YdbType.int8();
            }

            @Override
            protected YdbConstant getExpectedValue(YdbConstant expectedValue) {
                if (expectedValue.isNull()) {
                    throw new IgnoreMeException();
                }
                try {
                    return YdbConstant.createInt8Constant(-expectedValue.asInt());
                } catch (UnsupportedOperationException e) {
                    return null;
                }
            }

        },
        UNARY_MINUS_INT16("-", YdbType.int16()) {

            @Override
            public YdbType getExpressionType() {
                return YdbType.int16();
            }

            @Override
            protected YdbConstant getExpectedValue(YdbConstant expectedValue) {
                if (expectedValue.isNull()) {
                    throw new IgnoreMeException();
                }
                try {
                    return YdbConstant.createInt16Constant(-expectedValue.asInt());
                } catch (UnsupportedOperationException e) {
                    return null;
                }
            }

        },
        UNARY_MINUS_INT32("-", YdbType.int32()) {

            @Override
            public YdbType getExpressionType() {
                return YdbType.int32();
            }

            @Override
            protected YdbConstant getExpectedValue(YdbConstant expectedValue) {
                if (expectedValue.isNull()) {
                    throw new IgnoreMeException();
                }
                try {
                    return YdbConstant.createInt32Constant(-expectedValue.asInt());
                } catch (UnsupportedOperationException e) {
                    return null;
                }
            }

        },
        UNARY_MINUS_INT64("-", YdbType.int64()) {

            @Override
            public YdbType getExpressionType() {
                return YdbType.int64();
            }

            @Override
            protected YdbConstant getExpectedValue(YdbConstant expectedValue) {
                if (expectedValue.isNull()) {
                    throw new IgnoreMeException();
                }
                try {
                    return YdbConstant.createInt64Constant(-expectedValue.asInt());
                } catch (UnsupportedOperationException e) {
                    return null;
                }
            }

        },
        UNARY_MINUS_UINT8("-", YdbType.uint8()) {

            @Override
            public YdbType getExpressionType() {
                return YdbType.uint8();
            }

            @Override
            protected YdbConstant getExpectedValue(YdbConstant expectedValue) {
                if (expectedValue.isNull()) {
                    throw new IgnoreMeException();
                }
                try {
                    return YdbConstant.createUInt8Constant(-expectedValue.asInt());
                } catch (UnsupportedOperationException e) {
                    return null;
                }
            }

        },
        UNARY_MINUS_UINT16("-", YdbType.uint16()) {

            @Override
            public YdbType getExpressionType() {
                return YdbType.uint16();
            }

            @Override
            protected YdbConstant getExpectedValue(YdbConstant expectedValue) {
                if (expectedValue.isNull()) {
                    throw new IgnoreMeException();
                }
                try {
                    return YdbConstant.createUInt16Constant(-expectedValue.asInt());
                } catch (UnsupportedOperationException e) {
                    return null;
                }
            }

        },
        UNARY_MINUS_UINT32("-", YdbType.uint32()) {

            @Override
            public YdbType getExpressionType() {
                return YdbType.uint32();
            }

            @Override
            protected YdbConstant getExpectedValue(YdbConstant expectedValue) {
                if (expectedValue.isNull()) {
                    throw new IgnoreMeException();
                }
                try {
                    return YdbConstant.createUInt32Constant(-expectedValue.asInt());
                } catch (UnsupportedOperationException e) {
                    return null;
                }
            }

        },
        UNARY_MINUS_UINT64("-", YdbType.uint64()) {

            @Override
            public YdbType getExpressionType() {
                return YdbType.uint64();
            }

            @Override
            protected YdbConstant getExpectedValue(YdbConstant expectedValue) {
                if (expectedValue.isNull()) {
                    throw new IgnoreMeException();
                }
                try {
                    return YdbConstant.createUInt64Constant(-expectedValue.asInt());
                } catch (UnsupportedOperationException e) {
                    return null;
                }
            }

        };

        private String textRepresentation;
        private YdbType[] dataTypes;

        PrefixOperator(String textRepresentation, YdbType... dataTypes) {
            this.textRepresentation = textRepresentation;
            this.dataTypes = dataTypes.clone();
        }

        public abstract YdbType getExpressionType();

        protected abstract YdbConstant getExpectedValue(YdbConstant expectedValue);

        @Override
        public String getTextRepresentation() {
            return toString();
        }

    }

    private final YdbExpression expr;
    private final PrefixOperator op;

    public YdbPrefixOperation(YdbExpression expr, PrefixOperator op) {
        this.expr = expr;
        this.op = op;
    }

    @Override
    public YdbType getExpressionType() {
        return op.getExpressionType();
    }

    public YdbType[] getInputDataTypes() {
        return op.dataTypes;
    }

    public String getTextRepresentation() {
        return op.textRepresentation;
    }

    public YdbExpression getExpression() {
        return expr;
    }

}
