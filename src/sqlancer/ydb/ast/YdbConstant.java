package sqlancer.ydb.ast;

import com.google.common.primitives.UnsignedInteger;
import sqlancer.IgnoreMeException;
import sqlancer.ydb.YdbType;

public abstract class YdbConstant implements YdbExpression {

    public abstract String getTextRepresentation();

    public abstract String getUnquotedTextRepresentation();

    // NULL
    public static class YdbNullConstant extends YdbConstant {

        @Override
        public String getTextRepresentation() {
            return "NULL";
        }

        @Override
        public YdbType getExpressionType() {
            return null;
        }

        @Override
        public boolean isNull() {
            return true;
        }

        @Override
        public YdbConstant isEquals(YdbConstant rightVal) {
            return YdbConstant.createNullConstant();
        }

        @Override
        protected YdbConstant isLessThan(YdbConstant rightVal) {
            return YdbConstant.createNullConstant();
        }

        @Override
        public YdbConstant cast(YdbType type) {
            return YdbConstant.createNullConstant();
        }

        @Override
        public String getUnquotedTextRepresentation() {
            return getTextRepresentation();
        }

    }

    // BOOL
    public static class BooleanConstant extends YdbConstant {

        private final boolean value;

        public BooleanConstant(boolean value) {
            this.value = value;
        }

        @Override
        public String getTextRepresentation() {
            return value ? "true" : "false";
        }

        @Override
        public YdbType getExpressionType() {
            return YdbType.bool();
        }

        @Override
        public boolean asBoolean() {
            return value;
        }

        @Override
        public boolean isBoolean() {
            return true;
        }

        @Override
        public YdbConstant isEquals(YdbConstant rightVal) {
            if (rightVal.isNull()) {
                return YdbConstant.createNullConstant();
            } else if (rightVal.isBoolean()) {
                return YdbConstant.createBooleanConstant(value == rightVal.asBoolean());
            } else if (rightVal.isString()) {
                return YdbConstant.createBooleanConstant(value == rightVal.cast(YdbType.bool()).asBoolean());
            } else {
                throw new AssertionError(rightVal);
            }
        }

        @Override
        protected YdbConstant isLessThan(YdbConstant rightVal) {
            if (rightVal.isNull()) {
                return YdbConstant.createNullConstant();
            } else if (rightVal.isString()) {
                return isLessThan(rightVal.cast(YdbType.bool()));
            } else {
                assert rightVal.isBoolean();
                return YdbConstant.createBooleanConstant((value ? 1 : 0) < (rightVal.asBoolean() ? 1 : 0));
            }
        }

        @Override
        public YdbConstant cast(YdbType type) {
            switch (type.typeClass) {
                case BOOL:
                    return this;
                case FLOAT:
                    return YdbConstant.createFloatConstant(value ? 1 : 0);
                case DOUBLE:
                    return YdbConstant.createDoubleConstant(value ? 1 : 0);
                case INT8:
                    return YdbConstant.createInt8Constant(value ? 1 : 0);
                case INT16:
                    return YdbConstant.createInt16Constant(value ? 1 : 0);
                case INT32:
                    return YdbConstant.createInt32Constant(value ? 1 : 0);
                case INT64:
                    return YdbConstant.createInt64Constant(value ? 1 : 0);
                case UINT8:
                    return YdbConstant.createUInt8Constant(value ? 1 : 0);
                case UINT16:
                    return YdbConstant.createUInt16Constant(value ? 1 : 0);
                case UINT32:
                    return YdbConstant.createUInt32Constant(value ? 1 : 0);
                case UINT64:
                    return YdbConstant.createUInt64Constant(value ? 1 : 0);
                case STRING:
                    return YdbConstant.createStringConstant(String.valueOf(value ? 1 : 0));
                case UNSUPPORTED:
                    return null;
            }
            return null;
        }

        @Override
        public String getUnquotedTextRepresentation() {
            return getTextRepresentation();
        }

    }

    //INT8
    public static class Int8Constant extends YdbConstant {

        private long val;

        public Int8Constant(long val) {
            this.val = val;
            if (this.val > 127) {
                this.val = 127;
            }
            if (this.val < -128) {
                this.val = -128;
            }
        }

        @Override
        public String getTextRepresentation() {
            return "Int8('" + val + "')";
        }

        @Override
        public YdbType getExpressionType() {
            return YdbType.int8();
        }

        @Override
        public long asInt() {
            return val;
        }

        @Override
        public boolean isInt() {
            return true;
        }

        @Override
        public YdbConstant isEquals(YdbConstant rightVal) {
            if (rightVal.isNull()) {
                return YdbConstant.createNullConstant();
            } else if (rightVal.isBoolean()) {
                return cast(YdbType.bool()).isEquals(rightVal);
            } else if (rightVal.isInt()) {
                return YdbConstant.createBooleanConstant(val == rightVal.asInt());
            } else if (rightVal.isString()) {
                return YdbConstant.createBooleanConstant(val == rightVal.cast(YdbType.int8()).asInt());
            } else if (rightVal.isDouble()) {
                return YdbConstant.createBooleanConstant(val == rightVal.asDouble());
            } else {
                throw new AssertionError(rightVal);
            }
        }

