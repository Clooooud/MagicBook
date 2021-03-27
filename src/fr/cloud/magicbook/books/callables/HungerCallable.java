package fr.cloud.magicbook.books.callables;

import fr.cloud.magicbook.config.Parameter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class HungerCallable implements TargetCallable {

    private HashMap<Player, BukkitTask> hungered = new HashMap<>();

    @Parameter private double range = 15;
    @Parameter private double duration = 4;

    @Override
    public boolean run(PlayerInteractEvent event, Player target) {

        hungered.put(target, new BukkitRunnable() {
            @Override
            public void run() {
                if (hungered.get(target).getTaskId() != this.getTaskId()) {
                    return;
                }

                target.setFoodLevel(20);
                hungered.remove(target);
            }
        }.runTaskLater(getPlugin(), (long) (duration * 20)));
        target.setFoodLevel(4);

        for (int i = 0; i < 3; i++) {
            int delay = i*5;
            Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> target.getWorld().playSound(target.getLocation(), delay == 10 ? Sound.BURP : Sound.EAT, 1, 1), delay);
        }

        // TODO: particules

        event.getPlayer().sendMessage("§aVous avez fatigué " + target.getName());
        target.sendMessage("§c" + event.getPlayer().getName() + " vous a fatigué !");

        return true;
    }

    @Override
    public double getRange() {
        return range;
    }
}
