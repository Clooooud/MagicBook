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

        if(player.getHealth() == 20) {
            player.sendMessage("§5Tu as déjà toute ta vie !");
            return false;
        }

        player.setHealth(healthHealed + player.getHealth() > 20 ? 20 : healthHealed + player.getHealth());
        player.sendMessage("§5Tu as été soigné !");

        return true;
    }
}
