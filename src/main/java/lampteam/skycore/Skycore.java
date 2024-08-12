package lampteam.skycore;

import lampteam.skycore.listeners.BlockRelatedEvents;
import lampteam.skycore.listeners.EntityRelatedEvents;
import lampteam.skycore.listeners.PlayerRelatedEvents;
import lampteam.skycore.managers.ArenasManager;
import lampteam.skycore.managers.ConfigsManager;
import lampteam.skycore.managers.MenusManager;
import lampteam.skycore.managers.PlaceholdersManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Skycore extends JavaPlugin {

    private static Skycore plugin;

    public static Skycore getPlugin(){
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;

        Bukkit.getServer().getPluginManager().registerEvents(new PlayerRelatedEvents(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new EntityRelatedEvents(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new BlockRelatedEvents(), this);

        new PlaceholdersManager().register();

        ConfigsManager.loadArenasConfigs();
        ArenasManager.loadArenasFromConfig();
        MenusManager.init();


    }

    public static void reloadPlugin(){


        ConfigsManager.loadArenasConfigs();
        ArenasManager.loadArenasFromConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}