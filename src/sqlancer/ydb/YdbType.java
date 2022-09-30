package sqlancer.ydb;

import com.yandex.ydb.table.values.*;
import sqlancer.Randomly;

import java.util.*;
import java.util.stream.Collectors;

public class YdbType {

    public enum Class {
        BOOL,
        FLOAT, DOUBLE,
        INT8, INT16, INT32, INT64,
        UINT8, UINT16, UINT32, UINT64,
        STRING,
        UNSUPPORTED
    };

    private final Type type;

    public static Map<Type.Kind, List<Type>> types;
    public static List<Type.Kind> typesKinds;

    public static Map<Type.Kind, List<Type>> typesSupportedInColumns;
    public static List<Type.Kind> typesSupportedInColumnsKinds;

    public static Map<Type.Kind, List<Type>> typesSupportedAsPrimary;
    public static List<Type.Kind> typesSupportedAsPrimaryKinds;

    public static Map<Class, YdbType> createdConstants;

    public static Map<Class, List<Integer>> intRank;
    public static Map<Class, List<Class>> lowerInt;

    static {
        intRank = new HashMap<>();
        intRank.put(Class.INT8, Arrays.asList(8, 1));
        intRank.put(Class.INT16, Arrays.asList(16, 1));
        intRank.put(Class.INT32, Arrays.asList(32, 1));
        intRank.put(Class.INT64, Arrays.asList(64, 1));
        intRank.put(Class.UINT8, Arrays.asList(8, 0));
        intRank.put(Class.UINT16, Arrays.asList(16, 0));
        intRank.put(Class.UINT32, Arrays.asList(32, 0));
        intRank.put(Class.UINT64, Arrays.asList(64, 0));
    }

    static {
        lowerInt = new HashMap<>();
        List<Class> intClasses = Arrays.asList(
                Class.INT8, Class.INT16, Class.INT32, Class.INT64,
                Class.UINT8, Class.UINT16, Class.UINT32, Class.UINT64
        );

        for (Class f : intClasses) {
            lowerInt.put(f, new ArrayList<>());
            for (Class s : intClasses) {
                if (getResultClassInIntBinOp(f, s) == f) {
                    lowerInt.get(f).add(s);
                }
            }
        }

    }

    public Class typeClass;

    static {
        types = new HashMap<>();
        typesKinds = new ArrayList<>();

        typesSupportedInColumns = new HashMap<>();
        typesSupportedInColumnsKinds = new ArrayList<>();

        typesSupportedAsPrimary = new HashMap<>();
        typesSupportedAsPrimaryKinds = new ArrayList<>();

        createdConstants = new HashMap<>();
    }

    static {
        createdConstants.put(Class.BOOL, new YdbType(PrimitiveType.bool()));

        createdConstants.put(Class.INT8, new YdbType(PrimitiveType.int8()));
        createdConstants.put(Class.INT16, new YdbType(PrimitiveType.int16()));
        createdConstants.put(Class.INT32, new YdbType(PrimitiveType.int32()));
        createdConstants.put(Class.INT64, new YdbType(PrimitiveType.int64()));

        createdConstants.put(Class.UINT8, new YdbType(PrimitiveType.uint8()));
        createdConstants.put(Class.UINT16, new YdbType(PrimitiveType.uint16()));
        createdConstants.put(Class.UINT32, new YdbType(PrimitiveType.uint32()));
        createdConstants.put(Class.UINT64, new YdbType(PrimitiveType.uint64()));

        createdConstants.put(Class.FLOAT, new YdbType(PrimitiveType.float32()));
        createdConstants.put(Class.DOUBLE, new YdbType(PrimitiveType.float64()));

        createdConstants.put(Class.STRING, new YdbType(PrimitiveType.string()));
    }

    public static Class getResultClassInIntBinOp(Class left, Class right) {
        List<Integer> rankLeft = intRank.get(left);
        List<Integer> rankRight = intRank.get(right);

        for (int i = 0; i < rankLeft.size(); ++i) {
            if (rankLeft.get(i) > rankRight.get(i)) {
                return left;
            }
        }

        return right;
    }

    public static Class getLowerIntType(Class type) {
        List<Class> lower = lowerInt.get(type);
        return Randomly.fromList(lower);
    }

    public static YdbType bool() {
        return createdConstants.get(Class.BOOL);
    }

    public static YdbType int8() {
        return createdConstants.get(Class.INT8);
    }

    public static YdbType int16() {
        return createdConstants.get(Class.INT16);
    }

    public static YdbType int32() {
        return createdConstants.get(Class.INT32);
    }

    public static YdbType int64() {
        return createdConstants.get(Class.INT64);
    }

    public static YdbType uint8() {
        return createdConstants.get(Class.UINT8);
    }

