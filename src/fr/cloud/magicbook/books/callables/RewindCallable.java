package fr.cloud.magicbook.books.callables;

import fr.cloud.magicbook.config.Parameter;
import fr.cloud.magicbook.player.MagicBookPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class RewindCallable implements BookCallable {

    @Parameter
    private double cooldownReducePercentage = 75;

    @Override
    public boolean run(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        MagicBookPlayer bookPlayer = MagicBookPlayer.getPlayer(player);

        if (bookPlayer.getCooldowns().size() == 0 && bookPlayer.getCooldowns().values().stream().anyMatch(l -> System.currentTimeMillis() - l < 0)) {
            player.sendMessage("§cVous n'avez aucun temps de rechargement sur vos livres");
            return false;
        }

        bookPlayer.getCooldowns().replaceAll((book, l) -> l - (long) (book.getCooldown() * (cooldownReducePercentage / 100)) * 1000);
        player.sendMessage("§aVos temps de rechargement ont été réduit.");

        // TODO: particules

        return true;
    }
}
