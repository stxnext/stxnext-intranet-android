
package com.stxnext.management.android.yaaic;

import android.content.Context;
import android.content.Intent;

import com.google.common.collect.Lists;
import com.stxnext.management.android.AppIntranet;
import com.stxnext.management.android.yaaic.activity.ConversationActivity;
import com.stxnext.management.android.yaaic.db.Database;
import com.stxnext.management.android.yaaic.model.Authentication;
import com.stxnext.management.android.yaaic.model.Identity;
import com.stxnext.management.android.yaaic.model.Server;
import com.stxnext.management.android.yaaic.model.Status;

public class YaaicConnector {

    private static final int MAIN_SERVER_ID = 1;

    private static YaaicConnector _instance;

    public static YaaicConnector getInstance() {
        if (_instance == null) {
            _instance = new YaaicConnector();
        }
        return _instance;
    }

    private YaaicConnector() {
        prepareDatabase();
    }

    public void launchIRC(Context context) {
        Server server = Yaaic.getInstance().getServerById(MAIN_SERVER_ID);
        Intent intent = new Intent(context, ConversationActivity.class);

        if (server.getStatus() == Status.DISCONNECTED && !server.mayReconnect()) {
            server.setStatus(Status.PRE_CONNECTING);
            intent.putExtra("connect", true);
        }
        intent.putExtra("serverId", server.getId());
        context.startActivity(intent);
    }

    private void prepareDatabase() {
        Database db = new Database(AppIntranet.getApp());

        Server server = db.getServerById(MAIN_SERVER_ID);
        Identity identity = new Identity();
        identity.setAliases(Lists.newArrayList("luczakp"));
        identity.setIdent("luczakp");
        identity.setNickname("luczakp");
        identity.setRealName("luczakp");
        
        if (server == null) {

            Authentication authentication = new Authentication();
            authentication.setSaslUsername("luczakp");
            authentication.setSaslPassword("stx");
            authentication.setNickservPassword("stx");
            server = new Server();

            long identityId = db.addIdentity(
                    identity.getNickname(),
                    identity.getIdent(),
                    identity.getRealName(),
                    identity.getAliases()
                    );

            server.setAuthentication(authentication);
            server.setHost("chat.freenode.net");
            server.setTitle("STXNEXT");

            long serverId = db.addServer(server, (int) identityId);

            db.setChannels((int) serverId, Lists.newArrayList("#stxnext"));
            // db.setCommands((int) serverId, commands);
            db.close();

            server.setId((int) serverId);
        }
        server.setIdentity(identity);
        server.setAutoJoinChannels(Lists.newArrayList("#stxnext"));

        Yaaic.getInstance().addServer(server);
    }

}
