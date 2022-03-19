package fr.cloud.magicbook.utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Collection;
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

            Stream<Entity> entityStream = nearbyEntities.stream().filter(entity -> entity.getType() == EntityType.PLAYER && !entity.getUniqueId().equals(player.getUniqueId()));

            if (entityStream.findAny().isPresent()) {
                return entityStream.map(entity -> (Player) entity).findFirst().orElse(null);
            }
        }
        return null;
    }
}