        @Override
        protected YdbConstant isLessThan(YdbConstant rightVal) {
            if (rightVal.isNull()) {
                return YdbConstant.createNullConstant();
            } else if (rightVal.isInt()) {
                return YdbConstant.createBooleanConstant(val < rightVal.asInt());
            } else if (rightVal.isBoolean()) {
                throw new AssertionError(rightVal);
            } else if (rightVal.isString()) {
                return YdbConstant.createBooleanConstant(val < rightVal.cast(YdbType.int8()).asInt());
            } else if (rightVal.isDouble()) {
                return YdbConstant.createBooleanConstant(val < rightVal.asDouble());
            } else {
                throw new IgnoreMeException();
            }

        }

        @Override
        public YdbConstant cast(YdbType type) {
            switch (type.typeClass) {
                case BOOL:
                    return YdbConstant.createBooleanConstant(val != 0);
                case FLOAT:
                    return YdbConstant.createFloatConstant(val);
                case DOUBLE:
                    return YdbConstant.createDoubleConstant(val);
                case INT8:
                    return this;
                case INT16:
                    return YdbConstant.createInt16Constant(val);
                case INT32:
                    return YdbConstant.createInt32Constant(val);
                case INT64:
                    return YdbConstant.createInt64Constant(val);
                case UINT8:
                    return YdbConstant.createUInt8Constant(val);
                case UINT16:
                    return YdbConstant.createUInt16Constant(val);
                case UINT32:
                    return YdbConstant.createUInt32Constant(val);
                case UINT64:
                    return YdbConstant.createUInt64Constant(val);
                case STRING:
                    return YdbConstant.createStringConstant(String.valueOf(val));
                case UNSUPPORTED:
                    return null;
            }
            return null;
        }

        @Override
        public String getUnquotedTextRepresentation() {
            return getTextRepresentation();
        }

    }

    // INT16
    public static class Int16Constant extends YdbConstant {

        private long val;

        public Int16Constant(long val) {
            this.val = val;
            if (this.val > 32767) {
                this.val = 32767;
            }
            if (this.val < -32768) {
                this.val = -32768;
            }
        }

        @Override
        public String getTextRepresentation() {
            return "Int16('" + val + "')";
        }

        @Override
        public YdbType getExpressionType() {
            return YdbType.int16();
        }

        @Override
        public long asInt() {
            return val;
        }

        @Override
        public boolean isInt() {
            return true;
        }

        @Override
        public YdbConstant isEquals(YdbConstant rightVal) {
            if (rightVal.isNull()) {
                return YdbConstant.createNullConstant();
            } else if (rightVal.isBoolean()) {
                return cast(YdbType.bool()).isEquals(rightVal);
            } else if (rightVal.isInt()) {
                return YdbConstant.createBooleanConstant(val == rightVal.asInt());
            } else if (rightVal.isString()) {
                return YdbConstant.createBooleanConstant(val == rightVal.cast(YdbType.int16()).asInt());
            } else if (rightVal.isDouble()) {
                return YdbConstant.createBooleanConstant(val == rightVal.asDouble());
            } else {
                throw new AssertionError(rightVal);
            }
        }

        @Override
        protected YdbConstant isLessThan(YdbConstant rightVal) {
            if (rightVal.isNull()) {
                return YdbConstant.createNullConstant();
            } else if (rightVal.isInt()) {
                return YdbConstant.createBooleanConstant(val < rightVal.asInt());
            } else if (rightVal.isBoolean()) {
                throw new AssertionError(rightVal);
            } else if (rightVal.isString()) {
                return YdbConstant.createBooleanConstant(val < rightVal.cast(YdbType.int16()).asInt());
            } else if (rightVal.isDouble()) {
                return YdbConstant.createBooleanConstant(val < rightVal.asDouble());
            } else {
                throw new IgnoreMeException();
            }
        }

        @Override
        public YdbConstant cast(YdbType type) {
            switch (type.typeClass) {
                case BOOL:
                    return YdbConstant.createBooleanConstant(val != 0);
                case FLOAT:
                    return YdbConstant.createFloatConstant(val);
                case DOUBLE:
                    return YdbConstant.createDoubleConstant(val);
                case INT8:
                    return YdbConstant.createInt8Constant(val);
                case INT16:
                    return this;
                case INT32:
                    return YdbConstant.createInt32Constant(val);
                case INT64:
                    return YdbConstant.createInt64Constant(val);
                case UINT8:
                    return YdbConstant.createUInt8Constant(val);
                case UINT16:
                    return YdbConstant.createUInt16Constant(val);
                case UINT32:
                    return YdbConstant.createUInt32Constant(val);
                case UINT64:
                    return YdbConstant.createUInt64Constant(val);
                case STRING:
                    return YdbConstant.createStringConstant(String.valueOf(val));
                case UNSUPPORTED:
                    return null;
            }
            return null;
        }

        @Override
        public String getUnquotedTextRepresentation() {
            return getTextRepresentation();
        }

    }

    // INT32
    public static class Int32Constant extends YdbConstant {

        private long val;

