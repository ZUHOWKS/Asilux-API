package zuhowks.asiluxteam.fr.asiluxapi.spigot.asilux.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import zuhowks.asiluxteam.fr.asiluxapi.commons.player.Account;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.data.management.redis.RedisAccess;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.data.management.redis.RedisManager;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class BankCommand implements CommandExecutor, TabCompleter {

    private String prefix = ChatColor.GREEN + "" + ChatColor.BOLD + "Asilux" + ChatColor.BLUE + "" + ChatColor.BOLD + " >>> ";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            Account pAccount = getAccount(p.getUniqueId());
            YamlConfiguration langYMl = YamlConfiguration.loadConfiguration(new File("lang.yml"));
            p.sendMessage(prefix + langYMl.getString("unknown-command." + pAccount.getLang()));
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    private Account getAccount(UUID uuid) {
        final RedisAccess redisAccessAccount = RedisManager.PLAYERS_ACCOUNT.getRedisAccess();
        final RedissonClient redissonClientAccount = redisAccessAccount.getRedissonClient();
        final RBucket<Account> accountRBucket = redissonClientAccount.getBucket("account:" + uuid.toString());
        return accountRBucket.get();
    }
}
