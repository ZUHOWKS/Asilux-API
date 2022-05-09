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
import java.util.*;

public class BankCommand implements CommandExecutor, TabCompleter {

    private String prefix = ChatColor.GREEN + "" + ChatColor.BOLD + "Asilux" + ChatColor.BLUE + "" + ChatColor.BOLD + " >>> ";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && AsiluxAPI.INSTANCE.getAsiluxEconomy().isEnable()) {
            Player p = (Player) sender;
            try {
                final AccountProvider pAP = new AccountProvider(p);
                final Account pAccount = pAP.getAccount();


                YamlConfiguration langYMl = YamlConfiguration.loadConfiguration(new File("lang.yml"));

                p.sendMessage(prefix + langYMl.getString("asilux.unknown.command." + pAccount.getLang()));
                if (args.length == 0) {
                    p.sendMessage(prefix + String.format(langYMl.getString("bank.info." + pAccount.getLang()), p.getName()) +
                            (pAccount.getCoins() <= 1 ? AsiluxAPI.INSTANCE.getAsiluxEconomy().getNameSingular() : AsiluxAPI.INSTANCE.getAsiluxEconomy().getNamePlural()) + ": §b" + pAccount.getCoins() + AsiluxAPI.INSTANCE.getAsiluxEconomy().getSymbol()
                    );
                } else {
                    if (args[0].equals("help")) {


                        if (p.hasPermission("bank.admin")) {
                            p.sendMessage(prefix + " §eBank Help:\n" +
                                    "  - /bank info | Get information of your bank account.\n" +
                                    "  - /bank pay <player> <integer> | Transit an amount to the player bank account.\n" +
                                    "  - /bank set <player> <integer> | Realise an operation to set the amount.\n" +
                                    "  - /bank reset <player> | Reset the player bank account.\n"
                            );
                        } else {
                            p.sendMessage(prefix + " §eBank Help:\n" +
                                    "  - /bank info | Get information of your bank account.\n" +
                                    "  - /bank pay <player> <integer> | Transit an amount to a player bank account.\n"
                            );
                        }

                    } else if (args[0].equals("info") && args.length == 1) {
                        List<String> bankInfo = langYMl.getStringList("bank.info." + pAccount.getLang());

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
                                    if (!pPayed.equals(p)) {
                                        final AccountProvider pPayedAP = new AccountProvider(pPayed);
                                        final Account pPayedAccount = pPayedAP.getAccount();
                                        if (pPayedAccount != null) {
                                            pPayedAccount.setCoins(pPayedAccount.getCoins() + pay);
                                            pAccount.setCoins(pAccount.getCoins() - pay);

                                            pPayedAP.sendAccount(pPayedAccount);
                                            pAP.sendAccount(pAccount);
                                            p.sendMessage(prefix + String.format(langYMl.getString("bank.transaction.pay." + pAccount.getLang()), pPayed.getName(), pay, AsiluxAPI.INSTANCE.getAsiluxEconomy().getNamePlural()));
                                        } else {
                                            p.sendMessage(prefix + langYMl.getString("asilux.unknown.player." + pAccount.getLang()));
                                        }
                                    } else {
                                        p.sendMessage(prefix + ChatColor.RED + "You can't realise a transaction with yourself.");
                                    }
                                } else {
                                    OfflinePlayer pOffPayed = Bukkit.getOfflinePlayer(args[1]);
                                    if (pOffPayed != null) {
                                        final AccountProvider pPayedAP = new AccountProvider(pPayed);
                                        final Account pPayedAccount = pPayedAP.getAccount();
                                        if (pPayedAccount != null) {
                                            pPayedAccount.setCoins(pPayedAccount.getCoins() + pay);
                                            pAccount.setCoins(pAccount.getCoins() - pay);

                                            pPayedAP.sendAccount(pPayedAccount);
                                            pAP.sendAccount(pAccount);
                                            p.sendMessage(prefix + String.format(langYMl.getString("asilux.unknown.player." + pAccount.getLang()), pPayed.getName(), pay, AsiluxAPI.INSTANCE.getAsiluxEconomy().getNamePlural()));
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
                                final Player pPayed = Bukkit.getPlayer(args[1]);
                                if (pPayed != null) {
                                    final AccountProvider pPayedAP = new AccountProvider(pPayed);
                                    final Account pPayedAccount = pPayedAP.getAccount();
                                    if (pPayedAccount != null) {
                                        pPayedAccount.setCoins(pay);

                                        pPayedAP.sendAccount(pPayedAccount);
                                        p.sendMessage(prefix + String.format(langYMl.getString("bank.transaction.set." + pAccount.getLang()), pPayed.getName(), pay, AsiluxAPI.INSTANCE.getAsiluxEconomy().getNamePlural()));
                                    } else {
                                        p.sendMessage(prefix + langYMl.getList("asilux.unknown.player." + pAccount.getLang()));
                                    }
                                } else {
                                    final OfflinePlayer pOffPayed = Bukkit.getOfflinePlayer(args[1]);
                                    if (pOffPayed != null) {
                                        final AccountProvider pPayedAP = new AccountProvider(pPayed);
                                        final Account pPayedAccount = pPayedAP.getAccount();

                                        if (pPayedAccount != null) {
                                            pPayedAccount.setCoins(pay);

                                            pPayedAP.sendAccount(pPayedAccount);
                                            p.sendMessage(prefix + ChatColor.RED + "Error! Try yo use the correct command syntax: " + ChatColor.AQUA + "/bank set <player> <integer>");
                                        }
                                    } else {
                                        p.sendMessage(prefix + langYMl.getList("asilux.unknown.player." + pAccount.getLang()));
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
                                    final Account pPayedAccount = pPayedAP.getAccount();
                                    if (pPayedAccount != null) {
                                        pPayedAccount.setCoins(0);

                                        pPayedAP.sendAccount(pPayedAccount);
                                        p.sendMessage(prefix + String.format(langYMl.getString("bank.transaction.set." + pAccount.getLang()), pPayed.getName(), 0, AsiluxAPI.INSTANCE.getAsiluxEconomy().getNamePlural()));
                                    } else {
                                        p.sendMessage(prefix + langYMl.getList("asilux.unknown.player." + pAccount.getLang()));
                                    }
                                } else {
                                    final OfflinePlayer pOffPayed = Bukkit.getOfflinePlayer(args[1]);
                                    if (pOffPayed != null) {
                                        final AccountProvider pPayedAP = new AccountProvider(pPayed);
                                        final Account pPayedAccount = pPayedAP.getAccount();
                                        if (pPayedAccount != null) {
                                            pPayedAccount.setCoins(0);

                                            pPayedAP.sendAccount(pPayedAccount);
                                            p.sendMessage(prefix + String.format(langYMl.getString("bank.transaction.set." + pAccount.getLang()), pOffPayed.getName(), 0, AsiluxAPI.INSTANCE.getAsiluxEconomy().getNamePlural()));
                                        }
                                    } else {
                                        p.sendMessage(prefix + langYMl.getList("asilux.unknown.player." + pAccount.getLang()));
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
            } catch (AccountNotFoundException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player) {
            final List<String> result = new ArrayList<String>();

            if (args.length == 1) {
                StringUtil.copyPartialMatches(args[0], Arrays.asList("info", "pay", "reset", "set", "help"), result);
                Collections.sort(result);

            } else if (args.length == 2 && (args[0].equalsIgnoreCase("pay") || args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("set"))) {
                final List<String> args_1 = new ArrayList<String>();
                for (Player player : AsiluxAPI.INSTANCE.getServer().getOnlinePlayers()) {
                    args_1.add(player.getName());
                }

                Collections.sort(args_1);
                StringUtil.copyPartialMatches(args[1], args_1, result);
            }
            return result;
        }
        return null;
    }
}
