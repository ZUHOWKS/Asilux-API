package zuhowks.asiluxteam.fr.asiluxapi.bc.data.management.redis;

import net.md_5.bungee.config.Configuration;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import zuhowks.asiluxteam.fr.asiluxapi.bc.AsiluxAPI;
import zuhowks.asiluxteam.fr.asiluxapi.bc.data.management.sql.DatabaseManager;
import zuhowks.asiluxteam.fr.asiluxapi.commons.player.Account;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public enum RedisManager {

    PLAYERS_ACCOUNT("players-account", DatabaseManager.PLAYERS_ACCOUNT),
    GAME_API("game-api");

    private RedisAccess redisAccess;
    private RedisCredentials credentials;
    private DatabaseManager databaseManager;

    RedisManager(String string) {

        Configuration configuration = AsiluxAPI.INSTANCE.configFile.getConfig();

        this.credentials = new RedisCredentials(
                configuration.getString("redis-manager." + string + ".ip"),
                configuration.getString("redis-manager." + string + ".password"),
                configuration.getInt("redis-manager." + string + ".port"),
                configuration.getInt("redis-manager." + string + ".database")
        );
        this.redisAccess = new RedisAccess(this.credentials);
        this.databaseManager = null;
    }

    RedisManager(String string, DatabaseManager databaseManager) {
        Configuration configuration = AsiluxAPI.INSTANCE.configFile.getConfig();

        this.credentials = new RedisCredentials(
                configuration.getString("redis-manager." + string + ".ip"),
                configuration.getString("redis-manager." + string + ".password"),
                configuration.getInt("redis-manager." + string + ".port"),
                configuration.getInt("redis-manager." + string + ".database")
        );
        this.redisAccess = new RedisAccess(this.credentials);
        this.redisAccess = new RedisAccess(this.credentials);
        this.databaseManager = databaseManager;
    }

    public RedisAccess getRedisAccess() {
        return redisAccess;
    }

    public RedisCredentials getCredentials() {
        return credentials;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public static void initRedissons() {
        for (RedisManager redisManager : values()) {
            final Config config = new Config();

            RedisCredentials credentials = redisManager.getCredentials();

            config.setCodec(new JsonJacksonCodec());
            config.setThreads(2);
            config.setNettyThreads(2);
            config.useSingleServer()
                    .setAddress(credentials.toRedisURL())
                    .setPassword(credentials.getPassword())
                    .setDatabase(credentials.getDatabase())
                    .setClientName(credentials.getClientName());
            redisManager.getRedisAccess().setRedissonClient(Redisson.create(config));
        }
    }

    public static void closeRedissons() throws SQLException {
        for (RedisManager redisManager : values()) {
            DatabaseManager databaseManager = redisManager.getDatabaseManager();
            if (databaseManager != null && databaseManager.equals(DatabaseManager.PLAYERS_ACCOUNT)) {
                final RedissonClient redissonClient = redisManager.getRedisAccess().getRedissonClient();
                for (String key : redissonClient.getKeys().getKeys()) {
                    final RBucket<Account> accountRBucket = redissonClient.getBucket(key);
                    final Account account = accountRBucket.get();

                    final PreparedStatement ps = databaseManager.getDatabaseAccess().getConnection().prepareStatement("UPDATE players_account SET uuid=?, ranked=?, coins=?, level=?, xp=?, mmr=?, lang=?");
                    ps.setString(1, account.getUuid().toString());
                    ps.setString(2, account.getRank());
                    ps.setInt(3, account.getCoins());
                    ps.setInt(4, account.getLevel());
                    ps.setInt(5, account.getXp());
                    ps.setInt(6, account.getMMR());
                    ps.setString(7, account.getLang());
                    ps.execute();
                }
            }
            redisManager.getRedisAccess().getRedissonClient().shutdown();
        }
    }
}