package sqlancer.ydb.ast;

import sqlancer.ydb.YdbType;

public class YdbRealColumn implements YdbColumnNode {

    private YdbSource source;

    private String name;

    private YdbType type;

    public YdbSource getSource() {
        return source;
    }

    public YdbRealColumn(YdbSource source, String name, YdbType type) {
        this.source = source;
        this.name = name;
        this.type = type;
    }

    public static YdbRealColumn create(YdbSource source, String name, YdbType type) {
        return new YdbRealColumn(source, name, type);
    }

    @Override
    public YdbType getExpressionType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public YdbType getType() {
        return type;
    }

}
