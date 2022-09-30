package sqlancer.ydb.ast;

import sqlancer.Randomly;
import sqlancer.common.ast.FunctionNode;
import sqlancer.ydb.YdbType;
import sqlancer.ydb.ast.YdbAggregate.YdbAggregateFunction;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class YdbAggregate extends FunctionNode<YdbAggregateFunction, YdbExpression>
        implements YdbExpression {

    public enum YdbAggregateFunction {
        AVG(
                YdbType.int8(),
                YdbType.int16(),
                YdbType.int32(),
                YdbType.int64(),
                YdbType.uint8(),
                YdbType.uint16(),
                YdbType.uint32(),
                YdbType.uint64()
        ),
        COUNT(YdbType.uint64()), MAX, MIN,
        // STRING_AGG
        SUM(
                YdbType.int8(),
                YdbType.int16(),
                YdbType.int32(),
                YdbType.int64(),
                YdbType.uint8(),
                YdbType.uint16(),
                YdbType.uint32(),
                YdbType.uint64(),
                YdbType.float32(),
                YdbType.float64()
        );

        private YdbType[] supportedReturnTypes;

        YdbAggregateFunction(YdbType... supportedReturnTypes) {
            this.supportedReturnTypes = supportedReturnTypes.clone();
        }

        public List<YdbType> getTypes(YdbType returnType) {
            return Arrays.asList(returnType);
        }

        public boolean supportsReturnType(YdbType returnType) {
            return Arrays.asList(supportedReturnTypes).stream().anyMatch(t -> t == returnType)
                    || supportedReturnTypes.length == 0;
        }

        public static List<YdbAggregateFunction> getAggregates(YdbType type) {
            return Arrays.asList(values()).stream().filter(p -> p.supportsReturnType(type))
                    .collect(Collectors.toList());
        }

        public YdbType getRandomReturnType() {
            if (supportedReturnTypes.length == 0) {
                return Randomly.fromOptions(YdbType.getRandom());
            } else {
                return Randomly.fromOptions(supportedReturnTypes);
            }
        }

    }

    public YdbAggregate(List<YdbExpression> args, YdbAggregateFunction func) {
        super(func, args);
    }

    @Override
    public YdbType getExpressionType() {
        return null;
    }

}