package sqlancer.ydb;

import sqlancer.common.visitor.BinaryOperation;
import sqlancer.common.visitor.ToStringVisitor;
import sqlancer.ydb.ast.YdbJoin.JoinType;
import sqlancer.ydb.ast.*;

import java.util.List;

public final class YdbToStringVisitor extends ToStringVisitor<YdbExpression> implements YdbVisitor {

    @Override
    public void visitSpecific(YdbExpression expr) {
        YdbVisitor.super.visit(expr);
    }

    @Override
    public void visit(YdbConstant constant) {
        sb.append(constant.getTextRepresentation());
    }

    @Override
    public String get() {
        return sb.toString();
    }

    @Override
    public void visit(YdbPrefixOperation op) {
        sb.append(op.getTextRepresentation());
        sb.append(" (");
        visit(op.getExpression());
        sb.append(")");
    }

    @Override
    public void visit(YdbSelect s) {
        sb.append("SELECT ");
        if (s.getFromOptions() == YdbSelect.SelectType.DISTINCT) {
            sb.append("DISTINCT ");
        }
        if (s.getFetchColumns() == null) {
            sb.append("*");
        } else {
            visitListOfColumns(s.getFetchColumns());
        }
        sb.append(" FROM ");
        visit(s.getSource());

        if (s.getWhereClause() != null) {
            sb.append(" WHERE ");
            visit(s.getWhereClause());
        }
        if (s.getGroupByClause().size() > 0) {
            sb.append(" GROUP BY ");
            visit(s.getGroupByClause());
        }
        if (s.getHavingClause() != null) {
            sb.append(" HAVING ");
            visit(s.getHavingClause());
        }
        if (!s.getOrderByClause().isEmpty()) {
            sb.append(" ORDER BY ");
            visit(s.getOrderByClause());
        }
        if (s.getLimitClause() != null) {
            sb.append(" LIMIT ");
            visit(s.getLimitClause());
        }

        if (s.getOffsetClause() != null) {
            sb.append(" OFFSET ");
            visit(s.getOffsetClause());
        }
    }

    @Override
    public void visit(YdbOrderByTerm op) {
        sb.append(op.getColumn().getName());
        sb.append(" ");
        sb.append(op.getOrder());
    }

    @Override
    public void visit(YdbFunction f) {
        sb.append(f.getFunctionName());
        sb.append("(");
        int i = 0;
        for (YdbExpression arg : f.getArguments()) {
            if (i++ != 0) {
                sb.append(", ");
            }
            visit(arg);
        }
        sb.append(")");
    }

    @Override
    public void visit(YdbCastOperation cast) {
        sb.append("CAST(");
        visit(cast.getExpression());
        sb.append(" AS ");
        appendType(cast);
        sb.append(")");
    }

    private void appendType(YdbCastOperation cast) {
        YdbType type = cast.getType();
        switch (type.typeClass) {
        case BOOL:
            sb.append("Bool");
            break;
        case INT8:
            sb.append("Int8");
            break;
        case INT16:
            sb.append("Int16");
            break;
        case INT32:
            sb.append("Int32");
            break;
        case INT64:
            sb.append("Int64");
            break;
        case UINT8:
            sb.append("Uint8");
            break;
        case UINT16:
            sb.append("Uint16");
            break;
        case UINT32:
            sb.append("Uint32");
            break;
        case UINT64:
            sb.append("Uint64");
            break;
        case FLOAT:
            sb.append("Float");
            break;
        case DOUBLE:
            sb.append("Double");
            break;
        case STRING:
            sb.append("String");
            break;
        default:
            throw new AssertionError(cast.getType());
        }
    }

    @Override
    public void visit(YdbInOperation op) {
        sb.append("(");
        visit(op.getExpr());
        sb.append(")");
        if (!op.isTrue()) {
            sb.append(" NOT");
        }
        sb.append(" IN (");
        visit(op.getListElements());
        sb.append(")");
    }

    @Override
    public void visit(YdbAggregate op) {
        sb.append(op.getFunction());
        sb.append("(");
        visit(op.getArgs());
        sb.append(")");
    }

    @Override
    public void visit(YdbPostfixOperation op) {
        sb.append("(");
        visit(op.getExpression());
        sb.append(")");
        sb.append(" ");
        sb.append(op.getOperatorTextRepresentation());
    }

    @Override
    public void visit(YdbBinaryLogicalOperation op) {
        super.visit((BinaryOperation<YdbExpression>) op);
    }

    @Override
    public void visit(YdbLikeOperation op) {
        super.visit((BinaryOperation<YdbExpression>) op);
    }

    @Override
    public void visit(YdbExpressionAlias exprAlias) {
        YdbExpression expression = exprAlias.getExpression();
        visit(expression);
        sb.append(" AS " + exprAlias.getAlias());
    }

    public void visitListOfColumns(List<YdbColumnNode> expressions) {
        for (int i = 0; i < expressions.size(); i++) {
            if (i != 0) {
                sb.append(", ");
            }
            visit(expressions.get(i));
        }
    }

    //columns
    @Override
    public void visit(YdbRealColumn column) {
        YdbSource source = column.getSource();
        if (source != null) {
            sb.append(source.getName() + ".");
        }
        sb.append(column.getName());
    }

    @Override
    public void visit(YdbAliasColumn column) {
        YdbExpression realExpression = column.getRealExpression();
        visit(realExpression);
        sb.append(" AS " + column.getName());
    }

    // sources
    @Override
    public void visit(YdbSubquery subquery) {
        sb.append("(");
        visit(subquery.getSelect());
        sb.append(") AS ");
        sb.append(subquery.getAlias());
    }

    @Override
    public void visit(YdbRealTable table) {
        sb.append("`" + table.getTable().getFullPath() + "`");
    }

    @Override
    public void visit(YdbJoin join) {
        List<YdbAliasTable> tables = join.getJoinTables();
        List<JoinType> joinTypes = join.getJoinTypes();
        List<YdbExpression> joinConditions = join.getJoinConditions();

        visit(tables.get(0));
        for (int i = 0; i < joinTypes.size(); ++i) {
            sb.append(" " + joinTypes.get(i).toString() + " JOIN ");
            visit(tables.get(i + 1));
            if (joinConditions.get(i) != null) {
                sb.append(" ON ");
                visit(joinConditions.get(i));
            }
            if (i + 1 < joinTypes.size()) {
                sb.append(" ");
            }
        }

    }

    @Override
    public void visit(YdbAliasTable table) {
        sb.append("`" + table.getRealTable().getFullPath() + "` AS " + table.getAlias());
    }

}
