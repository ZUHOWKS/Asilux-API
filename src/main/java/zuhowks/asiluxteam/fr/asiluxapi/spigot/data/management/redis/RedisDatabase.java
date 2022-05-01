package zuhowks.asiluxteam.fr.asiluxapi.spigot.data.management.redis;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

public class RedisDatabase {

    private RedisAccess redisAccess;

    public RedisDatabase() {
    }

    public RedisDatabase(RedisAccess redisAccess) {
        this.redisAccess = redisAccess;
    }

    public boolean setupRedisDatabase(String ip, String password, int port, int database) {
        this.redisAccess = new RedisAccess(
                new RedisCredentials(
                        ip,
                        password,
                        port,
                        database
                )
        );

        this.initRedisson();
        return !this.getRedisAccess().getRedissonClient().isShutdown();
    }

    public Object getObjFromRedisDatabase(String key) {
        if (!this.getRedisAccess().getRedissonClient().isShutdown()) {
            final RedissonClient redissonClient = this.getRedisAccess().getRedissonClient();
            final RBucket<Object> accountRBucket = redissonClient.getBucket(key);
            return accountRBucket.get();
        }
        return null;
    }

    public boolean setObjInRedisDatabase(String key, Object objet) {
        if (!this.getRedisAccess().getRedissonClient().isShutdown() && objet != null) {
            final RedissonClient redissonClient = this.getRedisAccess().getRedissonClient();
            final RBucket<Object> accountRBucket = redissonClient.getBucket(key);
            accountRBucket.set(objet);
            return true;
        }
        return false;
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