        public Int32Constant(long val) {
            this.val = val;
            if (this.val > 2147483647) {
                this.val = 2147483647;
            }
            if (this.val < -2147483648) {
                this.val = -2147483648;
            }
        }

        @Override
        public String getTextRepresentation() {
            return String.valueOf(val);
        }

        @Override
        public YdbType getExpressionType() {
            return YdbType.int32();
        }

        @Override
        public long asInt() {
            return val;
        }

        @Override
        public boolean isInt() {
            return true;
        }

        @Override
        public YdbConstant isEquals(YdbConstant rightVal) {
            if (rightVal.isNull()) {
                return YdbConstant.createNullConstant();
            } else if (rightVal.isBoolean()) {
                return cast(YdbType.bool()).isEquals(rightVal);
            } else if (rightVal.isInt()) {
                return YdbConstant.createBooleanConstant(val == rightVal.asInt());
            } else if (rightVal.isString()) {
                return YdbConstant.createBooleanConstant(val == rightVal.cast(YdbType.int32()).asInt());
            } else if (rightVal.isDouble()) {
                return YdbConstant.createBooleanConstant(val == rightVal.asDouble());
            } else {
                throw new AssertionError(rightVal);
            }
        }

        @Override
        protected YdbConstant isLessThan(YdbConstant rightVal) {
            if (rightVal.isNull()) {
                return YdbConstant.createNullConstant();
            } else if (rightVal.isInt()) {
                return YdbConstant.createBooleanConstant(val < rightVal.asInt());
            } else if (rightVal.isBoolean()) {
                throw new AssertionError(rightVal);
            } else if (rightVal.isString()) {
                return YdbConstant.createBooleanConstant(val < rightVal.cast(YdbType.int32()).asInt());
            } else if (rightVal.isDouble()) {
                return YdbConstant.createBooleanConstant(val < rightVal.asDouble());
            } else {
                throw new IgnoreMeException();
            }

        }

        @Override
        public YdbConstant cast(YdbType type) {
            switch (type.typeClass) {
                case BOOL:
                    return YdbConstant.createBooleanConstant(val != 0);
                case FLOAT:
                    return YdbConstant.createFloatConstant(val);
                case DOUBLE:
                    return YdbConstant.createDoubleConstant(val);
                case INT8:
                    return YdbConstant.createInt8Constant(val);
                case INT16:
                    return YdbConstant.createInt16Constant(val);
                case INT32:
                    return this;
                case INT64:
                    return YdbConstant.createInt64Constant(val);
                case UINT8:
                    return YdbConstant.createUInt8Constant(val);
                case UINT16:
                    return YdbConstant.createUInt16Constant(val);
                case UINT32:
                    return YdbConstant.createUInt32Constant(val);
                case UINT64:
                    return YdbConstant.createUInt64Constant(val);
                case STRING:
                    return YdbConstant.createStringConstant(String.valueOf(val));
                case UNSUPPORTED:
                    return null;
            }
            return null;
        }

        @Override
        public String getUnquotedTextRepresentation() {
            return getTextRepresentation();
        }

    }

    // INT64
    public static class Int64Constant extends YdbConstant {

        private final long val;

        public Int64Constant(long val) {
            this.val = val;
        }

        @Override
        public String getTextRepresentation() {
            return String.valueOf(val);
        }

        @Override
        public YdbType getExpressionType() {
            return YdbType.int64();
        }

        @Override
        public long asInt() {
            return val;
        }

        @Override
        public boolean isInt() {
            return true;
        }

        @Override
        public YdbConstant isEquals(YdbConstant rightVal) {
            if (rightVal.isNull()) {
                return YdbConstant.createNullConstant();
            } else if (rightVal.isBoolean()) {
                return cast(YdbType.bool()).isEquals(rightVal);
            } else if (rightVal.isInt()) {
                return YdbConstant.createBooleanConstant(val == rightVal.asInt());
            } else if (rightVal.isString()) {
                return YdbConstant.createBooleanConstant(val == rightVal.cast(YdbType.int64()).asInt());
            } else if (rightVal.isDouble()) {
                return YdbConstant.createBooleanConstant(val == rightVal.asDouble());
            } else {
                throw new AssertionError(rightVal);
            }
        }

        @Override
        protected YdbConstant isLessThan(YdbConstant rightVal) {
            if (rightVal.isNull()) {
                return YdbConstant.createNullConstant();
            } else if (rightVal.isInt()) {
                return YdbConstant.createBooleanConstant(val < rightVal.asInt());
            } else if (rightVal.isBoolean()) {
                throw new AssertionError(rightVal);
            } else if (rightVal.isString()) {
                return YdbConstant.createBooleanConstant(val < rightVal.cast(YdbType.int64()).asInt());
            } else if (rightVal.isDouble()) {
                return YdbConstant.createBooleanConstant(val < rightVal.asDouble());
            } else {
                throw new IgnoreMeException();
            }

        }

