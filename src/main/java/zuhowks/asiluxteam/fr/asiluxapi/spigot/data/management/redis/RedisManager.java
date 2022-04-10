package zuhowks.asiluxteam.fr.asiluxapi.spigot.data.management.redis;

import org.bukkit.configuration.file.FileConfiguration;
import org.redisson.Redisson;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.AsiluxAPI;

public enum RedisManager {

    PLAYERS_ACCOUNT("players-account"),
    GAME_API("game-api");


    private RedisAccess redisAccess;

    RedisManager(String string) {

        FileConfiguration configuration = AsiluxAPI.INSTANCE.getConfig();
        this.redisAccess = new RedisAccess(
                new RedisCredentials(
                    configuration.getString("redis-manager." + string + ".ip"),
                    configuration.getString("redis-manager." + string + ".password"),
                    configuration.getInt("redis-manager." + string + ".port"),
                    configuration.getInt("redis-manager." + string + ".database")
                )
        );

    }

    public RedisAccess getRedisAccess() {
        return redisAccess;
    }

    public void setRedisAccess(RedisAccess redisAccess) {
        this.redisAccess = redisAccess;
    }

    public static void initRedissons() {
        for (RedisManager redisManager : values()) {
            final Config config = new Config();

            RedisCredentials credentials = redisManager.getRedisAccess().getRedisCredentials();

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

    public static void closeRedissons() {
        for (RedisManager redisManager : values()) {
            redisManager.getRedisAccess().getRedissonClient().shutdown();
        }
    }
}
