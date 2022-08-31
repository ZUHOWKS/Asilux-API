package zuhowks.asiluxteam.fr.asiluxapi.spigot.core;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import zuhowks.asiluxteam.fr.asiluxapi.commons.player.Account;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.AsiluxAPI;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.data.management.exceptions.AccountNotFoundException;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.data.management.redis.RedisAccess;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.data.management.redis.RedisManager;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.data.management.sql.DatabaseManager;

import java.sql.*;
import java.util.Locale;
import java.util.UUID;

public class AccountProvider {
    public static final String REDIS_KEY = "account:";
    public static final Account DEFAULT_ACCOUNT = new Account(UUID.randomUUID(), "Champion", 100, 1, 0, 0, "en");

    private RedisAccess redisAccess;
    private Player player;

    public AccountProvider(Player player) {
        this.player = player;
        this.redisAccess = RedisManager.PLAYERS_ACCOUNT.getRedisAccess();
    }

    /**
     * Get account from Redis OR (if not exist) from SQL database.
     * <P><B>Note:</B> It's recommended to run this methode <code>Asynchronously</code> to avoid
     * a server hanging.
     * <P>
     * @return Return an <code>Account</code> of the Player.
     * @throws AccountNotFoundException
     */
    public Account getAccount(boolean withCreationIfNotExist) throws AccountNotFoundException {
        final Account accountFromRedis = getAccountFromRedis();
        if (accountFromRedis == null) {

            final Account accountFromDatabase = getAccountFromDatabase(withCreationIfNotExist);
            if (withCreationIfNotExist) {
                sendAccountToRedis(accountFromDatabase);
            }
            return accountFromDatabase;
        }
        return accountFromRedis;
    }

    /**
     * Send account to Redis & SQL database.
     * <P>
     * @param account Type: <code>Account</code> | Account of the player.
     */
    public void sendAccount(Account account) {
        sendAccountToRedis(account);
        sendAccountToSQL(account);
    }

    /**
     * Send account to SQL database.
     * <P>
     * @param account Type: <code>Account</code> | Account of the player.
     */
    public void sendAccountToSQL(Account account) {
        Bukkit.getScheduler().runTaskAsynchronously(AsiluxAPI.INSTANCE, () -> {
            try {
                final PreparedStatement ps = DatabaseManager.PLAYERS_ACCOUNT.getDatabaseAccess().getConnection().prepareStatement(
                        "UPDATE `players_account` SET ranked=?, coins=?, level=?, xp=?, mmr=?, lang=? WHERE uuid=?"
                );
                ps.setString(1,account.getRank().toLowerCase(Locale.ROOT));
                ps.setInt(2, account.getCoins());
                ps.setInt(3, account.getLevel());
                ps.setInt(4, account.getXp());
                ps.setInt(5, account.getMMR());
                ps.setString(6, account.getLang());
                ps.setString(7, account.getUuid().toString());
                ps.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    /**
     * Send account to Redis database.
     * <P>
     * @param account Type: <code>Account</code> | Account of the player.
     */
    public void sendAccountToRedis(Account account) {
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

    private Account getAccountFromDatabase(boolean withCreationIfNotExist) throws AccountNotFoundException {
        final Connection connection = DatabaseManager.PLAYERS_ACCOUNT.getDatabaseAccess().getConnection();
        final UUID uuid = player.getUniqueId();

        if (connection != null) {
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM players_account WHERE uuid = ?");
                ps.setString(1,uuid.toString());
                ps.executeQuery();
                final ResultSet rs = ps.getResultSet();

                if (rs.next()) {

                    final String rank = rs.getString("ranked");
                    final int coins = rs.getInt("coins");
                    final int level = rs.getInt("level");
                    final int xp = rs.getInt("xp");
                    final int mmr = rs.getInt("mmr");
                    final String lang = rs.getString("lang");
                    ps.close();
                    return new Account(uuid, rank, coins, level, xp, mmr, lang);

                } else {
                    ps.close();
                    return withCreationIfNotExist ? registerAccount(uuid, connection) : null;
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return null;
            }
        }

        return null;
    }

    public Account registerAccount(UUID uuid, Connection connection) throws SQLException {
        final Account account = DEFAULT_ACCOUNT.clone();
        final PreparedStatement ps = connection.prepareStatement("INSERT INTO players_account (uuid, ranked, coins, level, xp, mmr, lang) VALUES (?, ?, ?, ?, ?, ?, ?)");
        ps.setString(1, uuid.toString());
        ps.setString(2, account.getRank());
        ps.setInt(3, account.getCoins());
        ps.setInt(4, account.getLevel());
        ps.setInt(5, account.getXp());
        ps.setInt(6, account.getMMR());
        ps.setString(7, account.getLang());

        final boolean r = ps.execute();
        if (r) {
            sendAccountToRedis(account);
            return account;
        }
        return null;
    }
}
