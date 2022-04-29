package zuhowks.asiluxteam.fr.asiluxapi.bc.data.management.redis;

import org.redisson.api.RedissonClient;
import zuhowks.asiluxteam.fr.asiluxapi.bc.data.management.mysql.DatabaseManager;

public class RedisAccess {
    private RedissonClient redissonClient;
    private RedisCredentials redisCredentials;

    public RedisAccess(RedisCredentials redisCredentials) {
        this.redisCredentials = redisCredentials;
    }

    public void setRedissonClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public RedisCredentials getRedisCredentials() {
        return redisCredentials;
    }

    public RedissonClient getRedissonClient() {
        return redissonClient;
    }
}
