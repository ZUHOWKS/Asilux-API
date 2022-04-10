package zuhowks.asiluxteam.fr.asiluxapi.spigot.asilux.game;


import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameAPI implements Listener {

    private List<Object> stats;
    private String name;
    private final String command;
    private final ItemStack item;
    private String desc;

    public GameAPI(String name, String command, String itemName, String materialName, String... lore) {
        //Initialisation variable
        this.stats = new ArrayList<>();
        this.name = name;
        this.command = command;
        this.item = new ItemStack(Material.getMaterial(materialName), 1);
        this.desc = Arrays.toString(lore);

        //Set a basic item meta
        ItemMeta meta = this.item.getItemMeta();
        meta.setDisplayName(itemName);
        meta.setLore(Arrays.asList(lore));
        this.item.setItemMeta(meta);
    }

    public GameAPI(String name, String command, String itemName, Material material, String... lore) {
        //Initialisation variable
        this.stats = new ArrayList<>();
        this.name = name;
        this.command = command;
        this.item = new ItemStack(material, 1);
        this.desc = Arrays.toString(lore);

        //Set a basic item meta
        ItemMeta meta = this.item.getItemMeta();
        meta.setDisplayName(itemName);
        meta.setLore(Arrays.asList(lore));
        this.item.setItemMeta(meta);
    }

    public ItemStack getItem() {
        return this.item;
    }

    public String getName() {
        return this.name;
    }

    public List<Object> getStats() {
        return this.stats;
    }

    public String getDescription() {
        return this.desc;
    }

    public String getCommand() {
        return this.command;
    }

    public void setStats(Object... stat) {
        this.stats = Arrays.asList(stat);
    }

    public void setName(String name, String itemName) {
        this.name = name;
        ItemMeta meta = this.item.getItemMeta();
        meta.setDisplayName(itemName);
        this.item.setItemMeta(meta);
    }

    public void setDesc(String... lore) {
        this.desc = Arrays.toString(lore);
        ItemMeta meta = this.item.getItemMeta();
        meta.setLore(Arrays.asList(lore));
        this.item.setItemMeta(meta);

    }

    public void addStats(Object... stats) {
        for (Object stat : stats) {
            getStats().add(stat);
        }
    }
}
