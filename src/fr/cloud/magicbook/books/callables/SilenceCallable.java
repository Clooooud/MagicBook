package fr.cloud.magicbook.books.callables;

import fr.cloud.magicbook.config.Parameter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class SilenceCallable implements TargetCallable {

    @Parameter
    private double duration = 2.5, range = 15;

    @Override
    public boolean run(PlayerInteractEvent event, Player target) {
        Player player = event.getPlayer();

        JavaPlugin plugin = getPlugin();
        target.setMetadata("silence", new FixedMetadataValue(plugin, System.currentTimeMillis() + 2500));
        target.sendMessage("§5Vous avez été silence par " + player.getName());
        player.sendMessage("§5Vous avez silence " + target.getName());

        Bukkit.getScheduler().runTaskLater(plugin, () -> target.removeMetadata("silence", plugin), (long) (duration*20));
        return true;
    }

    @Override
    public double getRange() {
        return range;
    }
}
