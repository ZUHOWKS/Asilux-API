package zuhowks.asiluxteam.fr.asiluxapi.spigot.data.management.redis;

import org.redisson.api.RedissonClient;

public class RedisAccess {
    private RedissonClient redissonClient;
    private final RedisCredentials redisCredentials;

    public RedisAccess(RedisCredentials redisCredentials) {
        this.redisCredentials = redisCredentials;
    }

    public void setRedissonClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public RedissonClient getRedissonClient() {
        return redissonClient;
    }

    public RedisCredentials getRedisCredentials() {
        return redisCredentials;
    }
}
