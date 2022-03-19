package fr.cloud.magicbook.utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpellUtils {

    @Nullable
    public static Player getTarget(Player player, double range) {
        Location loc = player.getEyeLocation();
        Vector dir = loc.getDirection();
        for (double d = 0.5; d < range; d += 0.5) {
            Location newLoc = loc.clone().add(dir.clone().multiply(d));

            Collection<Entity> nearbyEntities = newLoc.getWorld().getNearbyEntities(newLoc, 0.3, 0.5, 0.3);
            if (nearbyEntities.isEmpty()) {
                continue;
            }

            Collection<Player> nearbyPlayers = nearbyEntities.stream()
                    .filter(entity -> !entity.getUniqueId().equals(player.getUniqueId()))
                    .filter(entity -> entity.getType() == EntityType.PLAYER)
                    .map(entity -> (Player) entity)
                    .collect(Collectors.toList());

            if (!nearbyPlayers.isEmpty()) {
                return nearbyPlayers.stream().findAny().orElse(null);
            }
        }
        return null;
    }
}
