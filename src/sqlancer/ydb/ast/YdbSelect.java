package sqlancer.ydb.ast;

import sqlancer.ydb.YdbType;

import java.util.Collections;
import java.util.List;

public class YdbSelect implements YdbExpression, YdbRealSource {

    public enum SelectType {
        DISTINCT, ALL;
    }

    private SelectType fromOptions = YdbSelect.SelectType.ALL;
    private List<YdbColumnNode> fetchColumns = Collections.emptyList();
    private List<YdbExpression> groupByClause = Collections.emptyList();
    private List<YdbExpression> orderByClause = Collections.emptyList();

    private YdbSource source;
    private YdbExpression whereClause;
    private YdbExpression limitClause;
    private YdbExpression offsetClause;
    private YdbExpression havingClause;


    public void setFromOptions(SelectType type) {
        this.fromOptions = type;
    }

    public void setFetchColumns(List<YdbColumnNode> columns) {
        this.fetchColumns = columns;
    }

    public void setGroupByClause(List<YdbExpression> groupByClause) {
        this.groupByClause = groupByClause;
    }

    public void setOrderByClause(List<YdbExpression> orderByClause) {
        this.orderByClause = orderByClause;
    }

    public void setSource(YdbSource source) {
        this.source = source;
    }

    public void setWhereClause(YdbExpression whereClause) {
        this.whereClause = whereClause;
    }

    public void setLimitClause(YdbExpression limitClause) {
        this.limitClause = limitClause;
    }

    public void setOffsetClause(YdbExpression offsetClause) {
        this.offsetClause = offsetClause;
    }

    public void setHavingClause(YdbExpression havingClause) {
        this.havingClause = havingClause;
    }


    public SelectType getFromOptions() {
        return fromOptions;
    }

    public List<YdbColumnNode> getFetchColumns() {
        return fetchColumns;
    }

    public List<YdbExpression> getGroupByClause() {
        return groupByClause;
    }

    public List<YdbExpression> getOrderByClause() {
        return orderByClause;
    }

    public YdbSource getSource() {
        return source;
    }

    public YdbExpression getWhereClause() {
        return whereClause;
    }

    public YdbExpression getLimitClause() {
        return limitClause;
    }

    public YdbExpression getOffsetClause() {
        return offsetClause;
    }

    public YdbExpression getHavingClause() {
        return havingClause;
    }

    @Override
    public YdbType getExpressionType() {
        return null;
    }

    @Override
    public List<YdbColumnNode> getSourceColumns() {
        return fetchColumns;
    }

    @Override
    public String getName() {
        return null;
    }


}
