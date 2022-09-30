package sqlancer.ydb.ast;

import sqlancer.ydb.YdbType;

public class YdbFunction implements YdbExpression {

    private final String func;
    private final YdbExpression[] args;
    private final YdbType returnType;
    private YdbFunctionWithResult functionWithKnownResult;

    public YdbFunction(YdbFunctionWithResult func, YdbType returnType, YdbExpression... args) {
        functionWithKnownResult = func;
        this.func = func.getName();
        this.returnType = returnType;
        this.args = args.clone();
    }

    public String getFunctionName() {
        return func;
    }

    public YdbExpression[] getArguments() {
        return args.clone();
    }

    public enum YdbFunctionWithResult {
        ABS(1, "ABS") {

            @Override
            public YdbConstant apply(YdbConstant[] evaluatedArgs, YdbExpression... args) {
                if (evaluatedArgs[0].isNull()) {
                    return YdbConstant.createNullConstant();
                } else {
                    YdbType returnType = evaluatedArgs[0].getExpressionType();
                    return YdbConstant.createIntConstant(Math.abs(evaluatedArgs[0].cast(YdbType.int64()).asInt()), returnType.typeClass);
                }
            }

            @Override
            public boolean supportsReturnType(YdbType type) {
                return  type.typeClass == YdbType.Class.INT8 ||
                        type.typeClass == YdbType.Class.INT16 ||
                        type.typeClass == YdbType.Class.INT32 ||
                        type.typeClass == YdbType.Class.INT64 ||
                        type.typeClass == YdbType.Class.UINT8 ||
                        type.typeClass == YdbType.Class.UINT16 ||
                        type.typeClass == YdbType.Class.UINT32 ||
                        type.typeClass == YdbType.Class.UINT64;

            }

            @Override
            public YdbType[] getInputTypesForReturnType(YdbType returnType, int nrArguments) {
                return new YdbType[] { returnType };
            }

        },
        LENGTH(1, "LENGTH") {
            @Override
            public YdbConstant apply(YdbConstant[] evaluatedArgs, YdbExpression... args) {
                if (evaluatedArgs[0].isNull()) {
                    return YdbConstant.createNullConstant();
                }
                String text = evaluatedArgs[0].asString();
                return YdbConstant.createIntConstant(text.length(), YdbType.Class.UINT32);
            }

            @Override
            public boolean supportsReturnType(YdbType type) {
                return type.typeClass == YdbType.Class.UINT32;
            }

            @Override
            public YdbType[] getInputTypesForReturnType(YdbType returnType, int nrArguments) {
                return new YdbType[] { YdbType.string() };
            }
        },
        SUBSTRING_2_PARAM(2, "SUBSTRING") {
            @Override
            public YdbConstant apply(YdbConstant[] evaluatedArgs, YdbExpression... args) {
                if (evaluatedArgs[0].isNull()) {
                    return YdbConstant.createNullConstant();
                }
                String text = evaluatedArgs[0].asString();
                int pos = (int) evaluatedArgs[1].asInt();
                return YdbConstant.createStringConstant(text.substring(pos));
            }

            @Override
            public boolean supportsReturnType(YdbType type) {
                return type.typeClass == YdbType.Class.STRING;
            }

            @Override
            public YdbType[] getInputTypesForReturnType(YdbType returnType, int nrArguments) {
                return new YdbType[] { YdbType.string(), YdbType.uint32() };
            }
        },
        SUBSTRING_3_PARAM(3, "SUBSTRING") {
            @Override
            public YdbConstant apply(YdbConstant[] evaluatedArgs, YdbExpression... args) {
                if (evaluatedArgs[0].isNull()) {
                    return YdbConstant.createNullConstant();
                }
                String text = evaluatedArgs[0].asString();
                int pos = (int) evaluatedArgs[1].asInt();
                int len = (int) evaluatedArgs[2].asInt();
                return YdbConstant.createStringConstant(text.substring(pos, len));
            }

            @Override
            public boolean supportsReturnType(YdbType type) {
                return type.typeClass == YdbType.Class.STRING;
            }

            @Override
            public YdbType[] getInputTypesForReturnType(YdbType returnType, int nrArguments) {
                return new YdbType[] { YdbType.string(), YdbType.uint32(), YdbType.uint32() };
            }
        },
        STARTS_WITH(2, "StartsWith") {
            @Override
            public YdbConstant apply(YdbConstant[] evaluatedArgs, YdbExpression... args) {
                if (evaluatedArgs[0].isNull()) {
                    return YdbConstant.createNullConstant();
                }
                String a = evaluatedArgs[0].asString();
                String b = evaluatedArgs[1].asString();
                return YdbConstant.createBooleanConstant(a.startsWith(b));
            }

            @Override
            public boolean supportsReturnType(YdbType type) {
                return type.typeClass == YdbType.Class.BOOL;
            }

            @Override
            public YdbType[] getInputTypesForReturnType(YdbType returnType, int nrArguments) {
                return new YdbType[] { YdbType.string(), YdbType.string() };
            }
        };

        private String functionName;
        final int nrArgs;
        private final boolean variadic;

        public YdbType[] getRandomTypes(int nr) {
            YdbType[] types = new YdbType[nr];
            for (int i = 0; i < types.length; i++) {
                types[i] = YdbType.getRandom();
            }
            return types;
        }

        YdbFunctionWithResult(int nrArgs, String functionName) {
            this.nrArgs = nrArgs;
            this.functionName = functionName;
            this.variadic = false;
        }

        /**
         * Gets the number of arguments if the function is non-variadic. If the function is variadic, the minimum number
         * of arguments is returned.
         *
         * @return the number of arguments
         */
        public int getNrArgs() {
            return nrArgs;
        }

        public abstract YdbConstant apply(YdbConstant[] evaluatedArgs, YdbExpression... args);

        @Override
        public String toString() {
            return functionName;
        }

        public boolean isVariadic() {
            return variadic;
        }

        public String getName() {
            return functionName;
        }

        public abstract boolean supportsReturnType(YdbType type);

        public abstract YdbType[] getInputTypesForReturnType(YdbType returnType, int nrArguments);

        public boolean checkArguments(YdbExpression... constants) {
            return true;
        }

    }

    @Override
    public YdbType getExpressionType() {
        return returnType;
    }

}