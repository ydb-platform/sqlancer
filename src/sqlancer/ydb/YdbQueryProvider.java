package sqlancer.ydb;

@FunctionalInterface
public interface YdbQueryProvider<S> {
    YdbQueryAdapter getQuery(S globalState) throws Exception;
}
