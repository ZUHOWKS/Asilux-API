package zuhowks.asiluxteam.fr.asiluxapi.bc.data.management.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public enum DatabaseManager {
    //Use main database
    PLAYERS_ACCOUNT(new DatabaseCredantial("localhost",
            3308,
            "test",
            "root",
            "root"
    ));

    private DatabaseAccess databaseAccess;

    DatabaseManager(DatabaseCredantial credantial) {
        this.databaseAccess = new DatabaseAccess(credantial);
    }

    public DatabaseAccess getDatabaseAccess() {
        return databaseAccess;
    }

    public static void initAllDatabaseConnection () {
        for (DatabaseManager databaseManager : values()) {
            databaseManager.databaseAccess.initPool();
            if (databaseManager.equals(DatabaseManager.PLAYERS_ACCOUNT)) {
                final Connection connection = databaseManager.getDatabaseAccess().getConnection();
                try {
                    PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `players_account` (\n" +
                            "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                            "  `uuid` varchar(36) CHARACTER SET latin1 NOT NULL,\n" +
                            "  `rank` varchar(32) CHARACTER SET latin1 NOT NULL,\n" +
                            "  `coins` int(11) NOT NULL,\n" +
                            "  `level` smallint(6) NOT NULL,\n" +
                            "  `xp` int(11) NOT NULL,\n" +
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
