package zuhowks.asiluxteam.fr.asiluxapi.bc.data.management.mysql;

import net.md_5.bungee.config.Configuration;
import zuhowks.asiluxteam.fr.asiluxapi.bc.AsiluxAPI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public enum DatabaseManager {
    //Use main database
    PLAYERS_ACCOUNT("players-account");

    private DatabaseAccess databaseAccess;

    DatabaseManager(String database) {
        Configuration configuration = AsiluxAPI.INSTANCE.configFile.getConfig();
        this.databaseAccess = new DatabaseAccess(
                new DatabaseCredantial(
                        configuration.getString("mysql-manager." + database + ".host"),
                        configuration.getInt("mysql-manager." + database + ".port"),
                        configuration.getString("mysql-manager." + database + ".database"),
                        configuration.getString("mysql-manager." + database + ".username"),
                        configuration.getString("mysql-manager." + database + ".password")
        ));
    }

    public DatabaseAccess getDatabaseAccess() {
        return databaseAccess;
    }

    public static void initAllDatabaseConnection () {
        for (DatabaseManager databaseManager : values()) {
            databaseManager.databaseAccess.initPool();
            if (databaseManager.equals(DatabaseManager.PLAYERS_ACCOUNT)) {
                try {
                    PreparedStatement ps = databaseManager.getDatabaseAccess().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `players_account` (\n" +
                            "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                            "  `uuid` varchar(36) CHARACTER SET latin1 NOT NULL,\n" +
                            "  `rank` varchar(32) CHARACTER SET latin1 NOT NULL,\n" +
                            "  `coins` int(11) NOT NULL,\n" +
                            "  `level` smallint(6) NOT NULL,\n" +
                            "  `xp` int(11) NOT NULL,\n" +
                            "  `mmr` int(11),\n" +
                            "  PRIMARY KEY (`id`)," +
                            "  UNIQUE (`uuid`)" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8"
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
            databaseManager.databaseAccess.closePool();
        }
    }
}
