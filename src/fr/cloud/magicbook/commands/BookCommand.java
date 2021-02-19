package fr.cloud.magicbook.commands;

import fr.cloud.magicbook.books.Book;
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

public class BookCommand implements TabExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("§cUsage: /book <name> [infinite:y/n]");
            sender.sendMessage("§cListe des livres: §5" + Book.getBookSet().stream().map(Book::getRegistryName).collect(Collectors.joining("§c, §5")));
            return true;
        }

        Book books = Book.getBook(args[0]);
        if (books == null) {
            sender.sendMessage("§cUsage: /book <name> [infinite:y/n]");
            sender.sendMessage("§cListe des livres: §5" + Book.getBookSet().stream().map(Book::getRegistryName).collect(Collectors.joining("§c, §5")));
            return true;
        }

        boolean infinite = false;
        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("y")) {
                infinite = true;
            }
        }

        ItemStack stack = infinite ? books.getStack(-1) : books.getStack();
        ((Player) sender).getInventory().addItem(stack);
        sender.sendMessage("§5Et voici ton livre !");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            return Book.getBookSet().stream().map(Book::getRegistryName).filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        } else if (args.length == 2) {
            return Arrays.asList("y", "n");
        }
        return Collections.emptyList();
    }
}
