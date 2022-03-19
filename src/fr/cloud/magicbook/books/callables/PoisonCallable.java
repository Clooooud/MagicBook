package fr.cloud.magicbook.books.callables;

import fr.cloud.magicbook.config.Parameter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PoisonCallable implements TargetCallable {

    @Parameter
    private double duration = 8;
    @Parameter
    private double range = 15;
    @Parameter
    private int amplifier = 0;

    @Override
    public boolean run(PlayerInteractEvent event, Player target) {
        target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, (int) (duration * 20), amplifier, true, true));
        event.getPlayer().sendMessage("§cVous avez empoisonné " + target.getName() + " !");
        return true;
    }

    @Override
    public double getRange() {
        return range;
    }
}
