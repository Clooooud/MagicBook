package fr.cloud.magicbook.commands;

import fr.cloud.magicbook.MagicBook;
import fr.cloud.magicbook.books.Book;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MagicBookCommand implements TabExecutor {

    private final MagicBook plugin;

    public MagicBookCommand(MagicBook plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("§cUsage: /magicbook <reload/version/give/resetcd>");
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
            case "resetcd": {
                if (args.length > 1) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player == null) {
                        sender.sendMessage("§cErreur: Joueur introuvable");
                        return true;
                    }

                    Book.resetCooldown(player);
                } else {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("§cUsage: /magicbook resetcd <player>");
                        return true;
                    }

                    Book.resetCooldown((Player) sender);
                }
                break;
            }
            case "give": {
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /magicbook give <player> <name> [infinite:y/n]");
                    sender.sendMessage("§cListe des livres: §5" + Book.getBookSet().stream().map(Book::getRegistryName).collect(Collectors.joining("§c, §5")));
                    return true;
                }

                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    sender.sendMessage("§cErreur: Joueur introuvable");
                    return true;
                }

                Book books = Book.getBook(args[2]);
                if (books == null) {
                    sender.sendMessage("§cUsage: /magicbook give <player> <name> [infinite:y/n]");
                    sender.sendMessage("§cListe des livres: §5" + Book.getBookSet().stream().map(Book::getRegistryName).collect(Collectors.joining("§c, §5")));
                    return true;
                }

                boolean infinite = false;
                if (args.length == 4) {
                    if (args[3].equalsIgnoreCase("y")) {
                        infinite = true;
                    }
                }

                ItemStack stack = infinite ? books.getStack(-1) : books.getStack();
                player.getInventory().addItem(stack);
                sender.sendMessage("§aEt voici ton livre !");
                break;
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        switch (args.length) {
            case 1:
                return Arrays.asList("reload", "version", "give", "resetcd");
            case 2:
                if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("resetcd")) {
                    return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
                }
            case 3:
                if (args[0].equalsIgnoreCase("give")) {
                    return Book.getBookSet().stream().map(Book::getRegistryName).filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
                }
            case 4:
                if (args[0].equalsIgnoreCase("give")) {
                    return Arrays.asList("y", "n");
                }
        }
        return Collections.emptyList();
    }
}
