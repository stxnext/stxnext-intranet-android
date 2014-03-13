
package com.stxnext.management.server.planningpoker.server.database.managers;

import java.io.File;
import java.sql.SQLException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.db.SqliteDatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;
import com.stxnext.management.server.planningpoker.server.ServerConfigurator;
import com.stxnext.management.server.planningpoker.server.database.dto.Player;

public class DAO {

    protected static final String DEFAULT_DATABASE_URL = "jdbc:h2:mem:ormlite";
    protected String databaseUrl = DEFAULT_DATABASE_URL;
    protected static final String DB_NAME = "poker";

    public static DAO _instance;

    public static DAO getInstance() {
        if (_instance == null) {
            _instance = new DAO();
        }
        return _instance;
    }

    protected JdbcConnectionSource connectionSource;
    protected DatabaseType databaseType = null;
    private Logger logger;

    private Dao<Player, Long> playerDao;

    private DAO() {
        logger = ServerConfigurator.getInstance().getLogger();
        createAndConnect();
    }

    protected void setDatabaseParams() {
        databaseUrl = "jdbc:sqlite:";
        try {
            connectionSource = new JdbcConnectionSource(DEFAULT_DATABASE_URL);
            databaseType = new SqliteDatabaseType();
        } catch (Exception e) {
            logger.log(Level.ERROR, "", e);
        }
    }

    private void setupDatabase(ConnectionSource connectionSource) {
        try {
            playerDao = DaoManager.createDao(connectionSource, Player.class);
            if (!playerDao.isTableExists()) {
                TableUtils.createTable(connectionSource, Player.class);
            }

        } catch (SQLException e) {
            logger.log(Level.ERROR, "", e);
        }

    }

    public void createAndConnect() {
        DatabaseConnection conn = null;
        try {
            File dbDir = new File("poker");
            String dbUrl = "jdbc:h2:" + dbDir.getPath() + "/" + DB_NAME;
            connectionSource = new JdbcConnectionSource(dbUrl);
            conn = connectionSource.getReadWriteConnection();
            setupDatabase(connectionSource);
        } catch (Exception c) {
            logger.log(Level.ERROR, "", c);
        }
    }

}
