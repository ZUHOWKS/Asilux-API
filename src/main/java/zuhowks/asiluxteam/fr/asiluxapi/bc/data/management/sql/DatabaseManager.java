package zuhowks.asiluxteam.fr.asiluxapi.bc.data.management.sql;

import net.md_5.bungee.config.Configuration;
import zuhowks.asiluxteam.fr.asiluxapi.bc.AsiluxAPI;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public enum DatabaseManager {
    //Use main database
    PLAYERS_ACCOUNT("players-account");

    private SQLDatabase SQLDatabase;

    DatabaseManager(String database) {
        Configuration configuration = AsiluxAPI.INSTANCE.configFile.getConfig();
        this.SQLDatabase = new SQLDatabase(
                new DatabaseCredantial(
                        configuration.getString("sql-manager." + database + ".host"),
                        configuration.getInt("sql-manager." + database + ".port"),
                        configuration.getString("sql-manager." + database + ".database"),
                        configuration.getString("sql-manager." + database + ".username"),
                        configuration.getString("sql-manager." + database + ".password")
        ));
    }

    public SQLDatabase getDatabaseAccess() {
        return SQLDatabase;
    }

    public static void initAllDatabaseConnection () {
        for (DatabaseManager databaseManager : values()) {
            databaseManager.SQLDatabase.initPool();
            if (databaseManager.equals(DatabaseManager.PLAYERS_ACCOUNT)) {
                try {
                    PreparedStatement ps = databaseManager.getDatabaseAccess().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS players_account (" +
                            "  uuid varchar(36) CHARACTER SET latin1 NOT NULL," +
                            "  ranked varchar(32) CHARACTER SET latin1 NOT NULL," +
                            "  coins int(11) NOT NULL," +
                            "  level int(6) NOT NULL," +
                            "  xp int(11) NOT NULL," +
                            "  mmr int(11)," +
                            "  lang varchar(11) CHARACTER SET latin1 NOT NULL," +
                            "  PRIMARY KEY (uuid)," +
                            ");"
                    );
                    ps.execute();
                } catch (SQLException throwable) {
                    throwable.printStackTrace();
                }
            }
        }
    }

    public static void closeAllDatabaseConnection () {
        for (DatabaseManager databaseManager : values()) {
            databaseManager.SQLDatabase.closePool();
        }
    }
}
