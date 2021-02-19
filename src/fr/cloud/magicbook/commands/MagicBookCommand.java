package fr.cloud.magicbook.commands;

import fr.cloud.magicbook.MagicBook;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class MagicBookCommand implements TabExecutor {

    private MagicBook plugin;

    public MagicBookCommand(MagicBook plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("§cUsage: /magicbook <reload/version>");
            return true;
        }

        args = Stream.of(args).map(String::toLowerCase).toArray(String[]::new);

        switch (args[0]) {
            case "reload":
                plugin.getConfigCreator().load();
                sender.sendMessage("§aLa configuration du plugin a bien été rechargé.");
                break;
            case "version":
                sender.sendMessage("§7MagicBook §av" + plugin.getDescription().getVersion() + "\n§7by " + plugin.getDescription().getAuthors().get(0));
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "version");
        }
        return Collections.emptyList();
    }
}
