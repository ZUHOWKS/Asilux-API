package zuhowks.asiluxteam.fr.asiluxapi.spigot.data.management.redis;

import org.redisson.Redisson;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

public class RedisDatabase {

    private RedisAccess redisAccess;

    public RedisDatabase(RedisAccess redisAccess) {
        this.redisAccess = redisAccess;
    }

    public RedisAccess getRedisAccess() {
        return redisAccess;
    }

    public void initRedisson() {
        final Config config = new Config();

        RedisCredentials credentials = this.getRedisAccess().getRedisCredentials();

        config.setCodec(new JsonJacksonCodec());
        config.setThreads(2);
        config.setNettyThreads(2);
        config.useSingleServer()
                .setAddress(credentials.toRedisURL())
                .setPassword(credentials.getPassword())
                .setDatabase(credentials.getDatabase())
                .setClientName(credentials.getClientName());
        this.redisAccess.setRedissonClient(Redisson.create(config));
    }

    public void closeRedisson() {
        this.getRedisAccess().getRedissonClient().shutdown();
    }
}
