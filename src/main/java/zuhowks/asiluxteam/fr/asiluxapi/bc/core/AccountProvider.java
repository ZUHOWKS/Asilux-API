package zuhowks.asiluxteam.fr.asiluxapi.bc.core;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import zuhowks.asiluxteam.fr.asiluxapi.bc.AsiluxAPI;
import zuhowks.asiluxteam.fr.asiluxapi.bc.data.exceptions.AccountNotFoundException;
import zuhowks.asiluxteam.fr.asiluxapi.bc.data.management.mysql.DatabaseManager;
import zuhowks.asiluxteam.fr.asiluxapi.bc.data.management.redis.RedisAccess;
import zuhowks.asiluxteam.fr.asiluxapi.bc.data.management.redis.RedisManager;
import zuhowks.asiluxteam.fr.asiluxapi.commons.player.Account;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class AccountProvider {
    public static final String REDIS_KEY = "account:";
    public static final Account DEFAULT_ACCOUNT = new Account(0, UUID.randomUUID(), "Champion", 100, 1, 0, 0, "en");

    private RedisAccess redisAccess;
    private ProxiedPlayer player;

    public AccountProvider(ProxiedPlayer player) {
        this.player = player;
        this.redisAccess = RedisManager.PLAYERS_ACCOUNT.getRedisAccess();
    }

    public Account getAccount() throws AccountNotFoundException {
        Account account = getAccountFromRedis();
        if (account == null) {

            account = getAccountFromDatabase();
            sendAccountToRedis(account);
        }
        player.sendMessage(new TextComponent(ChatColor.AQUA + "Your account as been found successfully ! Welcome on " + ChatColor.YELLOW + "Asilux " + ChatColor.AQUA + "!"));
        return account;
    }

    public void sendAccountToRedis (Account account) {
        final RedissonClient redissonClient = redisAccess.getRedissonClient();
        final String key = REDIS_KEY + this.player.getUniqueId().toString();
        final RBucket<Account> accountRBucket = redissonClient.getBucket(key);

        accountRBucket.set(account);
    }

    private Account getAccountFromRedis() {
        final RedissonClient redissonClient = redisAccess.getRedissonClient();
        final String key = REDIS_KEY + this.player.getUniqueId().toString();
        final RBucket<Account> accountRBucket = redissonClient.getBucket(key);

        return accountRBucket.get();
    }

    private Account getAccountFromDatabase() throws AccountNotFoundException {
        final Connection connection = DatabaseManager.PLAYERS_ACCOUNT.getDatabaseAccess().getConnection();
        final UUID uuid = player.getUniqueId();
        Account account = null;

        if (connection != null) {
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM players_account WHERE uuid = ?");
                ps.setString(1,uuid.toString());
                ps.executeQuery();
                final ResultSet rs = ps.getResultSet();

                if (rs.next()) {
                    player.sendMessage(new TextComponent(ChatColor.AQUA + "Your account as been found successfully ! Welcome on " + ChatColor.YELLOW + "Asilux " + ChatColor.AQUA + "!"));

                    final int id = rs.getInt("id");
                    final String rank = rs.getString("rank");
                    final int coins = rs.getInt("coins");
                    final int level = rs.getInt("level");
                    final int xp = rs.getInt("xp");
                    final int mmr = rs.getInt("mmr");
                    final String lang = rs.getString("lang");
                    account = new Account(id, uuid, rank, coins, level, xp, mmr, lang);

                } else {
                    account = registerAccount(uuid, connection);
                }
                ps.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return account;
    }

    public Account registerAccount(UUID uuid, Connection connection) throws SQLException {
        final Account account = DEFAULT_ACCOUNT.clone();
        final PreparedStatement ps = connection.prepareStatement("INSERT INTO players_account (uuid, rank, coins, level, xp) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, uuid.toString());
        ps.setString(2, account.getRank());
        ps.setInt(3, account.getCoins());
        ps.setInt(4, account.getLevel());
        ps.setInt(5, account.getXp());
        ps.setInt(6, account.getMMR());
        ps.setString(7, account.getLang());

        final int row = ps.executeUpdate();

        final ResultSet rs = ps.getGeneratedKeys();

        if (row > 0 && rs.next()) {
            final int id = rs.getInt(1);

            account.setId(id);
            account.setUuid(uuid);

            player.sendMessage(new TextComponent(ChatColor.AQUA + "Account creation successfully ! Welcome on " + ChatColor.YELLOW + "Asilux " + ChatColor.AQUA + "!"));
            sendAccountToRedis(account);
            return account;
            //AsiluxAPI.INSTANCE.accounts.add(account);
        }
        return null;
    }
}
