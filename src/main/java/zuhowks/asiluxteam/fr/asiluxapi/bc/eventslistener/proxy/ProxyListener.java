package zuhowks.asiluxteam.fr.asiluxapi.bc.eventslistener.proxy;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import zuhowks.asiluxteam.fr.asiluxapi.bc.AsiluxAPI;
import zuhowks.asiluxteam.fr.asiluxapi.commons.game.GamesRegistry;


public class ProxyListener implements Listener {

    @EventHandler
    public void onProxyPing(ProxyPingEvent e){
        final ServerPing serverPing = e.getResponse();
        String vDiff = ChatColor.GOLD + "!JOIN NOW!";
        serverPing.getPlayers().setMax(0);
        if (e.getConnection().getVersion() < serverPing.getVersion().getProtocol())
            vDiff = ChatColor.RED + "You are in a too low version to play on the server";

        serverPing.setDescriptionComponent(new TextComponent(ChatColor.YELLOW + "Server Asilux enable from " + ChatColor.AQUA + serverPing.getVersion().getName() + ChatColor.YELLOW + " !\n" +
                "\t" + vDiff));
        e.setResponse(serverPing);

    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if(event.getTag().equals(AsiluxAPI.INSTANCE.mainChannel)) {
            final ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
            final String sub = in.readUTF();
            /*
            if (sub.equals("getAccount")) {
                final String uuidStr = in.readUTF();
                final UUID uuid = UUID.fromString(uuidStr);

                final ArrayList<Account> accounts = AsiluxAPI.INSTANCE.accounts;
                for (Account account : accounts) {
                    if (account.getUuid().equals(uuid)) {
                        //Send plugin message with account info
                        final ByteArrayDataOutput out = ByteStreams.newDataOutput();

                        out.writeUTF("getAccount");
                        out.writeInt(account.getId());
                        out.writeUTF(account.getUuid().toString());
                        out.writeUTF(account.getRank());
                        out.writeInt(account.getCoins());
                        out.writeInt(account.getLevel());
                        out.writeInt(account.getXp());

                        final ProxiedPlayer player = AsiluxAPI.INSTANCE.getProxy().getPlayer(event.getReceiver().toString());

                        player.getServer().sendData(AsiluxAPI.INSTANCE.mainChannel, out.toByteArray());
                        refreshAsiluxGames(player);
                    }
                }
            }
            */
            if (sub.equals("refreshGame")) {
                //refreshAsiluxGames(AsiluxAPI.INSTANCE.getProxy().getPlayer(event.getReceiver().toString()));
            }
            if (sub.equals("newGame")) {

                final String gameName = in.readUTF();
                final String command = in.readUTF();
                final String itemName = in.readUTF();
                final String materialName = in.readUTF();
                final String lore = in.readUTF();

                AsiluxAPI.INSTANCE.gamesRegistry.registerGame(gameName, command, itemName, materialName, lore);
            }
        }
    }

    public void refreshAsiluxGames(ProxiedPlayer player) {
        GamesRegistry gamesRegistry = AsiluxAPI.INSTANCE.gamesRegistry;
        System.out.println("CALLED METHODE: " + gamesRegistry.getGameNames().size() );
        for (int i = 0 ; i < gamesRegistry.getGameNames().size(); i++ ) {
            final ByteArrayDataOutput out = ByteStreams.newDataOutput();

            out.writeUTF("asiluxGamesRefresh");
            out.writeUTF(gamesRegistry.getGameNames().get(i));
            out.writeUTF(gamesRegistry.getCommands().get(i));
            out.writeUTF(gamesRegistry.getItemNames().get(i));
            out.writeUTF(gamesRegistry.getMaterialNames().get(i));
            StringBuilder lore = new StringBuilder();
            for (String str : gamesRegistry.getDescs().get(i)) {
                lore.append(str).append(",");
            }
            out.writeUTF(lore.toString());
            System.out.println("REFRESH PACKET SEND WITH: " + gamesRegistry.getGameNames().get(i) + ", " + gamesRegistry.getCommands().get(i) + ", " + gamesRegistry.getItemNames().get(i) + ", " + gamesRegistry.getMaterialNames().get(i));
            player.getServer().sendData(AsiluxAPI.INSTANCE.mainChannel, out.toByteArray());
        }
    }
}
