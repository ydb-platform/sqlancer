package sqlancer.ydb;

import sqlancer.common.query.Query;

public abstract class YdbQueryAdapter extends Query<YdbConnection> {
    @Override
    public String getQueryString() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getUnterminatedQueryString() {
        throw new UnsupportedOperationException();
    }
}
