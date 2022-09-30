package sqlancer.ydb.ast;

import java.util.List;
import java.util.stream.Collectors;

public class YdbSubquery implements YdbAliasSource {

    YdbSelect select;

    String alias;

    public YdbSelect getSelect() {
        return select;
    }

    public String getAlias() {
        return alias;
    }

    public YdbSubquery(YdbSelect select, String alias) {
        this.select = select;
        this.alias = alias;
    }

    @Override
    public List<YdbColumnNode> getSourceColumns() {
        return select.getSourceColumns().stream().map(t -> new YdbRealColumn(this, t.getName(), t.getType()))
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return alias;
    }
}
