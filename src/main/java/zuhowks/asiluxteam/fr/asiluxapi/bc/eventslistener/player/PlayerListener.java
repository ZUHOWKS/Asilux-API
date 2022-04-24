package zuhowks.asiluxteam.fr.asiluxapi.bc.eventslistener.player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.protocol.packet.Chat;
import org.redisson.api.RBucket;
import zuhowks.asiluxteam.fr.asiluxapi.bc.AsiluxAPI;
import zuhowks.asiluxteam.fr.asiluxapi.bc.core.AccountProvider;
import zuhowks.asiluxteam.fr.asiluxapi.bc.data.exceptions.AccountNotFoundException;
import zuhowks.asiluxteam.fr.asiluxapi.bc.data.management.redis.RedisManager;
import zuhowks.asiluxteam.fr.asiluxapi.commons.player.Account;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        ProxiedPlayer p = event.getPlayer();
        AsiluxAPI.INSTANCE.getProxy().getScheduler().runAsync(AsiluxAPI.INSTANCE, () -> {
            try {
                final AccountProvider accountProvider = new AccountProvider(p);
                final Account account = accountProvider.getAccount();

                accountProvider.sendAccountToRedis(account);
            } catch (AccountNotFoundException e) {
                System.err.println(e.getMessage());
                p.disconnect(new TextComponent(ChatColor.RED + "Error! Account don't find. Retry later."));
            }
        });
    }

    @EventHandler
    public void onPlayerLeave(PlayerDisconnectEvent event) {
        final ProxiedPlayer p = event.getPlayer();
        AsiluxAPI.INSTANCE.getProxy().getScheduler().runAsync(AsiluxAPI.INSTANCE, () -> {

            final RBucket<Account> accountRBucket = RedisManager.PLAYERS_ACCOUNT.getRedisAccess().getRedissonClient().getBucket("account:" + p.getUniqueId().toString());
            final Account account = accountRBucket.get();

            try {
                final PreparedStatement ps = RedisManager.PLAYERS_ACCOUNT.getDatabaseManager().getDatabaseAccess().getConnection().prepareStatement("UPDATE `players_account` SET id=" + account.getId() + ", uuid=" + account.getUuid() + ", rank=" + account.getRank(), Integer.parseInt(", coins=" + account.getCoins() + ", level=" + account.getLevel() + ", xp=" + account.getXp() + ", mmr=" + account.getMMR() + " WHERE id=" + account.getId()));
                ps.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }
}
