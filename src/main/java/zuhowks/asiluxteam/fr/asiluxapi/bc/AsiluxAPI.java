package zuhowks.asiluxteam.fr.asiluxapi.bc;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import zuhowks.asiluxteam.fr.asiluxapi.commons.game.GamesRegistry;
import zuhowks.asiluxteam.fr.asiluxapi.bc.data.management.redis.RedisManager;
import zuhowks.asiluxteam.fr.asiluxapi.bc.eventslistener.player.PlayerListener;
import zuhowks.asiluxteam.fr.asiluxapi.bc.eventslistener.proxy.ProxyListener;
import zuhowks.asiluxteam.fr.asiluxapi.bc.data.management.sql.DatabaseManager;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public final class AsiluxAPI extends Plugin {

    public static AsiluxAPI INSTANCE;
    public File pluginFile;
    public FileYML configFile;
    public String mainChannel;
    public GamesRegistry gamesRegistry;

    @Override
    public void onEnable() {
        INSTANCE = this;

        //Get file & configuration file.
        configFile = new FileYML("config");

        //Init Database & Redis
        DatabaseManager.initAllDatabaseConnection();
        RedisManager.initRedissons();

        //ZUHOWKS Account
        //accounts.add(new Account(0, UUID.fromString("b887bc07-dd49-4aee-9193-dd159ee4912a"), "Admin", 100000, 100, 15800));

        //Init gamesRegistry;
        gamesRegistry = new GamesRegistry("gameAPI");
        //gamesRegistry.registerGame("Bedwars", "say cc", ChatColor.RED + "Bedwars" , "BED", "Click ?");

        //Register AListener channel for all servers => Main Channel!
        this.mainChannel = "AListener";
        this.getProxy().registerChannel(mainChannel);

        PluginManager pm = this.getProxy().getPluginManager();
        pm.registerListener(this, new PlayerListener());
        pm.registerListener(this, new ProxyListener());
    }

    @Override
    public void onDisable() {
        try {
            RedisManager.closeRedissons();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        DatabaseManager.closeAllDatabaseConnection();
    }
}
