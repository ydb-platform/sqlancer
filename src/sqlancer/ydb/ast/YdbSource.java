package sqlancer.ydb.ast;

import java.util.List;

public interface YdbSource {

    public List<YdbColumnNode> getSourceColumns();

    public String getName();

}
