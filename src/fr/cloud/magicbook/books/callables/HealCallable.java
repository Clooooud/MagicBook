package fr.cloud.magicbook.books.callables;

import fr.cloud.magicbook.config.Parameter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class HealCallable implements BookCallable {

    @Parameter
    private int healthHealed = 8;

    @Override
    public boolean run(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.getHealth() == 20) {
            player.sendMessage("§cTu as déjà toute ta vie !");
            return false;
        }

        if (player.getHealth() == 0) {
            return false;
        }

        player.setHealth(healthHealed + player.getHealth() > 20 ? 20 : healthHealed + player.getHealth());
        player.sendMessage("§aTu as été soigné !");

        return true;
    }
}