        @Override
        public YdbConstant cast(YdbType type) {
            switch (type.typeClass) {
                case BOOL:
                    return YdbConstant.createBooleanConstant(val != 0);
                case FLOAT:
                    return YdbConstant.createFloatConstant(val);
                case DOUBLE:
                    return YdbConstant.createDoubleConstant(val);
                case INT8:
                    return YdbConstant.createInt8Constant(val);
                case INT16:
                    return YdbConstant.createInt16Constant(val);
                case INT32:
                    return YdbConstant.createInt32Constant(val);
                case INT64:
                    return this;
                case UINT8:
                    return YdbConstant.createUInt8Constant(val);
                case UINT16:
                    return YdbConstant.createUInt16Constant(val);
                case UINT32:
                    return YdbConstant.createUInt32Constant(val);
                case UINT64:
                    return YdbConstant.createUInt64Constant(val);
                case STRING:
                    return YdbConstant.createStringConstant(String.valueOf(val));
                case UNSUPPORTED:
                    return null;
            }
            return null;
        }

        @Override
        public String getUnquotedTextRepresentation() {
            return getTextRepresentation();
        }

    }

    // UINT8
    public static class UInt8Constant extends YdbConstant {

        private long val;

        public UInt8Constant(long val) {
            this.val = val;
            if (this.val > 255) {
                this.val = 255;
            }
        }

        @Override
        public String getTextRepresentation() {
            return "Uint8('" + val + "')";
        }

        @Override
        public YdbType getExpressionType() {
            return YdbType.uint8();
        }

        @Override
        public long asInt() {
            return val;
        }

        @Override
        public boolean isInt() {
            return true;
        }

        @Override
        public YdbConstant isEquals(YdbConstant rightVal) {
            if (rightVal.isNull()) {
                return YdbConstant.createNullConstant();
            } else if (rightVal.isBoolean()) {
                return cast(YdbType.bool()).isEquals(rightVal);
            } else if (rightVal.isInt()) {
                return YdbConstant.createBooleanConstant(val == rightVal.asInt());
            } else if (rightVal.isString()) {
                return YdbConstant.createBooleanConstant(val == rightVal.cast(YdbType.uint8()).asInt());
            } else if (rightVal.isDouble()) {
                return YdbConstant.createBooleanConstant(val == rightVal.asDouble());
            } else {
                throw new AssertionError(rightVal);
            }
        }

        @Override
        protected YdbConstant isLessThan(YdbConstant rightVal) {
            if (rightVal.isNull()) {
                return YdbConstant.createNullConstant();
            } else if (rightVal.isInt()) {
                return YdbConstant.createBooleanConstant(val < rightVal.asInt());
            } else if (rightVal.isBoolean()) {
                throw new AssertionError(rightVal);
            } else if (rightVal.isString()) {
                return YdbConstant.createBooleanConstant(val < rightVal.cast(YdbType.uint8()).asInt());
            } else if (rightVal.isDouble()) {
                return YdbConstant.createBooleanConstant(val < rightVal.asDouble());
            } else {
                throw new IgnoreMeException();
            }

        }

        @Override
        public YdbConstant cast(YdbType type) {
            switch (type.typeClass) {
                case BOOL:
                    return YdbConstant.createBooleanConstant(val != 0);
                case FLOAT:
                    return YdbConstant.createFloatConstant(val);
                case DOUBLE:
                    return YdbConstant.createDoubleConstant(val);
                case INT8:
                    return YdbConstant.createInt8Constant(val);
                case INT16:
                    return YdbConstant.createInt16Constant(val);
                case INT32:
                    return YdbConstant.createInt32Constant(val);
                case INT64:
                    return YdbConstant.createInt64Constant(val);
                case UINT8:
                    return this;
                case UINT16:
                    return YdbConstant.createUInt16Constant(val);
                case UINT32:
                    return YdbConstant.createUInt32Constant(val);
                case UINT64:
                    return YdbConstant.createUInt64Constant(val);
                case STRING:
                    return YdbConstant.createStringConstant(String.valueOf(val));
                case UNSUPPORTED:
                    return null;
            }
            return null;
        }

        @Override
        public String getUnquotedTextRepresentation() {
            return getTextRepresentation();
        }

    }

    // UINT16
    public static class UInt16Constant extends YdbConstant {

        private long val;

        public UInt16Constant(long val) {
            this.val = val;
            if (this.val > 65535) {
                this.val = 65535;
            }
        }

        @Override
        public String getTextRepresentation() {
            return "Uint16('" + val + "')";
        }

        @Override
        public YdbType getExpressionType() {
            return YdbType.uint16();
        }

        @Override
        public long asInt() {
            return val;
        }

        @Override
        public boolean isInt() {
            return true;
        }

        @Override
        public YdbConstant isEquals(YdbConstant rightVal) {
            if (rightVal.isNull()) {
                return YdbConstant.createNullConstant();
            } else if (rightVal.isBoolean()) {
                return cast(YdbType.bool()).isEquals(rightVal);
            } else if (rightVal.isInt()) {
                return YdbConstant.createBooleanConstant(val == rightVal.asInt());
            } else if (rightVal.isString()) {
                return YdbConstant.createBooleanConstant(val == rightVal.cast(YdbType.uint16()).asInt());
            } else if (rightVal.isDouble()) {
                return YdbConstant.createBooleanConstant(val == rightVal.asDouble());
            } else {
                throw new AssertionError(rightVal);
            }
        }

