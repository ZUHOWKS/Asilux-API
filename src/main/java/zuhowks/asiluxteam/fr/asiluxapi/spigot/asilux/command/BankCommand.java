package zuhowks.asiluxteam.fr.asiluxapi.spigot.asilux.command;

import org.apache.commons.io.FileUtils;
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
import org.bukkit.util.FileUtil;
import org.bukkit.util.StringUtil;
import org.junit.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import zuhowks.asiluxteam.fr.asiluxapi.commons.player.Account;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.AsiluxAPI;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.core.AccountProvider;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.data.management.exceptions.AccountNotFoundException;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.data.management.redis.RedisAccess;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.data.management.redis.RedisManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

public class BankCommand implements CommandExecutor, TabCompleter {

    private String prefix = ChatColor.GREEN + "" + ChatColor.BOLD + "Asilux Bank" + ChatColor.AQUA + "" + ChatColor.BOLD + " >>> ";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && AsiluxAPI.INSTANCE.getAsiluxEconomy().isEnable()) {
            Player p = (Player) sender;
            try {
                final AccountProvider pAP = new AccountProvider(p);
                final Account pAccount = pAP.getAccount(true);



                YamlConfiguration langYMl = AsiluxAPI.INSTANCE.getLangYamlConfig();
                if (args.length == 0) {
                    p.sendMessage(prefix + String.format(langYMl.getString("bank.info." + pAccount.getLang()), p.getName()) + ChatColor.GREEN + " " +
                            (pAccount.getCoins() <= 1 ? AsiluxAPI.INSTANCE.getAsiluxEconomy().getNameSingular() : AsiluxAPI.INSTANCE.getAsiluxEconomy().getNamePlural()) + ": " + ChatColor.AQUA + pAccount.getCoins() + AsiluxAPI.INSTANCE.getAsiluxEconomy().getSymbol()
                    );
                } else {
                    if (args[0].equals("help")) {


                        if (p.hasPermission("bank.admin")) {
                            //Bank command help for admin
                            p.sendMessage(prefix + ChatColor.GREEN + " Bank Help:\n" +
                                    ChatColor.AQUA + "  - /bank info" + ChatColor.GRAY + " | " + ChatColor.GREEN + "Get information about your bank account.\n" +
                                    ChatColor.AQUA + "  - /bank pay <player> <integer>" + ChatColor.GRAY + " | " + ChatColor.GREEN + "Transit an amount to the player bank account.\n" +
                                    ChatColor.AQUA + "  - /bank set <player> <integer>" + ChatColor.GRAY + " | " + ChatColor.GREEN + "Realise an operation to set the amount.\n" +
                                    ChatColor.AQUA + "  - /bank reset <player>" + ChatColor.GRAY + " | " + ChatColor.GREEN + "Reset the player bank account.\n"
                            );
                        } else {
                            //Bank command help for player
                            p.sendMessage(prefix + ChatColor.GREEN + " Bank Help:\n" +
                                    ChatColor.AQUA + "  - /bank info" + ChatColor.GRAY + " | " + ChatColor.GREEN + "Get information of your bank account.\n" +
                                    ChatColor.AQUA + "  - /bank pay <player> <integer>" + ChatColor.GRAY + " | " + ChatColor.GREEN + "Transit an amount to a player bank account.\n"
                            );
                        }

                    } else if (args[0].equals("info") && args.length == 1) {
                        p.sendMessage(prefix + String.format(langYMl.getString("bank.info." + pAccount.getLang()), p.getName()) + " " +
                                (pAccount.getCoins() <= 1 ? AsiluxAPI.INSTANCE.getAsiluxEconomy().getNameSingular() : AsiluxAPI.INSTANCE.getAsiluxEconomy().getNamePlural()) + ": " + ChatColor.BLUE + pAccount.getCoins() + AsiluxAPI.INSTANCE.getAsiluxEconomy().getSymbol()
                        );
                    } else if (args[0].equals("pay")) {
                        if (args.length == 3) {
                            int pay = Integer.parseInt(args[2]);
                            if (pAccount.getCoins() >= pay) {
                                final Player pPayed = Bukkit.getPlayer(args[1]);
                                if (pPayed != null) {
                                    if (!pPayed.equals(p)) {
                                        final AccountProvider pPayedAP = new AccountProvider(pPayed);
                                        final Account pPayedAccount = pPayedAP.getAccount(true);
                                        if (pPayedAccount != null) {
                                            pPayedAccount.setCoins(pPayedAccount.getCoins() + pay);
                                            pAccount.setCoins(pAccount.getCoins() - pay);

                                            pPayedAP.sendAccount(pPayedAccount);
                                            pAP.sendAccount(pAccount);

                                            p.sendMessage(prefix + String.format(langYMl.getString("bank.transaction.pay." + pAccount.getLang()), pPayed.getName(), pay, (pay <= 1 ? AsiluxAPI.INSTANCE.getAsiluxEconomy().getNameSingular() : AsiluxAPI.INSTANCE.getAsiluxEconomy().getNamePlural())));
                                            pPayed.sendMessage(prefix + String.format(langYMl.getString("bank.transaction.receive.pay." + pPayedAccount.getLang()), p.getName(), pay, (pay <= 1 ? AsiluxAPI.INSTANCE.getAsiluxEconomy().getNameSingular() : AsiluxAPI.INSTANCE.getAsiluxEconomy().getNamePlural())));
                                        } else {
                                            p.sendMessage(prefix + langYMl.getString("asilux.unknown.player." + pAccount.getLang()));
                                        }
                                    } else {
                                        p.sendMessage(prefix + ChatColor.RED + "You can't realise a transaction with yourself.");
                                    }
                                } else {
                                    final Player pOffPayed = Bukkit.getOfflinePlayer(args[1]).getPlayer();
                                    if (pOffPayed != null) {
                                        final AccountProvider pPayedAP = new AccountProvider(pOffPayed);
                                        final Account pPayedAccount = pPayedAP.getAccount(false);
                                        if (pPayedAccount != null) {
                                            pPayedAccount.setCoins(pPayedAccount.getCoins() + pay);
                                            pAccount.setCoins(pAccount.getCoins() - pay);

                                            pPayedAP.sendAccount(pPayedAccount);
                                            pAP.sendAccount(pAccount);

                                            p.sendMessage(prefix + String.format(langYMl.getString("asilux.unknown.player." + pAccount.getLang()), pOffPayed.getName(), pay, (pay <= 1 ? AsiluxAPI.INSTANCE.getAsiluxEconomy().getNameSingular() : AsiluxAPI.INSTANCE.getAsiluxEconomy().getNamePlural())));
                                            pOffPayed.sendMessage(prefix + String.format(langYMl.getString("bank.transaction.receive.pay." + pPayedAccount.getLang()), p.getName(), pay, (pay <= 1 ? AsiluxAPI.INSTANCE.getAsiluxEconomy().getNameSingular() : AsiluxAPI.INSTANCE.getAsiluxEconomy().getNamePlural())));
                                        }
                                    } else {
                                        p.sendMessage(prefix + langYMl.getString("asilux.unknown.player." + pAccount.getLang()));
                                    }
                                }
                            } else {
                                p.sendMessage(prefix + ChatColor.RED + "Unable to complete the transaction. You do not have enough money to pay an amount of " + ChatColor.AQUA + pay + (pay <= 1 ? AsiluxAPI.INSTANCE.getAsiluxEconomy().getNameSingular() : AsiluxAPI.INSTANCE.getAsiluxEconomy().getNamePlural()) + ChatColor.RED + ".");
                            }
                        } else {
                            p.sendMessage(prefix + langYMl.getString("asilux.unknown.command." + pAccount.getLang()));
                        }
                    } else if (p.hasPermission("bank.admin")) {
                        if (args[0].equals("set")) {
                            if (args.length == 3) {
                                int pay = Integer.parseInt(args[2]);
                                final Player pPayed = Bukkit.getPlayer(args[1]);
                                if (pPayed != null) {
                                    final AccountProvider pPayedAP = new AccountProvider(pPayed);
                                    final Account pPayedAccount = pPayedAP.getAccount(true);
                                    if (pPayedAccount != null) {
                                        pPayedAccount.setCoins(pay);

                                        pPayedAP.sendAccount(pPayedAccount);

                                        p.sendMessage(prefix + String.format(langYMl.getString("bank.transaction.set." + pAccount.getLang()), pPayed.getName(), pay, AsiluxAPI.INSTANCE.getAsiluxEconomy().getNamePlural()));
                                        pPayed.sendMessage(prefix + String.format(langYMl.getString("bank.transaction.receive.fixe." + pPayedAccount.getLang()), pay, AsiluxAPI.INSTANCE.getAsiluxEconomy().getNamePlural()));
                                    } else {
                                        p.sendMessage(prefix + langYMl.getString("asilux.unknown.player." + pAccount.getLang()));
                                    }
                                } else {
                                    final Player pOffPayed = Bukkit.getOfflinePlayer(args[1]).getPlayer();
                                    if (pOffPayed != null) {
                                        final AccountProvider pPayedAP = new AccountProvider(pOffPayed);
                                        final Account pPayedAccount = pPayedAP.getAccount(false);

                                        if (pPayedAccount != null) {
                                            pPayedAccount.setCoins(pay);

                                            pPayedAP.sendAccount(pPayedAccount);
                                            p.sendMessage(prefix + String.format(langYMl.getString("bank.transaction.set." + pAccount.getLang()), pOffPayed.getName(), pay, AsiluxAPI.INSTANCE.getAsiluxEconomy().getNamePlural()));
                                            pOffPayed.sendMessage(prefix + String.format(langYMl.getString("bank.transaction.receive.fixe." + pPayedAccount.getLang()), pay, AsiluxAPI.INSTANCE.getAsiluxEconomy().getNamePlural()));
                                        }
                                    } else {
                                        p.sendMessage(prefix + langYMl.getString("asilux.unknown.player." + pAccount.getLang()));
                                    }
                                }
                            } else {
                                p.sendMessage(prefix + ChatColor.RED + "Error! Try yo use the correct command syntax: " + ChatColor.AQUA + "/bank set <player> <integer>");
                            }

                        } else if (args[0].equals("reset")) {
                            if (args.length == 2) {
                                final Player pPayed = Bukkit.getPlayer(args[1]);
                                if (pPayed != null) {
                                    final AccountProvider pPayedAP = new AccountProvider(pPayed);
                                    final Account pPayedAccount = pPayedAP.getAccount(true);
                                    if (pPayedAccount != null) {
                                        pPayedAccount.setCoins(0);

                                        pPayedAP.sendAccount(pPayedAccount);
                                        p.sendMessage(prefix + String.format(langYMl.getString("bank.transaction.set." + pAccount.getLang()), pPayed.getName(), 0, AsiluxAPI.INSTANCE.getAsiluxEconomy().getNamePlural()));
                                        pPayed.sendMessage(prefix + String.format(langYMl.getString("bank.transaction.receive.fixe." + pPayedAccount.getLang()), 0, AsiluxAPI.INSTANCE.getAsiluxEconomy().getNamePlural()));
                                    } else {
                                        p.sendMessage(prefix + langYMl.getString("asilux.unknown.player." + pAccount.getLang()));
                                    }
                                } else {
                                    final Player pOffPayed = Bukkit.getOfflinePlayer(args[1]).getPlayer();
                                    if (pOffPayed != null) {
                                        final AccountProvider pPayedAP = new AccountProvider(pOffPayed);
                                        final Account pPayedAccount = pPayedAP.getAccount(false);
                                        if (pPayedAccount != null) {
                                            pPayedAccount.setCoins(0);

                                            pPayedAP.sendAccount(pPayedAccount);
                                            p.sendMessage(prefix + String.format(langYMl.getString("bank.transaction.set." + pAccount.getLang()), pOffPayed.getName(), 0, AsiluxAPI.INSTANCE.getAsiluxEconomy().getNamePlural()));
                                            pOffPayed.sendMessage(prefix + String.format(langYMl.getString("bank.transaction.receive.fixe." + pPayedAccount.getLang()), 0, AsiluxAPI.INSTANCE.getAsiluxEconomy().getNamePlural()));
                                        }
                                    } else {
                                        p.sendMessage(prefix + langYMl.getString("asilux.unknown.player." + pAccount.getLang()));
                                    }
                                }
                            } else {
                                p.sendMessage(prefix + langYMl.getString("asilux.unknown.command." + pAccount.getLang()));
                            }
                        } else {
                            p.sendMessage(prefix + langYMl.getString("asilux.unknown.command." + pAccount.getLang()));
                        }
                    } else {
                        p.sendMessage(prefix + langYMl.getString("asilux.unknown.command." + pAccount.getLang()));
                    }
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player) {
            final Player p = (Player) sender;
            final List<String> result = new ArrayList<String>();

            if (args.length == 1) {
                if (p.hasPermission("bank.admin")) {
                    StringUtil.copyPartialMatches(args[0], Arrays.asList("info", "pay", "reset", "set", "help"), result);
                } else {
                    StringUtil.copyPartialMatches(args[0], Arrays.asList("info", "pay", "help"), result);
                }
                Collections.sort(result);

            } else if (args.length == 2 && (args[0].equalsIgnoreCase("pay") || args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("set"))) {
                final List<String> args_1 = new ArrayList<String>();
                for (Player player : AsiluxAPI.INSTANCE.getServer().getOnlinePlayers()) {
                    if (!player.getUniqueId().equals(p.getUniqueId())) {
                        args_1.add(player.getName());
                    }
                }

                Collections.sort(args_1);
                StringUtil.copyPartialMatches(args[1], args_1, result);
            }
            return result;
        }
        return null;
    }
}
