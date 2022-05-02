package zuhowks.asiluxteam.fr.asiluxapi.bc;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileYML {

    private final File file;
    private final String fileName;

    public FileYML(String fileName) {
        this.fileName = fileName;
        this.file = new File(AsiluxAPI.INSTANCE.getDataFolder(), fileName + ".yml");
        createFile();
    }

    /**
     * Methode to create a file with default config if not exist.
     */
    public void createFile() {
        if (!AsiluxAPI.INSTANCE.getDataFolder().exists()) {
            AsiluxAPI.INSTANCE.getDataFolder().mkdir(); // Creates the plugin's folder inside '/plugins'
        }

        if (!file.exists()) {
            try {
                Files.copy(AsiluxAPI.INSTANCE.getResourceAsStream(fileName + ".yml"), // This will copy your default config.yml from the jar
                        file.toPath());
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
