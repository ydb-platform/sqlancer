package sqlancer.ydb;

import sqlancer.ydb.ast.*;
import sqlancer.ydb.YdbProvider.YdbGlobalState;
import sqlancer.ydb.gen.YdbExpressionGenerator;

import java.util.List;

public interface YdbVisitor {

    // expressions
    void visit(YdbConstant constant);

    void visit(YdbPrefixOperation op);
    
    void visit(YdbPostfixOperation op);

    void visit(YdbSelect op);

    void visit(YdbOrderByTerm op);

    void visit(YdbFunction f);

    void visit(YdbCastOperation cast);

    void visit(YdbInOperation op);

    void visit(YdbAggregate op);

    void visit(YdbBinaryLogicalOperation op);

    void visit(YdbLikeOperation op);

    void visit(YdbExpressionAlias exprAlias);

    // columns
    void visit(YdbRealColumn column);

    void visit(YdbAliasColumn column);

    // tables
    void visit(YdbRealTable table);

    void visit(YdbJoin join);

    void visit(YdbAliasTable table);

    void visit(YdbSubquery subquery);

    default void visit(YdbColumnNode column) {
        if (column instanceof YdbRealColumn) {
            visit((YdbRealColumn) column);
        } else if (column instanceof YdbAliasColumn) {
            visit((YdbAliasColumn) column);
        }
    }

    default void visit(YdbSource source) {
        if (source instanceof YdbRealTable) {
            visit((YdbRealTable) source);
        } else if (source instanceof YdbJoin) {
            visit((YdbJoin) source);
        } else if (source instanceof YdbAliasTable) {
            visit((YdbAliasTable) source);
        } else if (source instanceof YdbSubquery) {
            visit((YdbSubquery) source);
        }
    }

    default void visit(YdbExpression expression) {
        if (expression instanceof YdbConstant) {
            visit((YdbConstant) expression);
        } else if (expression instanceof YdbPostfixOperation) {
            visit((YdbPostfixOperation) expression);
        } else if (expression instanceof YdbRealColumn) {
            visit((YdbRealColumn) expression);
        } else if (expression instanceof YdbPrefixOperation) {
            visit((YdbPrefixOperation) expression);
        } else if (expression instanceof YdbSelect) {
            visit((YdbSelect) expression);
        } else if (expression instanceof YdbOrderByTerm) {
            visit((YdbOrderByTerm) expression);
        } else if (expression instanceof YdbFunction) {
            visit((YdbFunction) expression);
        } else if (expression instanceof YdbCastOperation) {
            visit((YdbCastOperation) expression);
        } else if (expression instanceof YdbInOperation) {
            visit((YdbInOperation) expression);
        } else if (expression instanceof YdbAggregate) {
            visit((YdbAggregate) expression);
        } else if (expression instanceof YdbLikeOperation) {
            visit((YdbLikeOperation) expression);
        } else if (expression instanceof YdbExpressionAlias) {
            visit((YdbExpressionAlias) expression);
        } else {
            throw new AssertionError(expression);
        }
    }

    static String asString(YdbExpression expr) {
        YdbToStringVisitor visitor = new YdbToStringVisitor();
        visitor.visit(expr);
        return visitor.get();
    }

    static String getExpressionAsString(YdbGlobalState globalState, YdbType type, List<YdbColumnNode> columns) {
        YdbExpression expression = YdbExpressionGenerator.generateExpression(globalState, columns, type);
        YdbToStringVisitor visitor = new YdbToStringVisitor();
        visitor.visit(expression);
        return visitor.get();
    }

}