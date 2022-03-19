package fr.cloud.magicbook;

import fr.cloud.magicbook.books.Book;
import fr.cloud.magicbook.commands.MagicBookCommand;
import fr.cloud.magicbook.config.ConfigCreator;
import fr.cloud.magicbook.events.EventListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class MagicBook extends JavaPlugin {

    @Getter
    private WorldGuardManager worldGuardManager;
    @Getter
    private ConfigCreator configCreator;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        PluginCommand magicBook = getCommand("magicbook");
        magicBook.setExecutor(new MagicBookCommand(this));
        magicBook.setPermission("magicbook.admin");
        magicBook.setPermissionMessage("§cTu n'as pas accès à cette commande.");
    }

    @Override
    public void onLoad() {
        Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
        if (worldGuard == null) {
            getLogger().warning("WorldGuard isn't detected, ignoring this feature.");
        } else {
            worldGuardManager = new WorldGuardManager(this);
            worldGuardManager.load();
        }

        Book.loadBooks();

        configCreator = new ConfigCreator(this);
        configCreator.load();
    }
}
