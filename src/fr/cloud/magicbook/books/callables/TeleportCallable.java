package fr.cloud.magicbook.books.callables;

import fr.cloud.magicbook.config.Parameter;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.Set;

public class TeleportCallable implements BookCallable {

    @Parameter
    private int range = 20;

    @Override
    public boolean run(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = player.getTargetBlock((Set<Material>) null, range);
        Location start = player.getLocation().add(0,0.5,0);

        if (block.getType() == Material.AIR) {
            player.sendMessage("§cHors de portée !");
            return false;
        }

        Block blockUp = block.getRelative(BlockFace.UP);
        Block blockDown = block.getRelative(BlockFace.DOWN);
        Location to = null;

        if (!blockUp.getType().isSolid()) {
            if (!blockUp.getRelative(BlockFace.UP).getType().isSolid()) {
                to = blockUp.getLocation();
            }
        } else if (!blockDown.getType().isSolid()) {
            if (!blockDown.getRelative(BlockFace.DOWN).getType().isSolid()) {
                to = blockDown.getRelative(BlockFace.DOWN).getLocation();
            }
        }
        if (to != null) {
            to.setYaw(player.getLocation().getYaw());
            to.setPitch(player.getLocation().getPitch());
            to.add(0.5, 0, 0.5);
            player.teleport(to);

            to.add(0,0.5,0);

            double d = start.distance(to) / 10;
            Vector direction = to.toVector().subtract(start.toVector()).normalize();
            for (int i = 0; i < 10; i++) {
                Location l = start.clone();
                Vector v = direction.clone().multiply(i * d);
                l.add(v);

                PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.SPELL_MOB, true, (float) l.getX(), (float) l.getY(), (float) l.getZ(), 0, 0, 0, 0, 10);
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
                player.getNearbyEntities(32,32,32).stream().filter(entity -> entity.getType() == EntityType.PLAYER).forEach(p -> ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet));
            }

            player.sendMessage("§aTu viens de te téléporter !");
            return true;
        }

        player.sendMessage("§cTu ne peux pas te téléporter ici");
        return false;
    }

}
