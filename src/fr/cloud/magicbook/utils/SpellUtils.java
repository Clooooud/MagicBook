package fr.cloud.magicbook.utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Collection;

public class SpellUtils {

    @Nullable
    public static Player getTarget(Player player, double range) {
        Location loc = player.getEyeLocation();
        Vector dir = loc.getDirection();
        for (double d = 0.5; d < range; d += 0.5) {
            Location newLoc = loc.clone().add(dir.clone().multiply(d));

            Collection<Entity> nearbyEntities = newLoc.getWorld().getNearbyEntities(newLoc, 0.2, 0.5, 0.2);
            if (nearbyEntities.isEmpty()) {
                continue;
            }

            if (nearbyEntities.stream().anyMatch(entity -> entity.getType() == EntityType.PLAYER && !entity.getUniqueId().equals(player.getUniqueId()))) {
                return nearbyEntities.stream().filter(entity -> entity.getType() == EntityType.PLAYER && !entity.getUniqueId().equals(player.getUniqueId())).map(entity -> (Player) entity).findFirst().orElse(null);
            }
        }
        return null;
    }
}
