package zuhowks.asiluxteam.fr.asiluxapi.spigot.listeners.player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import zuhowks.asiluxteam.fr.asiluxapi.commons.game.GamesRegistry;
import zuhowks.asiluxteam.fr.asiluxapi.commons.player.Account;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.AsiluxAPI;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.asilux.game.GameAPI;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.data.management.redis.RedisAccess;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.data.management.redis.RedisManager;

import java.util.List;

public class PlayerJoinedListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        final Player p = event.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(AsiluxAPI.INSTANCE, () -> {
            final RedisAccess redisAccessAccount = RedisManager.PLAYERS_ACCOUNT.getRedisAccess();
            final RedissonClient redissonClientAccount = redisAccessAccount.getRedissonClient();
            final RBucket<Account> accountRBucket = redissonClientAccount.getBucket("account:" + p.getUniqueId().toString());
            final Account account = accountRBucket.get();

            if (!p.hasPlayedBefore()) {
                account.setXp(account.getXp() + 1000);
                accountRBucket.set(account);
            }

            final RedisAccess redisAccessGames = RedisManager.GAME_API.getRedisAccess();
            final RedissonClient redissonClientGames = redisAccessGames.getRedissonClient();
            final RBucket<GamesRegistry> gamesRegistryRBucket = redissonClientGames.getBucket("gameAPI");
            if (gamesRegistryRBucket.get() != null && !gamesRegistryRBucket.get().equals(AsiluxAPI.INSTANCE.getGamesRegistry())) {

                final GamesRegistry gamesRegistry = gamesRegistryRBucket.get();
                final List<String> gamesName = gamesRegistry.getGameNames();
                final List<String> command = gamesRegistry.getCommands();
                final List<String> itemNames = gamesRegistry.getItemNames();
                final List<String> materialNames = gamesRegistry.getMaterialNames();
                final List<String[]> descs = gamesRegistry.getDescriptions();

                for (int i = 0; i < gamesRegistry.getGameNames().size(); i++) {
                    AsiluxAPI.INSTANCE.getGameManager().addGame(new GameAPI(gamesName.get(i), command.get(i), itemNames.get(i), materialNames.get(i), descs.get(i)));
                }
            }
        });

        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("refreshGame");

        Bukkit.getScheduler().scheduleSyncDelayedTask(AsiluxAPI.INSTANCE, ()-> {
            p.sendPluginMessage(AsiluxAPI.INSTANCE, AsiluxAPI.INSTANCE.getMainChannel(), out.toByteArray());
        }, 20L);
    }
}
