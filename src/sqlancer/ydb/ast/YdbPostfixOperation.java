package sqlancer.ydb.ast;

import sqlancer.Randomly;
import sqlancer.common.ast.BinaryOperatorNode.Operator;
import sqlancer.ydb.YdbType;

import java.util.Arrays;

public class YdbPostfixOperation implements YdbExpression {

    private final YdbExpression expr;
    private final PostfixOperator op;
    private final String operatorTextRepresentation;

    public enum PostfixOperator implements Operator {
        IS_NULL("IS NULL") {
            @Override
            public YdbConstant apply(YdbConstant expectedValue) {
                return YdbConstant.createBooleanConstant(expectedValue.isNull());
            }

            @Override
            public YdbType[] getInputDataTypes() {
                return Arrays.asList(
                        YdbType.bool(),
                        YdbType.string(),
                        YdbType.float32(),
                        YdbType.float64(),
                        YdbType.int8(),
                        YdbType.int16(),
                        YdbType.int32(),
                        YdbType.int64(),
                        YdbType.uint8(),
                        YdbType.uint16(),
                        YdbType.uint32(),
                        YdbType.uint64()
                ).toArray(new YdbType[0]);
            }
        },
        IS_NOT_NULL("IS NOT NULL") {

            @Override
            public YdbConstant apply(YdbConstant expectedValue) {
                return YdbConstant.createBooleanConstant(!expectedValue.isNull());
            }

            @Override
            public YdbType[] getInputDataTypes() {
                return Arrays.asList(
                        YdbType.bool(),
                        YdbType.string(),
                        YdbType.float32(),
                        YdbType.float64(),
                        YdbType.int8(),
                        YdbType.int16(),
                        YdbType.int32(),
                        YdbType.int64(),
                        YdbType.uint8(),
                        YdbType.uint16(),
                        YdbType.uint32(),
                        YdbType.uint64()
                ).toArray(new YdbType[0]);
            }
        };
        private String[] textRepresentations;

        PostfixOperator(String... textRepresentations) {
            this.textRepresentations = textRepresentations.clone();
        }

        public abstract YdbConstant apply(YdbConstant expectedValue);

        public abstract YdbType[] getInputDataTypes();

        public static PostfixOperator getRandom() {
            return Randomly.fromOptions(values());
        }

        @Override
        public String getTextRepresentation() {
            return toString();
        }
    }

    public YdbPostfixOperation(YdbExpression expr, PostfixOperator op) {
        this.expr = expr;
        this.operatorTextRepresentation = Randomly.fromOptions(op.textRepresentations);
        this.op = op;
    }

    @Override
    public YdbType getExpressionType() {
        return YdbType.bool();
    }

    public String getOperatorTextRepresentation() {
        return operatorTextRepresentation;
    }

    public static YdbExpression create(YdbExpression expr, PostfixOperator op) {
        return new YdbPostfixOperation(expr, op);
    }

    public YdbExpression getExpression() {
        return expr;
    }

}