        @Override
        protected YdbConstant isLessThan(YdbConstant rightVal) {
            if (rightVal.isNull()) {
                return YdbConstant.createNullConstant();
            } else if (rightVal.isInt()) {
                return YdbConstant.createBooleanConstant(val < rightVal.asInt());
            } else if (rightVal.isBoolean()) {
                throw new AssertionError(rightVal);
            } else if (rightVal.isString()) {
                return YdbConstant.createBooleanConstant(val < rightVal.cast(YdbType.uint16()).asInt());
            } else if (rightVal.isDouble()) {
                return YdbConstant.createBooleanConstant(val < rightVal.asDouble());
            } else {
                throw new IgnoreMeException();
            }

        }

        @Override
        public YdbConstant cast(YdbType type) {
            switch (type.typeClass) {
                case BOOL:
                    return YdbConstant.createBooleanConstant(val != 0);
                case FLOAT:
                    return YdbConstant.createFloatConstant(val);
                case DOUBLE:
                    return YdbConstant.createDoubleConstant(val);
                case INT8:
                    return YdbConstant.createInt8Constant(val);
                case INT16:
                    return YdbConstant.createInt16Constant(val);
                case INT32:
                    return YdbConstant.createInt32Constant(val);
                case INT64:
                    return YdbConstant.createInt64Constant(val);
                case UINT8:
                    return YdbConstant.createUInt8Constant(val);
                case UINT16:
                    return this;
                case UINT32:
                    return YdbConstant.createUInt32Constant(val);
                case UINT64:
                    return YdbConstant.createUInt64Constant(val);
                case STRING:
                    return YdbConstant.createStringConstant(String.valueOf(val));
                case UNSUPPORTED:
                    return null;
            }
            return null;
        }

        @Override
        public String getUnquotedTextRepresentation() {
            return getTextRepresentation();
        }

    }

    // UINT32
    public static class UInt32Constant extends YdbConstant {

        private long val;

        public UInt32Constant(long val) {
            this.val = val;
            if (this.val > 4294967295L) {
                this.val = 4294967295L;
            }
        }

        @Override
        public String getTextRepresentation() {
            return val + "u";
        }

        @Override
        public YdbType getExpressionType() {
            return YdbType.uint32();
        }

        @Override
        public long asInt() {
            return val;
        }

        @Override
        public boolean isInt() {
            return true;
        }

        @Override
        public YdbConstant isEquals(YdbConstant rightVal) {
            if (rightVal.isNull()) {
                return YdbConstant.createNullConstant();
            } else if (rightVal.isBoolean()) {
                return cast(YdbType.bool()).isEquals(rightVal);
            } else if (rightVal.isInt()) {
                return YdbConstant.createBooleanConstant(val == rightVal.asInt());
            } else if (rightVal.isString()) {
                return YdbConstant.createBooleanConstant(val == rightVal.cast(YdbType.uint32()).asInt());
            } else if (rightVal.isDouble()) {
                return YdbConstant.createBooleanConstant(val == rightVal.asDouble());
            } else {
                throw new AssertionError(rightVal);
            }
        }

        @Override
        protected YdbConstant isLessThan(YdbConstant rightVal) {
            if (rightVal.isNull()) {
                return YdbConstant.createNullConstant();
            } else if (rightVal.isInt()) {
                return YdbConstant.createBooleanConstant(val < rightVal.asInt());
            } else if (rightVal.isBoolean()) {
                throw new AssertionError(rightVal);
            } else if (rightVal.isString()) {
                return YdbConstant.createBooleanConstant(val < rightVal.cast(YdbType.uint32()).asInt());
            } else if (rightVal.isDouble()) {
                return YdbConstant.createBooleanConstant(val < rightVal.asDouble());
            } else {
                throw new IgnoreMeException();
            }

        }

        @Override
        public YdbConstant cast(YdbType type) {
            switch (type.typeClass) {
                case BOOL:
                    return YdbConstant.createBooleanConstant(val != 0);
                case FLOAT:
                    return YdbConstant.createFloatConstant(val);
                case DOUBLE:
                    return YdbConstant.createDoubleConstant(val);
                case INT8:
                    return YdbConstant.createInt8Constant(val);
                case INT16:
                    return YdbConstant.createInt16Constant(val);
                case INT32:
                    return YdbConstant.createInt32Constant(val);
                case INT64:
                    return YdbConstant.createInt64Constant(val);
                case UINT8:
                    return YdbConstant.createUInt8Constant(val);
                case UINT16:
                    return YdbConstant.createUInt16Constant(val);
                case UINT32:
                    return this;
                case UINT64:
                    return YdbConstant.createUInt64Constant(val);
                case STRING:
                    return YdbConstant.createStringConstant(String.valueOf(val));
                case UNSUPPORTED:
                    return null;
            }
            return null;
        }

        @Override
        public String getUnquotedTextRepresentation() {
            return getTextRepresentation();
        }

    }

    // UINT64
    public static class UInt64Constant extends YdbConstant {

        private final long val;

        public UInt64Constant(long val) {
            this.val = val;
        }

