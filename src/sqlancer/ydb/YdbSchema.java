package sqlancer.ydb;

import com.yandex.ydb.scheme.SchemeOperationProtos;
import com.yandex.ydb.table.SchemeClient;
import com.yandex.ydb.table.SessionRetryContext;
import com.yandex.ydb.table.TableClient;
import com.yandex.ydb.table.description.ListDirectoryResult;
import com.yandex.ydb.table.description.TableColumn;
import com.yandex.ydb.table.description.TableDescription;
import com.yandex.ydb.table.rpc.grpc.GrpcSchemeRpc;
import com.yandex.ydb.table.rpc.grpc.GrpcTableRpc;
import com.yandex.ydb.table.values.Type;
import sqlancer.Randomly;
import sqlancer.common.schema.*;

import sqlancer.postgres.PostgresSchema;
import sqlancer.ydb.YdbProvider.YdbGlobalState;
import sqlancer.ydb.YdbSchema.YdbTable;

import java.util.*;

public class YdbSchema extends AbstractSchema<YdbGlobalState, YdbTable> {

    public YdbSchema(List<YdbTable> databaseTables) {
        super(databaseTables);
    }

    public YdbSchema(YdbConnection con, String databasePath) {
        super(YdbSchema.getDatabaseTables(con, databasePath));
    }

    public static class YdbColumn extends AbstractTableColumn<YdbTable, YdbType> {

        boolean isPrimary;

        public YdbColumn(String name, YdbTable table, YdbType type, boolean isPrimary) {
            super(name, table, type);
            this.isPrimary = isPrimary;
        }

        public YdbColumn(String name, YdbType type, boolean isPrimary) {
            super(name, null, type);
            this.isPrimary = isPrimary;
        }

        public void setPrimary(boolean isPrimary) {
            this.isPrimary = isPrimary;
        }

        public static YdbColumn createDummy(String name) {
            return new YdbColumn(name, YdbType.getRandom(), false);
        }
        
    }

    public static class YdbTable extends AbstractTable<YdbColumn, TableIndex, YdbGlobalState> {

        String fullPath;
        String dbPath;
        List<YdbColumn> primaryColumns;

        public YdbTable(String fullPath, String dbPath, List<YdbColumn> columns, List<YdbColumn> primaryColumns, List<TableIndex> indexes, boolean isView) {
            super(cropLastDir(dbPath), columns, indexes, isView);
            this.fullPath = fullPath;
            this.dbPath = dbPath;
            this.primaryColumns = primaryColumns;
        }

        public List<YdbColumn> getNonEmptySubsetWithAllPrimaryColumns() {
            List<YdbColumn> result = getRandomNonEmptyColumnSubset();

            for (YdbColumn col : primaryColumns) {
                if (!result.contains(col)) {
                    result.add(col);
                }
            }

            return result;
        }


        public String getFullPath() {
            return fullPath;
        }

        public String getDbPath() {
            return dbPath;
        }

        @Override
        public long getNrRows(YdbGlobalState globalState) {
            throw new UnsupportedOperationException();
        }
    }

    public static class YdbTables extends AbstractTables<YdbTable, YdbColumn> {
        public YdbTables(List<YdbTable> tables) {
            super(tables);
        }
    }

    public static String cropLastDir(String path) {
        int lastDel = 0;
        for (int i = 0; i < path.length(); ++i) {
            if (path.charAt(i) == '/') {
                lastDel = i;
            }
        }
        return path.substring(lastDel + 1);
    }

    private static List<YdbTable> getDatabaseTables(YdbConnection con, String root) {
        String databaseDir = cropLastDir(root);
        List<YdbTable> databaseTables = new ArrayList<>();
        List<String> tableFullPaths = getTableNames(con, root);
        for (String fullPath : tableFullPaths) {
            Map<ColumnDifferentiation, List<YdbColumn>> tableColumns = getTableColumns(con, fullPath);
            YdbTable t = new YdbTable(
                    fullPath,
                    databaseDir + "/" + cropLastDir(fullPath),
                    tableColumns.get(ColumnDifferentiation.ALL),
                    tableColumns.get(ColumnDifferentiation.PRIMARY),
                    Collections.emptyList(),
                    false);
            for (YdbColumn c : tableColumns.get(ColumnDifferentiation.ALL)) {
                c.setTable(t);
            }
            databaseTables.add(t);
        }
        return databaseTables;
    }

    private static List<String> getTableNames(YdbConnection con, String root) {
        List<String> tableNames = new ArrayList<>();
        Stack<String> dirs = new Stack<>();
        dirs.push(root);
        while (!dirs.empty()) {
            String dir = dirs.peek();
            dirs.pop();

            ListDirectoryResult listResult = con.schemeClient.listDirectory(dir).join().expect("list directory error");
            for (SchemeOperationProtos.Entry child : listResult.getChildren()) {
                String entryName = dir + "/" + child.getName();
                if (child.getType() == SchemeOperationProtos.Entry.Type.DIRECTORY) {
                    dirs.push(entryName);
                } else if (child.getType() == SchemeOperationProtos.Entry.Type.TABLE) {
                    tableNames.add(entryName);
                }
            }
        }

        return tableNames;
    }

    private enum ColumnDifferentiation {
        ALL,
        PRIMARY
    }

    private static Map<ColumnDifferentiation, List<YdbColumn>> getTableColumns(YdbConnection con, String tableName) {
        Map<ColumnDifferentiation, List<YdbColumn>> columns = new HashMap<>();
        columns.put(ColumnDifferentiation.ALL, new ArrayList<>());
        columns.put(ColumnDifferentiation.PRIMARY, new ArrayList<>());

        SessionRetryContext context = con.sessionRetryContext;

        TableDescription description = context.supplyResult(session -> {
            return session.describeTable(tableName);
        }).join().expect("describe table error");

        List<String> primaryKeys = description.getPrimaryKeys();
        for (TableColumn innerColumn : description.getColumns()) {
            String name = innerColumn.getName();
            Type type = innerColumn.getType();
            Boolean isPrimary = primaryKeys.contains(innerColumn.getName());
            YdbColumn column = new YdbColumn(name, null, new YdbType(type), isPrimary);

            columns.get(ColumnDifferentiation.ALL).add(column);
            if (isPrimary) {
                columns.get(ColumnDifferentiation.PRIMARY).add(column);
            }
        }

        return columns;
    }

    public YdbTables getRandomTableNonEmptyTables() {
        return new YdbTables(Randomly.nonEmptySubset(getDatabaseTables()));
    }

}
