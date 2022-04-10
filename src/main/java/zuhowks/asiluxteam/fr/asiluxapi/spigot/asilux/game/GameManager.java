package zuhowks.asiluxteam.fr.asiluxapi.spigot.asilux.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GameManager implements Listener {

    private final List<GameAPI> games; //Asilux game's registry
    private final Inventory gameMenu; //Asilux game's selector
    private final ItemStack itemNav; //Asilux game's item nav

    public GameManager() {
        //Init variable
        this.games = new ArrayList<>();
        this.gameMenu = Bukkit.createInventory(null, 36, ChatColor.YELLOW + "Game's Menu");
        this.itemNav = new ItemStack(Material.COMPASS, 1);

        //Set Game Selector's meta
        ItemMeta meta = this.itemNav.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Game Selector");
        this.itemNav.setItemMeta(meta);
    }

    public ItemStack getItemNav() {
        return this.itemNav;
    }

    public void addGame(GameAPI game) {
        for (GameAPI gameAPI : games)
            if (gameAPI.getName().equals(game.getName()))
                return;
        this.games.add(game);
        int size = games.size() - 1;
        this.gameMenu.setItem(((size - 5 * ((int) size / 5)) + 2 + (9 * (1 + (size / 5)))), games.get(size).getItem());
    }

    @EventHandler
    public void onItemClick(final PlayerInteractEvent e) {

        final Player p = e.getPlayer();
        final Action action = e.getAction();
        final ItemStack itemStack = e.getItem();

        if (action == Action.RIGHT_CLICK_AIR && itemStack.getItemMeta().hasDisplayName() && itemStack.getType() == itemNav.getType() && itemStack.getItemMeta().getDisplayName().equals(itemNav.getItemMeta().getDisplayName())) {
            p.openInventory(this.gameMenu);
        }

    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().getName().equals(this.gameMenu.getName())) return;

        e.setCancelled(true);
        final ItemStack clickedItem = e.getCurrentItem();
        if (!(clickedItem == null || clickedItem.getType() == Material.AIR || clickedItem.getType() == Material.STAINED_GLASS_PANE)) {
            for (GameAPI game : games) {
                if (game != null && game.getItem().getType() == clickedItem.getType()) {
                    final Player p = (Player) e.getWhoClicked();
                    System.out.println(game.getCommand());
                    p.closeInventory();
                    p.performCommand(game.getCommand());
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(gameMenu)) {
            e.setCancelled(true);
        }
    }
}
