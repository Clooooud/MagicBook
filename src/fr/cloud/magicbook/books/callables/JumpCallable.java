package fr.cloud.magicbook.books.callables;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class JumpCallable implements BookCallable {

    @Override
    public boolean run(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Vector vector = player.getLocation().getDirection().normalize();

        if (((Entity) player).isOnGround()) {

            vector.setX(vector.getX() * 8);
            vector.setZ(vector.getZ() * 8);
            vector.setY(1.25);

        } else {

            vector.setX(vector.getX() * 4);
            vector.setZ(vector.getZ() * 4);
            vector.setY(1.5);

        }

        player.setVelocity(vector);

        player.sendMessage("§aVous venez de vous propulser en avant !");

        return true;
    }
}
