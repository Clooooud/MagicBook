package fr.cloud.magicbook.books.callables;

import fr.cloud.magicbook.config.Parameter;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class DrainCallable implements TargetCallable {

    @Parameter
    private int damage = 2;

    @Parameter
    private double range = 15;

    @Override
    public boolean run(PlayerInteractEvent event, Player target) {
        Player player = event.getPlayer();

        if(target.getHealth() >= damage) {
            target.setHealth(target.getHealth()-damage);
        } else {
            target.setHealth(0);
        }

        Location start = player.getLocation().add(0, 0.5, 0);
        Location end = target.getLocation().add(0, 0.5, 0);

        end.getWorld().playSound(end, Sound.HURT_FLESH, 1, 1);

        double d = start.distance(end) / 10;
        Vector direction = end.toVector().subtract(start.toVector()).normalize();
        for (int i = 0; i < 10; i++) {
            Location l = start.clone();
            Vector v = direction.clone().multiply(i * d);
            l.add(v);

            PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.SPELL_MOB, true, (float) l.getX(), (float) l.getY(), (float) l.getZ(), 0, 0, 0, 0, 10);
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
            player.getNearbyEntities(32,32,32).stream().filter(entity -> entity.getType() == EntityType.PLAYER).forEach(p -> ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet));
        }

        player.setHealth(damage + player.getHealth() > 20 ? 20 : damage + player.getHealth());
        player.sendMessage("§aTu as drainé de la vie de " + target.getName() + " !");
        return true;
    }

    @Override
    public double getRange() {
        return range;
    }
}
