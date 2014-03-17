package com.stxnext.management.server.planningpoker.server.dto.combined;

import java.sql.SQLException;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.table.DatabaseTable;
import com.stxnext.management.server.planningpoker.server.database.managers.DAO;

@DatabaseTable(tableName = PlayerSession.ENTITY_NAME)
public class PlayerSession {

    public static final String ENTITY_NAME = "player_session";
    
    public static final String FIELD_SESSION_ID = "session_id";
    public static final String FIELD_PLAYER_ID = "player_id";
    
    @DatabaseField(generatedId = true)
    int id;

    @DatabaseField(foreign = true, columnName = FIELD_PLAYER_ID)
    Player player;

    @DatabaseField(foreign = true, columnName = FIELD_SESSION_ID)
    Session session;
    
    public PlayerSession(Player player, Session session){
        this.player = player;
        this.session = session;
    }
    
    public PlayerSession(){}

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public int getId() {
        return id;
    }
    
    public static PreparedQuery<Player> makePlayerForSession(DAO dao) throws SQLException {
        // build our inner query for UserPost objects
        QueryBuilder<PlayerSession, Long> userPostQb = dao.getPlayerSessionDao().queryBuilder();
        // just select the post-id field
        userPostQb.selectColumns(PlayerSession.FIELD_PLAYER_ID);
        SelectArg userSelectArg = new SelectArg();
        // you could also just pass in user1 here
        userPostQb.where().eq(PlayerSession.FIELD_SESSION_ID, userSelectArg);

        // build our outer query for Post objects
        QueryBuilder<Player, Long> postQb = dao.getPlayerDao().queryBuilder();
        // where the id matches in the post-id from the inner query
        postQb.where().in(Player.FIELD_ID, userPostQb);
        return postQb.prepare();
    }
    
    public static PreparedQuery<Session> makeUsersForPostQuery(DAO dao) throws SQLException {
        QueryBuilder<PlayerSession, Long> userPostQb = dao.getPlayerSessionDao().queryBuilder();
        // this time selecting for the user-id field
        userPostQb.selectColumns(PlayerSession.FIELD_SESSION_ID);
        SelectArg postSelectArg = new SelectArg();
        userPostQb.where().eq(PlayerSession.FIELD_PLAYER_ID, postSelectArg);

        // build our outer query
        QueryBuilder<Session, Long> userQb = dao.getSessionDao().queryBuilder();
        // where the user-id matches the inner query's user-id field
        userQb.where().in(Session.FIELD_ID, userPostQb);
        return userQb.prepare();
    }
    
}
