package sqlancer.ydb;

import com.yandex.ydb.auth.iam.CloudAuthProvider;
import com.yandex.ydb.core.grpc.GrpcTransport;
import com.yandex.ydb.table.SchemeClient;
import com.yandex.ydb.table.SessionRetryContext;
import com.yandex.ydb.table.TableClient;
import com.yandex.ydb.table.rpc.grpc.GrpcSchemeRpc;
import com.yandex.ydb.table.rpc.grpc.GrpcTableRpc;
import sqlancer.SQLancerDBConnection;
import yandex.cloud.sdk.auth.provider.IamTokenCredentialProvider;

public class YdbConnection implements SQLancerDBConnection {

    public String root;

    private GrpcTransport transport;

    private TableClient tableClient;
    public SchemeClient schemeClient;

    public SessionRetryContext sessionRetryContext;

    public YdbConnection(YdbOptions options) {
        String connectionUrl = options.getConnectionURL();
        if (options.hasToken()) {
            this.transport = GrpcTransport.forConnectionString(connectionUrl)
                                .withAuthProvider(CloudAuthProvider.newAuthProvider(
                                        IamTokenCredentialProvider.builder().token(options.accessToken).build()
                                ))
                                .build();
        } else {
            this.transport = GrpcTransport.forConnectionString(connectionUrl).build();
        }
        this.root = transport.getDatabase();
        this.tableClient = TableClient.newClient(GrpcTableRpc.useTransport(transport)).build();
        this.schemeClient = SchemeClient.newClient(GrpcSchemeRpc.useTransport(transport)).build();
        this.sessionRetryContext = SessionRetryContext.create(this.tableClient).build();
    }

    @Override
    public String getDatabaseVersion() throws Exception {
        return transport.toString();
    }

    @Override
    public void close() throws Exception {
        this.tableClient.close();
        this.schemeClient.close();
        transport.close();
    }
}
