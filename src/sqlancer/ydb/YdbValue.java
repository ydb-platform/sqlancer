package sqlancer.ydb;

import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import com.yandex.ydb.table.values.*;
import sqlancer.Randomly;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class YdbValue {

    static Random rndGen;
    static String alph;

    static {
        rndGen = new Random();
        alph = "abcdefghijklmnopqrstuvwxyz";
    }

    public static Value<?> generateConstant(Type type) {
        switch (type.getKind()) {
            case PRIMITIVE:
                return generatePrimitiveValue((PrimitiveType) type);
            case OPTIONAL: {
                OptionalType ot = (OptionalType) type;
                return OptionalValue.of(Objects.requireNonNull(generateConstant(ot.getItemType())));
            }
            case LIST: {
                return generateRandomList(YdbType.getRandom().getYdbType());
            }
            case DECIMAL:
            case TUPLE:
            case STRUCT:
            case DICT:
            case VARIANT:
            case VOID:
                throw new AssertionError();
        }

        return null;
    }

    public static String generateStringFrom(Value<?> val) {
        Type type = val.getType();
        switch (type.getKind()) {
            case PRIMITIVE:
                PrimitiveValue pv = (PrimitiveValue) val;
                switch (pv.getType().getId()) {
                    case Float32:
                        return pv.toString() + "f";
                    case Bool:
                    case Int8:
                    case Uint8:
                    case Int16:
                    case Uint16:
                    case Int32:
                    case Uint32:
                    case Int64:
                    case Uint64:
                    case Float64:
                    case String:
                    case Utf8:
                    case Yson:
                    case Json:
                    case Uuid:
                    case Date:
                    case Datetime:
                    case Timestamp:
                    case Interval:
                    case TzDate:
                    case TzDatetime:
                    case TzTimestamp:
                    case JsonDocument:
                    case DyNumber:
                        return pv.toString();
                }
                return val.toString();
            case OPTIONAL: {
                OptionalValue ov = (OptionalValue) val;
                return generateStringFrom(ov.get());
            }
            case LIST: {
                ListValue lv = (ListValue) val;
                StringBuilder sb = new StringBuilder();

                sb.append("[");
                for (int i = 0; i < lv.size(); ++i) {
                    sb.append(generateStringFrom(lv.get(i)));
                    if (i + 1 < lv.size()) {
                        sb.append(", ");
                    }
                }
                sb.append("]");

                return sb.toString();
            }
            case DECIMAL:
            case TUPLE:
            case STRUCT:
            case DICT:
            case VARIANT:
            case VOID:
                throw new AssertionError();
        }

        return null;
    }

    public static PrimitiveValue generatePrimitiveValue(PrimitiveType type) {
        switch (type.getId()) {
            case Bool:
                return PrimitiveValue.bool(Randomly.getBoolean());
            case Int8:
                return PrimitiveValue.int8(Randomly.<Byte>fromOptions((byte) 0, (byte) 1, (byte) -128, (byte) 127));
            case Uint8:
                return PrimitiveValue.uint8(Randomly.<Byte>fromOptions((byte) 0, (byte) 1, (byte) 256));
            case Int16:
                return PrimitiveValue.int16(Randomly.<Short>fromOptions((short) 0, (short) 1, (short) -32768, (short) 32767));
            case Uint16:
                return PrimitiveValue.uint16(Randomly.<Short>fromOptions((short) 0, (short) 1, (short) 65535));
            case Int32:
                return PrimitiveValue.int32(Randomly.fromOptions(0, 1, -2147483648, 2147483647));
            case Uint32:
                return PrimitiveValue.uint32(Randomly.fromOptions(0, 1, -1));
            case Int64:
                return PrimitiveValue.int64(Randomly.fromOptions(0L, 1L, -9223372036854775808L, 9223372036854775807L));
            case Uint64:
                return PrimitiveValue.uint64(Randomly.fromOptions(0, 1, -1));
            case Float32:
                return PrimitiveValue.float32(rndGen.nextFloat());
            case Float64:
                return PrimitiveValue.float64(rndGen.nextDouble());
            case String:
                return PrimitiveValue.string(generateRandomString().getBytes());
            case Utf8:
                return PrimitiveValue.utf8(generateRandomString());
            case Date:
                return PrimitiveValue.date(Instant.now());
            case Yson:
            case Json:
            case Uuid:
            case Datetime:
            case Timestamp:
            case Interval:
            case TzDate:
            case TzDatetime:
            case TzTimestamp:
            case JsonDocument:
            case DyNumber:
                throw new AssertionError();
        }

        return null;
    }

    private static String generateRandomString() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < Randomly.smallNumber(); ++i) {
            builder.append(alph.charAt(rndGen.nextInt(0, alph.length() - 1)));
        }

        return builder.toString();
    }

    private static ListValue generateRandomList(Type type) {
        List<Value<?>> values = new ArrayList<>();

        for (int i = 0; i < Randomly.smallNumber(); ++i) {
            values.add(generateConstant(type));
        }

        return ListValue.of(values.toArray(new Value[0]));
    }

}
