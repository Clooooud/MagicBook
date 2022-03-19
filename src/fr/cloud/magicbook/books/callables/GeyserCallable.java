package fr.cloud.magicbook.books.callables;

import fr.cloud.magicbook.config.Parameter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class GeyserCallable implements TargetCallable {

    @Parameter
    private int damage = 3;

    @Parameter
    private double range = 15;

    @Override
    public boolean run(PlayerInteractEvent event, Player target) {

        if (target.getGameMode() == GameMode.CREATIVE) {
            return false;
        }

        target.setVelocity(new Vector(0, 1, 0));

        if(target.getHealth() >= damage) {
            target.setHealth(target.getHealth()-damage);
        } else {
            target.setHealth(0);
        }

        Location location = target.getLocation();
        location.getWorld().playSound(location, Sound.HURT_FLESH, 1, 1);

        JavaPlugin plugin = getPlugin();
        for(int i = 0; i <= 2; i++) {
            int finalI = i;
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Block block = location.clone().add(0, finalI, 0).getBlock();
                if(block.getType() == Material.AIR) {
                    block.setType(Material.STATIONARY_WATER);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> block.setType(Material.AIR), 5);
                }
            }, i*2);
        }

        event.getPlayer().sendMessage("§aTu as fait apparaître un geyser au pied de " + target.getName() + " !");

        return true;
    }

    @Override
    public double getRange() {
        return range;
    }

}
