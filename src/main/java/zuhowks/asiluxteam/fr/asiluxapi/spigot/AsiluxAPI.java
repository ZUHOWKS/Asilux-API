package zuhowks.asiluxteam.fr.asiluxapi.spigot;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.Test;
import zuhowks.asiluxteam.fr.asiluxapi.commons.game.GamesRegistry;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.asilux.command.BankCommand;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.asilux.game.GameManager;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.data.management.redis.RedisManager;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.listeners.player.PlayerJoinedListener;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.listeners.server.ServerMessageListener;

public final class AsiluxAPI extends JavaPlugin {

    public static AsiluxAPI INSTANCE;
    public String mainChannel;
    public GameManager gameManager;
    public boolean isLobby = true;
    public GamesRegistry gamesRegistry;


    @Override
    public void onEnable() {

        INSTANCE = this;

        //Setup the main channel's name
        this.mainChannel = "AListener";

        //Load & save config | If config file not exist, create a default config file
        this.saveDefaultConfig();

        //Redis Access Setup
        RedisManager.initRedissons();

        //Register and manage games and inventory of the game menu
        this.gameManager = new GameManager();
        this.gamesRegistry = new GamesRegistry("gameAPI");

        //Plugin Channel to communicate with BungeeCord Asilux-API
        this.getServer().getMessenger().registerIncomingPluginChannel(this, this.mainChannel, new ServerMessageListener());
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, this.mainChannel);

        //Registery event Listener
        eventsRegistry(this.getServer().getPluginManager(),
                new PlayerJoinedListener(), //Player Joined Event listener
                gameManager //Inventory of the game menu listener
        );

        //Registry Command
        this.getCommand("bank").setExecutor(new BankCommand());

        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("asiluxGamesRefresh");
    }

    @Override
    public void onDisable() {
        RedisManager.closeRedissons();
    }


    /**
     * Methode to registry event listener more simply.
     *
     * @param pluginManager: PluginManager | Server's Plugin Manager.
     * @param eventsListener: Tab Object | Object which implement Listener.
     */
    public void eventsRegistry(PluginManager pluginManager, Object... eventsListener) {
        if (eventsListener == null || eventsListener.length <= 0) return;
        for (Object eventListener : eventsListener) {
            if (eventListener instanceof Listener)
                pluginManager.registerEvents((Listener) eventListener, this);
        }
    }
}