        @Override
        public String getTextRepresentation() {
            return "Uint64('" + val + "')";
        }

        @Override
        public YdbType getExpressionType() {
            return YdbType.uint64();
        }

        @Override
        public long asInt() {
            return val;
        }

        @Override
        public boolean isInt() {
            return true;
        }

        @Override
        public YdbConstant isEquals(YdbConstant rightVal) {
            if (rightVal.isNull()) {
                return YdbConstant.createNullConstant();
            } else if (rightVal.isBoolean()) {
                return cast(YdbType.bool()).isEquals(rightVal);
            } else if (rightVal.isInt()) {
                return YdbConstant.createBooleanConstant(val == rightVal.asInt());
            } else if (rightVal.isString()) {
                return YdbConstant.createBooleanConstant(val == rightVal.cast(YdbType.uint64()).asInt());
            } else if (rightVal.isDouble()) {
                return YdbConstant.createBooleanConstant(val == rightVal.asDouble());
            } else {
                throw new AssertionError(rightVal);
            }
        }

        @Override
        protected YdbConstant isLessThan(YdbConstant rightVal) {
            if (rightVal.isNull()) {
                return YdbConstant.createNullConstant();
            } else if (rightVal.isInt()) {
                return YdbConstant.createBooleanConstant(val < rightVal.asInt());
            } else if (rightVal.isBoolean()) {
                throw new AssertionError(rightVal);
            } else if (rightVal.isString()) {
                return YdbConstant.createBooleanConstant(val < rightVal.cast(YdbType.uint64()).asInt());
            } else if (rightVal.isDouble()) {
                return YdbConstant.createBooleanConstant(val < rightVal.asDouble());
            } else {
                throw new IgnoreMeException();
            }

        }

        @Override
        public YdbConstant cast(YdbType type) {
            switch (type.typeClass) {
                case BOOL:
                    return YdbConstant.createBooleanConstant(val != 0);
                case FLOAT:
                    return YdbConstant.createFloatConstant(val);
                case DOUBLE:
                    return YdbConstant.createDoubleConstant(val);
                case INT8:
                    return YdbConstant.createInt8Constant(val);
                case INT16:
                    return YdbConstant.createInt16Constant(val);
                case INT32:
                    return YdbConstant.createInt32Constant(val);
                case INT64:
                    return YdbConstant.createInt64Constant(val);
                case UINT8:
                    return YdbConstant.createUInt8Constant(val);
                case UINT16:
                    return YdbConstant.createUInt16Constant(val);
                case UINT32:
                    return YdbConstant.createUInt32Constant(val);
                case UINT64:
                    return this;
                case STRING:
                    return YdbConstant.createStringConstant(String.valueOf(val));
                case UNSUPPORTED:
                    return null;
            }
            return null;
        }

        @Override
        public String getUnquotedTextRepresentation() {
            return getTextRepresentation();
        }

    }

    // STRING
    public static class StringConstant extends YdbConstant {

        private final String val;

        public StringConstant(String value) {
            this.val = value;
        }

        @Override
        public String getTextRepresentation() {
            return String.format("\"%s\"", val);
        }

        @Override
        public YdbConstant isEquals(YdbConstant rightVal) {
            if (rightVal.isNull()) {
                return YdbConstant.createNullConstant();
            } else if (rightVal.isInt()) {
                return cast(YdbType.int64()).isEquals(rightVal.cast(YdbType.int64()));
            } else if (rightVal.isBoolean()) {
                return cast(YdbType.bool()).isEquals(rightVal.cast(YdbType.bool()));
            } else if (rightVal.isString()) {
                return YdbConstant.createBooleanConstant(val.contentEquals(rightVal.asString()));
            } else {
                throw new AssertionError(rightVal);
            }
        }

        @Override
        protected YdbConstant isLessThan(YdbConstant rightVal) {
            if (rightVal.isNull()) {
                return YdbConstant.createNullConstant();
            } else if (rightVal.isInt()) {
                throw new AssertionError(rightVal);
            } else if (rightVal.isBoolean()) {
                throw new AssertionError(rightVal);
            } else if (rightVal.isString()) {
                return YdbConstant.createBooleanConstant(val.compareTo(rightVal.asString()) < 0);
            } else {
                throw new AssertionError(rightVal);
            }
        }

        @Override
        public YdbConstant cast(YdbType type) {
            String s = val.trim();
            switch (type.typeClass) {
                case BOOL:
                    switch (s.toUpperCase()) {
                        case "TRUE":
                        case "true":
                            return YdbConstant.createTrue();
                        case "FALSE":
                        case "false":
                            return YdbConstant.createFalse();
                        default:
                            throw new AssertionError(this);
                    }
                case FLOAT:
                    return YdbConstant.createFloatConstant(Float.parseFloat(s));
                case DOUBLE:
                    return YdbConstant.createDoubleConstant(Double.parseDouble(s));
                case INT8:
                    return YdbConstant.createInt8Constant(Long.parseLong(s));
                case INT16:
                    return YdbConstant.createInt16Constant(Long.parseLong(s));
                case INT32:
                    return YdbConstant.createInt32Constant(Long.parseLong(s));
                case INT64:
                    return YdbConstant.createInt64Constant(Long.parseLong(s));
                case UINT8:
                    return YdbConstant.createUInt8Constant(Long.parseLong(s));
                case UINT16:
                    return YdbConstant.createUInt16Constant(Long.parseLong(s));
                case UINT32:
                    return YdbConstant.createUInt32Constant(Long.parseLong(s));
                case UINT64:
                    return YdbConstant.createUInt64Constant(Long.parseLong(s));
                case STRING:
                    return this;
            }
            return null;
        }

