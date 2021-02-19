package fr.cloud.magicbook.books.callables;

import fr.cloud.magicbook.config.Parameter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class GeyserCallable implements TargetCallable {

    @Parameter
    private int damage = 3;

    @Parameter
    private double range = 15;

    @Override
    public boolean run(PlayerInteractEvent event, Player target) {

        target.setNoDamageTicks(target.getMaximumNoDamageTicks());
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> target.setVelocity(target.getVelocity().setY(1)), 1);

        if(target.getHealth() >= damage)
            target.setHealth(target.getHealth()-damage);
        else
            target.setHealth(0);

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

        return true;
    }

    @Override
    public double getRange() {
        return range;
    }

}
