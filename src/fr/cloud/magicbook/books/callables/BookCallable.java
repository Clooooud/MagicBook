package fr.cloud.magicbook.books.callables;

import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public interface BookCallable {

    boolean run(PlayerInteractEvent event);

    default JavaPlugin getPlugin() {
        return (JavaPlugin) Bukkit.getPluginManager().getPlugin("MagicBook");
    }
}
