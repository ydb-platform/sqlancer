package sqlancer.ydb;

import com.yandex.ydb.scheme.SchemeOperationProtos;
import com.yandex.ydb.table.SchemeClient;
import com.yandex.ydb.table.SessionRetryContext;
import com.yandex.ydb.table.TableClient;
import com.yandex.ydb.table.description.ListDirectoryResult;
import com.yandex.ydb.table.rpc.grpc.GrpcSchemeRpc;
import com.yandex.ydb.table.rpc.grpc.GrpcTableRpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class YdbDatabaseDeleter {

    public YdbConnection connection;

    public YdbDatabaseDeleter(YdbConnection connection) {
        this.connection = connection;
    }

    public void deleteFolder(String path) {
        SessionRetryContext ctx = connection.sessionRetryContext;
        deleteFolderRec(path, connection.schemeClient, ctx);
    }

    private void deleteFolderRec(String path, SchemeClient schemeClient, SessionRetryContext ctx) {
        try {
            ListDirectoryResult listResult = schemeClient.listDirectory(path).join().expect("list directory error");
            for (SchemeOperationProtos.Entry child : listResult.getChildren()) {
                String entryName = path + "/" + child.getName();
                if (child.getType() == SchemeOperationProtos.Entry.Type.DIRECTORY) {
                    deleteFolderRec(entryName, schemeClient, ctx);
                } else if (child.getType() == SchemeOperationProtos.Entry.Type.TABLE) {
                    try {
                        ctx.supplyStatus(session -> {
                            return session.dropTable(entryName);
                        }).join().expect("drop table error");
                    } catch (Exception e) {}
                }
            }
        } catch (Exception e) {}
    }
}
