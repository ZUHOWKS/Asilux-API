package zuhowks.asiluxteam.fr.asiluxapi.bc;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileYML {

    private String fileName;

    public FileYML(String fileName) {
        this.fileName = fileName;
        createFile();
    }

    /**
     * Methode to create a file with default config if not exist.
     */
    public void createFile() {
        if (!AsiluxAPI.INSTANCE.getDataFolder().exists()) {
            AsiluxAPI.INSTANCE.getDataFolder().mkdir();
        }

        File file = new File(AsiluxAPI.INSTANCE.getDataFolder(), fileName + ".yml");

        if (!file.exists()) {
            try {
                file.createNewFile();

                Configuration configFile = this.getConfig();
                if (configFile == null || !configFile.contains("redis-manager")) {
                    Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File("config.yml"));
                    this.saveConfig(config);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Methode to get configuration of a file.
     *
     * @return Configuration | Null if file don't exist and Configuration if file exist.
     */
    public Configuration getConfig() {
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(AsiluxAPI.INSTANCE.getDataFolder(), fileName + ".yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Methode to save a config file in a file.
     *
     * @param config: Configuration | A configuration of a file.
     */
    public void saveConfig(Configuration config) {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(AsiluxAPI.INSTANCE.getDataFolder(), fileName + ".yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
