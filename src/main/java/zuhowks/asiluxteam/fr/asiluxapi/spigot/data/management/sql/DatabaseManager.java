package zuhowks.asiluxteam.fr.asiluxapi.spigot.data.management.sql;

import org.bukkit.configuration.file.FileConfiguration;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.AsiluxAPI;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public enum DatabaseManager {
    //Use Players Account database
    PLAYERS_ACCOUNT("players-account");

    private final SQLDatabase SQLDatabase;

    DatabaseManager(String database) {
        FileConfiguration configuration = AsiluxAPI.INSTANCE.getConfig();
        this.SQLDatabase = new SQLDatabase(
                new DatabaseCredential(
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

    public static void initAllDatabaseConnections () {
        for (DatabaseManager databaseManager : values()) {
            databaseManager.SQLDatabase.initPool();
            if (databaseManager.equals(DatabaseManager.PLAYERS_ACCOUNT)) { //&& !AsiluxAPI.INSTANCE.getServer().spigot().getSpigotConfig().getBoolean("settings.bungeecord")) {
                try {
                    PreparedStatement ps = databaseManager.getDatabaseAccess().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `players_account` (\n" +
                            "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                            "  `uuid` varchar(36) CHARACTER SET latin1 NOT NULL,\n" +
                            "  `rank` varchar(32) CHARACTER SET latin1 NOT NULL,\n" +
                            "  `coins` int(11) NOT NULL,\n" +
                            "  `level` smallint(6) NOT NULL,\n" +
                            "  `xp` int(11) NOT NULL,\n" +
                            "  `mmr` int(11),\n" +
                            "  `lang` varchar(12) CHARACTER SET latin1 NOT NULL,\n" +
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

    public static void closeAllDatabaseConnections () {
        for (DatabaseManager databaseManager : values()) {
            databaseManager.SQLDatabase.closePool();
        }
    }
}
