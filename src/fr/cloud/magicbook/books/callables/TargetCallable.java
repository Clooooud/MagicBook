package fr.cloud.magicbook.books.callables;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public interface TargetCallable extends BookCallable {

    @Override
    default boolean run(PlayerInteractEvent event) {
        return run(event, null);
    }

    boolean run(PlayerInteractEvent event, Player target);

    double getRange();
}