    public static YdbType uint16() {
        return createdConstants.get(Class.UINT16);
    }

    public static YdbType uint32() {
        return createdConstants.get(Class.UINT32);
    }

    public static YdbType uint64() {
        return createdConstants.get(Class.UINT64);
    }

    public static YdbType float32() {
        return createdConstants.get(Class.FLOAT);
    }

    public static YdbType float64() {
        return createdConstants.get(Class.DOUBLE);
    }

    public static YdbType string() {
        return createdConstants.get(Class.STRING);
    }

    public static YdbType type(Class classType) {
        return createdConstants.get(classType);
    }

    static {
        typesKinds.add(Type.Kind.PRIMITIVE);
        types.put(Type.Kind.PRIMITIVE, Arrays.asList(
                PrimitiveType.uint8(),
                PrimitiveType.uint16(),
                PrimitiveType.uint32(),
                PrimitiveType.uint64(),
                PrimitiveType.int8(),
                PrimitiveType.int16(),
                PrimitiveType.int32(),
                PrimitiveType.int64(),
                PrimitiveType.float32(),
                PrimitiveType.float64(),
                PrimitiveType.bool(),
                PrimitiveType.string()
        ));
    }

    static {
        typesSupportedInColumnsKinds.add(Type.Kind.PRIMITIVE);
        typesSupportedInColumns.put(Type.Kind.PRIMITIVE, Arrays.asList(
                PrimitiveType.uint8(),
                PrimitiveType.uint32(),
                PrimitiveType.uint64(),
                PrimitiveType.int32(),
                PrimitiveType.int64(),
                PrimitiveType.float32(),
                PrimitiveType.float64(),
                PrimitiveType.bool(),
                PrimitiveType.string()
        ));
    }

    static {
        typesSupportedAsPrimaryKinds.add(Type.Kind.PRIMITIVE);
        typesSupportedAsPrimary.put(Type.Kind.PRIMITIVE, Arrays.asList(
                PrimitiveType.uint8(),
                PrimitiveType.uint32(),
                PrimitiveType.uint64(),
                PrimitiveType.int32(),
                PrimitiveType.int64(),
                PrimitiveType.bool(),
                PrimitiveType.string()
        ));
    }

    private void setClass(Type type) {
        switch (type.getKind()) {
            case PRIMITIVE: {
                PrimitiveType pt = (PrimitiveType) type;
                switch (pt.getId()) {
                    case Bool:
                        this.typeClass = Class.BOOL;
                        break;
                    case Int8:
                        this.typeClass = Class.INT8;
                        break;
                    case Uint8:
                        this.typeClass = Class.UINT8;
                        break;
                    case Int16:
                        this.typeClass = Class.INT16;
                        break;
                    case Uint16:
                        this.typeClass = Class.UINT16;
                        break;
                    case Int32:
                        this.typeClass = Class.INT32;
                        break;
                    case Uint32:
                        this.typeClass = Class.UINT32;
                        break;
                    case Int64:
                        this.typeClass = Class.INT64;
                        break;
                    case Uint64:
                        this.typeClass = Class.UINT64;
                        break;
                    case Float32:
                        this.typeClass = Class.FLOAT;
                        break;
                    case Float64:
                        this.typeClass = Class.DOUBLE;
                        break;
                    case String:
                        this.typeClass = Class.STRING;
                        break;
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
                        this.typeClass = Class.UNSUPPORTED;
                        break;
                }
                break;
            }
            case OPTIONAL:
                OptionalType ot = (OptionalType) type;
                setClass(ot.getItemType());
                break;
            case DECIMAL:
            case LIST:
            case TUPLE:
            case STRUCT:
            case DICT:
            case VARIANT:
            case VOID:
                this.typeClass = Class.UNSUPPORTED;
        }
    }

    public YdbType(Type type) {
        this.type = type;
        setClass(type);
    }

    public Type getYdbType() {
        return this.type;
    }

    public static YdbType getRandom() {
        Type.Kind kind = Randomly.fromList(typesKinds);
        return new YdbType(Randomly.fromList(types.get(kind)));
    }

    public static YdbType getRandomColumnType(boolean primary) {
        if (primary) {
            Type.Kind kind = Randomly.fromList(typesSupportedAsPrimaryKinds);
            return new YdbType(Randomly.fromList(typesSupportedAsPrimary.get(kind)));
        } else {
            Type.Kind kind = Randomly.fromList(typesSupportedInColumnsKinds);
            return new YdbType(Randomly.fromList(typesSupportedInColumns.get(kind)));
        }
    }

    public static boolean canBePrimary(Type t) {
        return typesSupportedAsPrimary.get(t.getKind()).contains(t);
    }



}