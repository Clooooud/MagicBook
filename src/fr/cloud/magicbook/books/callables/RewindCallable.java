package fr.cloud.magicbook.books.callables;

import fr.cloud.magicbook.books.Book;
import fr.cloud.magicbook.config.Parameter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class RewindCallable implements BookCallable {

    @Parameter private double cooldownReducePercentage = 75;

    @Override
    public boolean run(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (Book.getCooldowns().entrySet().stream().noneMatch(entry -> entry.getValue().containsKey(player) && entry.getValue().get(player) - System.currentTimeMillis() > 0)) {
            player.sendMessage("§cVous n'avez aucun temps de rechargement sur vos livres");
            return false;
        }

        Book.getCooldowns().entrySet().stream().filter(entry -> entry.getValue().containsKey(player)).forEach(entry -> entry.getValue().put(player, entry.getValue().get(player) - (long)(entry.getKey().getCooldown() * (cooldownReducePercentage / 100)) * 1000));
        player.sendMessage("§aVos temps de rechargement ont été réduit.");

        // TODO: particules

        return true;
    }
}
