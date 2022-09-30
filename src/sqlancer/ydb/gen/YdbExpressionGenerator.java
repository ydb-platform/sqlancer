package sqlancer.ydb.gen;

import sqlancer.IgnoreMeException;
import sqlancer.Randomly;
import sqlancer.common.gen.ExpressionGenerator;
import sqlancer.ydb.YdbSchema.YdbColumn;
import sqlancer.ydb.YdbProvider.YdbGlobalState;
import sqlancer.ydb.YdbType;
import sqlancer.ydb.ast.YdbOrderByTerm.YdbOrder;
import sqlancer.ydb.ast.YdbFunction.YdbFunctionWithResult;
import sqlancer.ydb.ast.YdbPrefixOperation.PrefixOperator;
import sqlancer.ydb.ast.YdbBinaryLogicalOperation.BinaryLogicalOperator;
import sqlancer.ydb.ast.YdbAggregate.YdbAggregateFunction;
import sqlancer.ydb.ast.YdbBinaryArithmeticOperation.YdbBinaryOperator;
import sqlancer.ydb.ast.YdbPostfixOperation.PostfixOperator;
import sqlancer.ydb.ast.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class YdbExpressionGenerator implements ExpressionGenerator<YdbExpression> {

    private final int maxDepth;

    private final Randomly r;

    private List<YdbColumnNode> columns;

    private boolean expectedResult;

    private YdbGlobalState globalState;

    private boolean allowAggregateFunctions;

    public YdbExpressionGenerator(YdbGlobalState globalState) {
        this.r = globalState.getRandomly();
        this.maxDepth = globalState.getOptions().getMaxExpressionDepth();
        this.globalState = globalState;
    }

    public YdbExpressionGenerator setColumns(List<YdbColumnNode> columns) {
        this.columns = columns;
        return this;
    }

    public List<YdbColumnNode> getColumns() {
        return columns;
    }

    public Randomly getRandomly() {
        return r;
    }

    public YdbExpression generateExpression(int depth) {
        return generateExpression(depth, YdbType.getRandom());
    }

    public List<YdbExpression> generateOrderBy() {
        List<YdbExpression> orderBys = Randomly.nonEmptySubset(columns).stream()
                .map(t -> new YdbOrderByTerm(t, YdbOrder.getRandomOrder()))
                .collect(Collectors.toList());

        return orderBys;
    }

    private enum BooleanExpression {
        POSTFIX_OPERATOR, NOT, BINARY_LOGICAL_OPERATOR, BINARY_COMPARISON, FUNCTION, CAST, LIKE, IN_OPERATION
    }

    private YdbExpression generateFunctionWithKnownResult(int depth, YdbType type) {
        List<YdbFunctionWithResult> functions = Stream.of(YdbFunction.YdbFunctionWithResult.values())
                .filter(f -> f.supportsReturnType(type)).collect(Collectors.toList());
        if (functions.isEmpty()) {
            throw new AssertionError("no function with return type: " + type.toString());
        }
        YdbFunctionWithResult randomFunction = Randomly.fromList(functions);
        int nrArgs = randomFunction.getNrArgs();
        if (randomFunction.isVariadic()) {
            nrArgs += Randomly.smallNumber();
        }
        YdbType[] argTypes = randomFunction.getInputTypesForReturnType(type, nrArgs);
        YdbExpression[] args = new YdbExpression[nrArgs];
        do {
            for (int i = 0; i < args.length; i++) {
                args[i] = generateExpression(depth + 1, argTypes[i]);
            }
        } while (!randomFunction.checkArguments(args));
        return new YdbFunction(randomFunction, type, args);
    }

    private YdbExpression generateBooleanExpression(int depth) {
        List<BooleanExpression> validOptions = new ArrayList<>(Arrays.asList(BooleanExpression.values()));
        BooleanExpression option = Randomly.fromList(validOptions);
        switch (option) {
        case POSTFIX_OPERATOR:
            PostfixOperator random = PostfixOperator.getRandom();
            return YdbPostfixOperation.create(generateExpression(depth + 1, Randomly.fromOptions(random.getInputDataTypes())), random);
        case IN_OPERATION:
            return inOperation(depth + 1);
        case NOT:
            return new YdbPrefixOperation(generateExpression(depth + 1, YdbType.bool()), PrefixOperator.NOT);
        case BINARY_LOGICAL_OPERATOR:
            YdbExpression first = generateExpression(depth + 1, YdbType.bool());
            int nr = Randomly.smallNumber() + 1;
            for (int i = 0; i < nr; i++) {
                first = new YdbBinaryLogicalOperation(
                        first,
                        generateExpression(depth + 1, YdbType.bool()),
                        BinaryLogicalOperator.getRandom());
            }
            return first;
        case BINARY_COMPARISON:
            YdbType dataType = getMeaningfulType();
            return generateComparison(depth, dataType);
        case CAST:
            return new YdbCastOperation(generateExpression(depth + 1), YdbType.bool());
        case FUNCTION:
            return generateFunction(depth + 1, YdbType.bool());
        case LIKE:
            return new YdbLikeOperation(
                    generateExpression(depth + 1, YdbType.string()),
                    generateExpression(depth + 1, YdbType.string()));
        default:
            throw new AssertionError();
        }
    }

    private YdbType getMeaningfulType() {
        // make it more likely that the expression does not only consist of constant
        // expressions
        if (Randomly.getBooleanWithSmallProbability() || columns == null || columns.isEmpty()) {
            return YdbType.getRandom();
        } else {
            return Randomly.fromList(columns).getType();
        }
    }

    private YdbExpression generateFunction(int depth, YdbType type) {
        return generateFunctionWithKnownResult(depth, type);
    }

    private YdbExpression generateComparison(int depth, YdbType dataType) {
        YdbExpression leftExpr = generateExpression(depth + 1, dataType);
        YdbExpression rightExpr = generateExpression(depth + 1, dataType);
        return getComparison(leftExpr, rightExpr);
    }

    private YdbExpression getComparison(YdbExpression leftExpr, YdbExpression rightExpr) {
        return new YdbBinaryComparisonOperation(
                leftExpr,
                rightExpr,
                YdbBinaryComparisonOperation.YdbBinaryComparisonOperator.getRandom()
        );
    }

    private YdbExpression inOperation(int depth) {
        YdbType type = YdbType.getRandom();
        YdbExpression leftExpr = generateExpression(depth + 1, type);
        List<YdbExpression> rightExpr = new ArrayList<>();
        for (int i = 0; i < Randomly.smallNumber() + 1; i++) {
            rightExpr.add(generateExpression(depth + 1, type));
        }
        return new YdbInOperation(leftExpr, rightExpr, Randomly.getBoolean());
    }

    public static YdbExpression generateExpression(YdbGlobalState globalState, YdbType type) {
        return new YdbExpressionGenerator(globalState).generateExpression(0, type);
    }

    public YdbExpression generateExpression(int depth, YdbType originalType) {
        if (originalType == null) {
            System.out.println("fmkwlmflkew");
        }
        return generateExpressionInternal(depth, originalType);
    }

    private YdbExpression generateExpressionInternal(int depth, YdbType dataType) throws AssertionError {
        if (allowAggregateFunctions && Randomly.getBoolean()) {
            allowAggregateFunctions = false; // aggregate function calls cannot be nested
            return getAggregate(dataType);
        }
        if (Randomly.getBooleanWithRatherLowProbability() || depth > maxDepth) {
            // generic expression
            if (Randomly.getBoolean() || depth > maxDepth) {
                if (Randomly.getBooleanWithRatherLowProbability()) {
                    return generateConstant(r, dataType);
                } else {
                    if (filterColumns(dataType).isEmpty()) {
                        return generateConstant(r, dataType);
                    } else {
                        return createColumnOfType(dataType);
                    }
                }
            } else {
                return new YdbCastOperation(generateExpression(depth + 1), dataType);
            }
        } else {
            switch (dataType.typeClass) {
                case BOOL:
                    return generateBooleanExpression(depth);
                case FLOAT:
                case DOUBLE:
                    return generateConstant(r, dataType);
                case INT8:
                case INT16:
                case INT32:
                case INT64:
                case UINT8:
                case UINT16:
                case UINT32:
                case UINT64:
                    return generateIntExpression(depth, dataType);
                case STRING:
                    return generateStringExpression(depth);
                default:
                    throw new AssertionError(dataType);
            }
        }
    }

    private enum RangeExpression {
        BINARY_OP;
    }

    private enum TextExpression {
        CAST, FUNCTION, CONCAT
    }

    private YdbExpression generateStringExpression(int depth) {
        TextExpression option;
        List<TextExpression> validOptions = new ArrayList<>(Arrays.asList(TextExpression.values()));
        option = Randomly.fromList(validOptions);

        switch (option) {
        case CAST:
            return new YdbCastOperation(generateExpression(depth + 1), YdbType.string());
        case FUNCTION:
            return generateFunction(depth + 1, YdbType.string());
        case CONCAT:
            return generateConcat(depth);
        default:
            throw new AssertionError();
        }
    }

    private YdbExpression generateConcat(int depth) {
        YdbExpression left = generateExpression(depth + 1, YdbType.string());
        YdbExpression right = generateExpression(depth + 1, YdbType.string());
        return new YdbConcatOperation(left, right);
    }

    private enum IntExpression {
        UNARY_OPERATION, FUNCTION, CAST, BINARY_ARITHMETIC_EXPRESSION
    }

    private YdbExpression generateIntExpression(int depth, YdbType dataType) {
        IntExpression option;
        option = Randomly.fromOptions(IntExpression.values());

        YdbType.Class classType = dataType.typeClass;

        switch (option) {
        case CAST:
            return new YdbCastOperation(generateExpression(depth + 1), YdbType.type(classType));
        case UNARY_OPERATION:
            YdbExpression intExpression = generateExpression(depth + 1, YdbType.type(classType));
            switch (classType) {
                case INT8:
                    return new YdbPrefixOperation(intExpression,
                            Randomly.getBoolean() ? PrefixOperator.UNARY_PLUS_INT8 : PrefixOperator.UNARY_MINUS_INT8);
                case INT16:
                    return new YdbPrefixOperation(intExpression,
                            Randomly.getBoolean() ? PrefixOperator.UNARY_PLUS_INT16 : PrefixOperator.UNARY_MINUS_INT16);
                case INT32:
                    return new YdbPrefixOperation(intExpression,
                            Randomly.getBoolean() ? PrefixOperator.UNARY_PLUS_INT32 : PrefixOperator.UNARY_MINUS_INT32);
                case INT64:
                    return new YdbPrefixOperation(intExpression,
                            Randomly.getBoolean() ? PrefixOperator.UNARY_PLUS_INT64 : PrefixOperator.UNARY_MINUS_INT64);
                case UINT8:
                    return new YdbPrefixOperation(intExpression,
                            Randomly.getBoolean() ? PrefixOperator.UNARY_PLUS_UINT8 : PrefixOperator.UNARY_MINUS_UINT8);
                case UINT16:
                    return new YdbPrefixOperation(intExpression,
                            Randomly.getBoolean() ? PrefixOperator.UNARY_PLUS_UINT16 : PrefixOperator.UNARY_MINUS_UINT16);
                case UINT32:
                    return new YdbPrefixOperation(intExpression,
                            Randomly.getBoolean() ? PrefixOperator.UNARY_PLUS_UINT32 : PrefixOperator.UNARY_MINUS_UINT32);
                case UINT64:
                    return new YdbPrefixOperation(intExpression,
                            Randomly.getBoolean() ? PrefixOperator.UNARY_PLUS_UINT64 : PrefixOperator.UNARY_MINUS_UINT64);
            }
        case FUNCTION:
            return generateFunction(depth + 1, YdbType.type(classType));
        case BINARY_ARITHMETIC_EXPRESSION:
            YdbType.Class secondClassType = YdbType.getLowerIntType(classType);
            return new YdbBinaryArithmeticOperation(
                    generateExpression(depth + 1, YdbType.type(classType)),
                    generateExpression(depth + 1, YdbType.type(secondClassType)), YdbBinaryOperator.getRandom());
        default:
            throw new AssertionError();
        }
    }

    private YdbExpression createColumnOfType(YdbType type) {
        List<YdbColumnNode> columns = filterColumns(type);
        YdbColumnNode fromList = Randomly.fromList(columns);
        return fromList;
    }

    final List<YdbColumnNode> filterColumns(YdbType type) {
        if (columns == null) {
            return Collections.emptyList();
        } else {
            return columns.stream().filter(c -> c.getType().typeClass == type.typeClass).collect(Collectors.toList());
        }
    }

    public static YdbExpression generateConstant(Randomly r, YdbType type) {
        switch (type.typeClass) {
            case BOOL:
                return YdbConstant.createBooleanConstant(Randomly.getBoolean());
            case FLOAT:
                return YdbConstant.createFloatConstant((float) r.getFiniteDouble());
            case DOUBLE:
                return YdbConstant.createDoubleConstant(r.getFiniteDouble());
            case INT8:
                return YdbConstant.createInt8Constant(r.getInteger());
            case INT16:
                return YdbConstant.createInt16Constant(r.getInteger());
            case INT32:
                return YdbConstant.createInt32Constant(r.getInteger());
            case INT64:
                return YdbConstant.createInt64Constant(r.getInteger());
            case UINT8:
                return YdbConstant.createUInt8Constant(r.getPositiveInteger());
            case UINT16:
                return YdbConstant.createUInt16Constant(r.getPositiveInteger());
            case UINT32:
                return YdbConstant.createUInt32Constant(r.getPositiveInteger());
            case UINT64:
                return YdbConstant.createUInt64Constant(r.getPositiveInteger());
            case STRING:
                return YdbConstant.createStringConstant(r.getString());
            default:
                throw new AssertionError(type);
        }
    }

    public static YdbExpression generateExpression(YdbGlobalState globalState, List<YdbColumnNode> columns, YdbType type) {
        return new YdbExpressionGenerator(globalState).setColumns(columns).generateExpression(0, type);
    }

    public static YdbExpression generateExpression(YdbGlobalState globalState, List<YdbColumnNode> columns) {
        return new YdbExpressionGenerator(globalState).setColumns(columns).generateExpression(0);
    }

    public List<YdbExpression> generateExpressions(int nr) {
        List<YdbExpression> expressions = new ArrayList<>();
        for (int i = 0; i < nr; i++) {
            expressions.add(generateExpression(0));
        }
        return expressions;
    }

    public YdbExpression generateExpression(YdbType dataType) {
        return generateExpression(0, dataType);
    }

    public YdbExpressionGenerator setGlobalState(YdbGlobalState globalState) {
        this.globalState = globalState;
        return this;
    }

    public YdbExpression generateHavingClause() {
        this.allowAggregateFunctions = true;
        YdbExpression expression = generateExpression(YdbType.bool());
        this.allowAggregateFunctions = false;
        return expression;
    }

    public YdbExpression generateAggregate() {
        return getAggregate(YdbType.getRandom());
    }

    private YdbExpression getAggregate(YdbType dataType) {
        List<YdbAggregateFunction> aggregates = YdbAggregateFunction.getAggregates(dataType);
        YdbAggregateFunction agg = Randomly.fromList(aggregates);
        return generateArgsForAggregate(dataType, agg);
    }

    public YdbAggregate generateArgsForAggregate(YdbType dataType, YdbAggregateFunction agg) {
        List<YdbType> types = agg.getTypes(dataType);
        List<YdbExpression> args = new ArrayList<>();
        for (YdbType argType : types) {
            args.add(generateExpression(argType));
        }
        return new YdbAggregate(args, agg);
    }

    public YdbExpressionGenerator allowAggregates(boolean value) {
        allowAggregateFunctions = value;
        return this;
    }

    @Override
    public YdbExpression generatePredicate() {
        return generateExpression(YdbType.bool());
    }

    @Override
    public YdbExpression negatePredicate(YdbExpression predicate) {
        return new YdbPrefixOperation(predicate, YdbPrefixOperation.PrefixOperator.NOT);
    }

    @Override
    public YdbExpression isNull(YdbExpression expr) {
        return new YdbPostfixOperation(expr, PostfixOperator.IS_NULL);
    }

}
