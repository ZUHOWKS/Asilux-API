package zuhowks.asiluxteam.fr.asiluxapi.spigot.asilux.command;

import org.apache.commons.lang.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.junit.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import zuhowks.asiluxteam.fr.asiluxapi.commons.player.Account;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.AsiluxAPI;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.data.management.redis.RedisAccess;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.data.management.redis.RedisManager;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class BankCommand implements CommandExecutor, TabCompleter {

    private String prefix = ChatColor.GREEN + "" + ChatColor.BOLD + "Asilux" + ChatColor.BLUE + "" + ChatColor.BOLD + " >>> ";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && AsiluxAPI.INSTANCE.getAsiluxEconomy().isEnable()) {
            Player p = (Player) sender;

            final RedisAccess redisAccessAccount = RedisManager.PLAYERS_ACCOUNT.getRedisAccess();
            final RedissonClient redissonClientAccount = redisAccessAccount.getRedissonClient();

            final RBucket<Account> pAccountRBucket = redissonClientAccount.getBucket("account:" + p.getUniqueId());

            Account pAccount = pAccountRBucket.get();
            YamlConfiguration langYMl = YamlConfiguration.loadConfiguration(new File("lang.yml"));

            p.sendMessage(prefix + langYMl.getString("asilux.unknown.command." + pAccount.getLang()));
            if (args.length == 0) {
                List<String> bankInfo = (List<String>) langYMl.getList("bank.info." + pAccount.getLang());
                p.sendMessage(prefix + bankInfo.get(0) + "\n   " +
                        bankInfo.get(1) + " §b" + p.getName() + "\n   §e" +
                        (pAccount.getCoins() <= 1 ? AsiluxAPI.INSTANCE.getAsiluxEconomy().getNameSingular() : AsiluxAPI.INSTANCE.getAsiluxEconomy().getNamePlural()) + ": §b" + pAccount.getCoins() + AsiluxAPI.INSTANCE.getAsiluxEconomy().getSymbol()
                );
            } else {
                if (args[0].equals("help")) {
                    //TODO: Player Help
                    p.sendMessage(prefix + " §eBank Help:");

                    //TODO: Admin Help
                    if (p.hasPermission("bank.admin")) {

                    }

                } else if (args[0].equals("info") && args.length == 1) {
                    List<String> bankInfo = (List<String>) langYMl.getList("bank.info." + pAccount.getLang());
                    p.sendMessage(prefix + bankInfo.get(0) + "\n   " +
                            bankInfo.get(1) + " §b" + p.getName() + "\n   §e" +
                            (pAccount.getCoins() <= 1 ? AsiluxAPI.INSTANCE.getAsiluxEconomy().getNameSingular() : AsiluxAPI.INSTANCE.getAsiluxEconomy().getNamePlural()) + ": §b" + pAccount.getCoins() + AsiluxAPI.INSTANCE.getAsiluxEconomy().getSymbol()
                    );
                } else if (args[0].equals("pay")) {
                    if (args.length == 3) {
                        int pay = Integer.parseInt(args[2]);
                        if (pAccount.getCoins() >= pay) {
                            Player pPayed = Bukkit.getPlayer(args[1]);
                            if (pPayed != null) {
                                final RBucket<Account> pPayedAccountRBucket = redissonClientAccount.getBucket("account:" + pPayed.getUniqueId());
                                Account pPayedAccount = pPayedAccountRBucket.get();
                                if (pPayedAccount != null) {
                                    pPayedAccount.setCoins(pPayedAccount.getCoins() + pay);
                                    pAccount.setCoins(pAccount.getCoins() - pay);

                                    pPayedAccountRBucket.set(pPayedAccount);
                                    pAccountRBucket.set(pAccount);
                                    //TODO: Send success message of the operation to the player & the payed player
                                } else {
                                    p.sendMessage(prefix + langYMl.getList("asilux.unknown.player." + pAccount.getLang()));
                                }
                            } else {
                                OfflinePlayer pOffPayed = Bukkit.getOfflinePlayer(args[1]);
                                if (pOffPayed != null) {
                                    final RBucket<Account> pPayedAccountRBucket = redissonClientAccount.getBucket("account:" + pOffPayed.getUniqueId());
                                    Account pPayedAccount = pPayedAccountRBucket.get();
                                    if (pPayedAccount != null) {
                                        pPayedAccount.setCoins(pPayedAccount.getCoins() + pay);
                                        pAccount.setCoins(pAccount.getCoins() - pay);

                                        pPayedAccountRBucket.set(pPayedAccount);
                                        pAccountRBucket.set(pAccount);
                                        //TODO: Send success message of the operation to the player
                                    }
                                } else {
                                    p.sendMessage(prefix + langYMl.getList("asilux.unknown.player." + pAccount.getLang()));
                                }
                            }
                        } else {

                        }
                    } else {

                    }
                } else if (p.hasPermission("bank.admin")) {
                    if (args[0].equals("set")) {
                        if (args.length == 3) {
                            int pay = Integer.parseInt(args[2]);
                            Player pPayed = Bukkit.getPlayer(args[1]);
                            if (pPayed != null) {
                                final RBucket<Account> pPayedAccountRBucket = redissonClientAccount.getBucket("account:" + pPayed.getUniqueId());
                                Account pPayedAccount = pPayedAccountRBucket.get();
                                if (pPayedAccount != null) {
                                    pPayedAccount.setCoins(pay);

                                    pPayedAccountRBucket.set(pPayedAccount);
                                    //TODO: Send success message of the operation to the player & the payed player
                                } else {
                                    p.sendMessage(prefix + langYMl.getList("asilux.unknown.player." + pAccount.getLang()));
                                }
                            } else {
                                OfflinePlayer pOffPayed = Bukkit.getOfflinePlayer(args[1]);
                                if (pOffPayed != null) {
                                    final RBucket<Account> pPayedAccountRBucket = redissonClientAccount.getBucket("account:" + pOffPayed.getUniqueId());
                                    Account pPayedAccount = pPayedAccountRBucket.get();
                                    if (pPayedAccount != null) {
                                        pPayedAccount.setCoins(pay);

                                        pPayedAccountRBucket.set(pPayedAccount);
                                        //TODO: Send success message of the operation to the player
                                    }
                                } else {
                                    p.sendMessage(prefix + langYMl.getList("asilux.unknown.player." + pAccount.getLang()));
                                }
                            }
                        } else {
                            //TODO: Give usage of command
                        }

                    } else if (args[0].equals("reset")) {
                        if (args.length == 2) {
                            Player pPayed = Bukkit.getPlayer(args[1]);
                            if (pPayed != null) {
                                final RBucket<Account> pPayedAccountRBucket = redissonClientAccount.getBucket("account:" + pPayed.getUniqueId());
                                Account pPayedAccount = pPayedAccountRBucket.get();
                                if (pPayedAccount != null) {
                                    pPayedAccount.setCoins(0);

                                    pPayedAccountRBucket.set(pPayedAccount);
                                    //TODO: Send success message of the operation to the player & the payed player
                                } else {
                                    p.sendMessage(prefix + langYMl.getList("asilux.unknown.player." + pAccount.getLang()));
                                }
                            } else {
                                OfflinePlayer pOffPayed = Bukkit.getOfflinePlayer(args[1]);
                                if (pOffPayed != null) {
                                    final RBucket<Account> pPayedAccountRBucket = redissonClientAccount.getBucket("account:" + pOffPayed.getUniqueId());
                                    Account pPayedAccount = pPayedAccountRBucket.get();
                                    if (pPayedAccount != null) {
                                        pPayedAccount.setCoins(0);

                                        pPayedAccountRBucket.set(pPayedAccount);
                                        //TODO: Send success message of the operation to the player
                                    }
                                } else {
                                    p.sendMessage(prefix + langYMl.getList("asilux.unknown.player." + pAccount.getLang()));
                                }
                            }
                        } else {
                            //TODO: Give usage of command
                        }
                    } else {
                        //TODO: Give usage of help command
                    }
                } else {
                    //TODO: Give usage of help command
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
