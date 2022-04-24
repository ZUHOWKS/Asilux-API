package zuhowks.asiluxteam.fr.asiluxapi.commons.game;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import zuhowks.asiluxteam.fr.asiluxapi.bc.data.management.redis.RedisAccess;
import zuhowks.asiluxteam.fr.asiluxapi.bc.data.management.redis.RedisManager;

import java.util.ArrayList;
import java.util.List;

public class GamesRegistry {
    private static final String REDIS_KEY = "gameAPI";

    private List<String> gameNames;
    private List<String> itemNames;
    private List<String> commands;
    private List<String> materialNames;
    private List<String[]> descs;

    public GamesRegistry() {
    }

    public GamesRegistry(String REDIS_KEY) {
        this.gameNames = new ArrayList<>();
        this.itemNames = new ArrayList<>();
        this.commands = new ArrayList<>();
        this.materialNames = new ArrayList<>();
        this.descs = new ArrayList<>();
    }

    public void registerGame(String gameName,  String command, String itemName, String materialName, String... desc) {
        if (!gameNames.contains(gameName) && (!itemNames.contains(itemName))) {
            this.gameNames.add(gameName);
            this.commands.add(command);
            this.itemNames.add(itemName);
            this.materialNames.add(materialName);
            this.descs.add(desc);
            final RedisAccess redisAccess = RedisManager.GAME_API.getRedisAccess();
            final RedissonClient redissonClient = redisAccess.getRedissonClient();
            final RBucket<GamesRegistry> gamesRgeistryBucket = redissonClient.getBucket(REDIS_KEY);

            gamesRgeistryBucket.set(this);
        }
    }

    public List<String> getGameNames() {
        return gameNames;
    }

    public List<String> getItemNames() {
        return itemNames;
    }

    public List<String> getCommands() {
        return commands;
    }

    public List<String> getMaterialNames() {
        return materialNames;
    }

    public List<String[]> getDescs() {
        return descs;
    }
}