        @Override
        public YdbType getExpressionType() {
            return YdbType.string();
        }

        @Override
        public boolean isString() {
            return true;
        }

        @Override
        public String asString() {
            return val;
        }

        @Override
        public String getUnquotedTextRepresentation() {
            return val;
        }

    }

    // FLOAT
    public static class FloatConstant extends YdbConstant {

        private final double val;

        public FloatConstant(double value) { this.val = value; }

        @Override
        public String getTextRepresentation() {
            return val + "f";
        }

        @Override
        public YdbConstant isEquals(YdbConstant rightVal) {
            if (rightVal.isNull()) {
                return YdbConstant.createNullConstant();
            } else if (rightVal.isInt()) {
                return cast(YdbType.int64()).isEquals(rightVal.cast(YdbType.int64()));
            } else if (rightVal.isBoolean()) {
                return cast(YdbType.bool()).isEquals(rightVal.cast(YdbType.bool()));
            } else if (rightVal.isString()) {
                return YdbConstant.createBooleanConstant(val == rightVal.cast(YdbType.float32()).asDouble());
            } else if (rightVal.isDouble()) {
                return YdbConstant.createBooleanConstant(val == rightVal.asDouble());
            } else {
                throw new AssertionError(rightVal);
            }
        }

        @Override
        protected YdbConstant isLessThan(YdbConstant rightVal) {
            if (rightVal.isNull()) {
                return YdbConstant.createNullConstant();
            } else if (rightVal.isInt()) {
                return YdbConstant.createBooleanConstant(val < rightVal.asInt());
            } else if (rightVal.isBoolean()) {
                return YdbConstant.createBooleanConstant(val < rightVal.cast(YdbType.float32()).asDouble());
            } else if (rightVal.isString()) {
                return YdbConstant.createBooleanConstant(val < rightVal.cast(YdbType.float32()).asDouble());
            } else if (rightVal.isDouble()) {
                return YdbConstant.createBooleanConstant(val < rightVal.asDouble());
            } else {
                throw new IgnoreMeException();
            }

        }

        @Override
        public YdbConstant cast(YdbType type) {
            switch (type.typeClass) {
                case BOOL:
                    return YdbConstant.createBooleanConstant(val != 0);
                case FLOAT:
                    return this;
                case DOUBLE:
                    return YdbConstant.createDoubleConstant(val);
                case INT8:
                    return YdbConstant.createInt8Constant((long) val);
                case INT16:
                    return YdbConstant.createInt16Constant((long) val);
                case INT32:
                    return YdbConstant.createInt32Constant((long) val);
                case INT64:
                    return YdbConstant.createInt64Constant((long) val);
                case UINT8:
                    return YdbConstant.createUInt8Constant((long) val);
                case UINT16:
                    return YdbConstant.createUInt16Constant((long) val);
                case UINT32:
                    return YdbConstant.createUInt32Constant((long) val);
                case UINT64:
                    return YdbConstant.createUInt64Constant((long) val);
                case STRING:
                    return YdbConstant.createStringConstant(String.valueOf(val));
                case UNSUPPORTED:
                    return null;
            }
            return null;
        }


        @Override
        public YdbType getExpressionType() {
            return YdbType.float32();
        }

        @Override
        public boolean isString() {
            return false;
        }

        @Override
        public String asString() {
            return String.valueOf(val) + "f";
        }

        @Override
        public String getUnquotedTextRepresentation() {
            return asString();
        }

        @Override
        public boolean isDouble() {
            return true;
        }

        @Override
        public double asDouble() {
            return val;
        }

    }

    // DOUBLE
    public static class DoubleConstant extends YdbConstant {

        private final double val;

        public DoubleConstant(double value) { this.val = value; }

        @Override
        public String getTextRepresentation() {
            return String.valueOf(val);
        }

        @Override
        public YdbConstant isEquals(YdbConstant rightVal) {
            if (rightVal.isNull()) {
                return YdbConstant.createNullConstant();
            } else if (rightVal.isInt()) {
                return cast(YdbType.int64()).isEquals(rightVal.cast(YdbType.int64()));
            } else if (rightVal.isBoolean()) {
                return YdbConstant.createBooleanConstant(val == rightVal.cast(YdbType.float64()).asDouble());
            } else if (rightVal.isString()) {
                return YdbConstant.createBooleanConstant(val == rightVal.cast(YdbType.float64()).asDouble());
            } else if (rightVal.isDouble()) {
                return YdbConstant.createBooleanConstant(val == rightVal.asDouble());
            } else {
                throw new AssertionError(rightVal);
            }
        }

