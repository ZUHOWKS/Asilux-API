package zuhowks.asiluxteam.fr.asiluxapi.spigot.listeners.server;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.AsiluxAPI;
import zuhowks.asiluxteam.fr.asiluxapi.spigot.asilux.game.GameAPI;

public class ServerMessageListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if(channel.equals(AsiluxAPI.INSTANCE.getMainChannel())) {
            final ByteArrayDataInput in = ByteStreams.newDataInput(message);
            final String sub = in.readUTF();

            if (sub.equals("getAccount")) {
                final int id = in.readInt();
                final String uuid = in.readUTF();
                final String rank = in.readUTF();
                final int coins = in.readInt();
                final int level = in.readInt();
                final int xp = in.readInt();

                player.sendMessage(ChatColor.YELLOW + " " + id + ", " + uuid + ", " + rank + ", " + coins + ", " + level + ", " + xp);
            }

            if (sub.equals("asiluxGamesRefresh")) {

                System.out.println("REFRESH PACKET RECEIVED");

                final String gameName = in.readUTF();
                final String command = in.readUTF();
                final String itemName = in.readUTF();
                final String materialName = in.readUTF();
                final String lore = in.readUTF();

                AsiluxAPI.INSTANCE.getGameManager().addGame(new GameAPI(gameName, command, itemName, Material.getMaterial(materialName), lore.split(",")));
            }
        }
    }
}
