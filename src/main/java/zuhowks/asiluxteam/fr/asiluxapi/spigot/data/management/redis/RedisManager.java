package zuhowks.asiluxteam.fr.asiluxapi.spigot.data.management.redis;

import org.bukkit.configuration.file.FileConfiguration;
import org.redisson.Redisson;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.AsiluxAPI;

public enum RedisManager {

    PLAYERS_ACCOUNT("players-account"),
    GAME_API("game-api");


    private RedisDatabase redisDatabase;

    RedisManager(String string) {

        FileConfiguration configuration = AsiluxAPI.INSTANCE.getConfig();
        this.redisDatabase = new RedisDatabase(new RedisAccess(
                new RedisCredentials(
                    configuration.getString("redis-manager." + string + ".ip"),
                    configuration.getString("redis-manager." + string + ".password"),
                    configuration.getInt("redis-manager." + string + ".port"),
                    configuration.getInt("redis-manager." + string + ".database")
                )
        ));

    }

    public RedisAccess getRedisAccess() {
        return redisDatabase.getRedisAccess();
    }

    public RedisDatabase getRedisDatabase() {
        return redisDatabase;
    }

    public static void initRedissons() {
        for (RedisManager redisManager : values()) {
            redisManager.getRedisDatabase().initRedisson();
        }
    }

    public static void closeRedissons() {
        for (RedisManager redisManager : values()) {
            redisManager.getRedisDatabase().closeRedisson();
        }
    }
}