        @Override
        protected YdbConstant isLessThan(YdbConstant rightVal) {
            if (rightVal.isNull()) {
                return YdbConstant.createNullConstant();
            } else if (rightVal.isInt()) {
                return YdbConstant.createBooleanConstant(val < rightVal.asInt());
            } else if (rightVal.isBoolean()) {
                return YdbConstant.createBooleanConstant(val < rightVal.cast(YdbType.float64()).asDouble());
            } else if (rightVal.isString()) {
                return YdbConstant.createBooleanConstant(val < rightVal.cast(YdbType.float64()).asDouble());
            } else if (rightVal.isDouble()) {
                return YdbConstant.createBooleanConstant(val < rightVal.asDouble());
            } else {
                throw new IgnoreMeException();
            }

        }

        @Override
        public YdbConstant cast(YdbType type) {
            switch (type.typeClass) {
                case BOOL:
                    return YdbConstant.createBooleanConstant(val != 0);
                case FLOAT:
                    return YdbConstant.createFloatConstant((float) val);
                case DOUBLE:
                    return this;
                case INT8:
                    return YdbConstant.createInt8Constant((long) val);
                case INT16:
                    return YdbConstant.createInt16Constant((long) val);
                case INT32:
                    return YdbConstant.createInt32Constant((long) val);
                case INT64:
                    return YdbConstant.createInt64Constant((long) val);
                case UINT8:
                    return YdbConstant.createUInt8Constant((long) val);
                case UINT16:
                    return YdbConstant.createUInt16Constant((long) val);
                case UINT32:
                    return YdbConstant.createUInt32Constant((long) val);
                case UINT64:
                    return YdbConstant.createUInt64Constant((long) val);
                case STRING:
                    return YdbConstant.createStringConstant(String.valueOf(val));
                case UNSUPPORTED:
                    return null;
            }
            return null;
        }


        @Override
        public YdbType getExpressionType() {
            return YdbType.float64();
        }

        @Override
        public boolean isString() {
            return false;
        }

        @Override
        public String asString() {
            return String.valueOf(val);
        }

        @Override
        public String getUnquotedTextRepresentation() {
            return asString();
        }

        @Override
        public boolean isDouble() {
            return true;
        }

        @Override
        public double asDouble() {
            return val;
        }

    }







    public String asString() { throw new UnsupportedOperationException(this.toString()); }

    public boolean asBoolean() { throw new UnsupportedOperationException(this.toString()); }

    public long asInt() { throw new UnsupportedOperationException(this.toString()); }

    public double asDouble() { throw new UnsupportedOperationException(this.toString()); }




    public boolean isString() { return false; }

    public boolean isBoolean() { return false; }

    public boolean isNull() { return false; }

    public boolean isInt() { return false; }

    public boolean isDouble() { return false; }
















    public abstract YdbConstant isEquals(YdbConstant rightVal);

    protected abstract YdbConstant isLessThan(YdbConstant rightVal);

    @Override
    public String toString() {
        return getTextRepresentation();
    }

    public abstract YdbConstant cast(YdbType type);













    public static YdbConstant createNullConstant() {
        return new YdbNullConstant();
    }

    public static YdbConstant createInt8Constant(long val) {
        return new Int8Constant(val);
    }
    public static YdbConstant createInt16Constant(long val) {
        return new Int16Constant(val);
    }
    public static YdbConstant createInt32Constant(long val) {
        return new Int32Constant(val);
    }
    public static YdbConstant createInt64Constant(long val) {
        return new Int64Constant(val);
    }

    public static YdbConstant createUInt8Constant(long val) {
        return new UInt8Constant(val);
    }
    public static YdbConstant createUInt16Constant(long val) {
        return new UInt16Constant(val);
    }
    public static YdbConstant createUInt32Constant(long val) {
        return new UInt32Constant(val);
    }
    public static YdbConstant createUInt64Constant(long val) {
        return new UInt64Constant(val);
    }

    public static YdbConstant createIntConstant(long val, YdbType.Class classType) {
        switch (classType) {
            case INT8:
                return createInt8Constant(val);
            case INT16:
                return createInt16Constant(val);
            case INT32:
                return createInt32Constant(val);
            case INT64:
                return createInt64Constant(val);
            case UINT8:
                return createUInt8Constant(val);
            case UINT16:
                return createUInt16Constant(val);
            case UINT32:
                return createUInt32Constant(val);
            case UINT64:
                return createUInt64Constant(val);
            case STRING:
            case UNSUPPORTED:
            case BOOL:
            case FLOAT:
            case DOUBLE:
                throw new AssertionError("Cannot create int constant with type: " + classType.toString());
        }

        return null;
    }

    public static YdbConstant createBooleanConstant(boolean val) {
        return new BooleanConstant(val);
    }

    public static YdbConstant createFalse() {
        return createBooleanConstant(false);
    }

    public static YdbConstant createTrue() {
        return createBooleanConstant(true);
    }

    public static YdbConstant createStringConstant(String string) {
        return new StringConstant(string);
    }

    public static YdbConstant createFloatConstant(float val) {
        return new FloatConstant(val);
    }

    public static YdbConstant createDoubleConstant(double val) {
        return new DoubleConstant(val);
    }

}
