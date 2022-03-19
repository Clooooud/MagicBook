package fr.cloud.magicbook.events;

import fr.cloud.magicbook.MagicBook;
import fr.cloud.magicbook.books.Book;
import fr.cloud.magicbook.player.MagicBookPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {

    private MagicBook plugin;

    public EventListener(MagicBook plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAsyncJoin(AsyncPlayerPreLoginEvent event) {
        MagicBookPlayer.loadPlayer(event.getName());
    }

    @EventHandler
    public void onBookUse(PlayerInteractEvent event) {
        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if(!event.hasItem()) {
            return;
        }

        if(!event.getItem().hasItemMeta()) {
            return;
        }

        if(!event.getItem().getItemMeta().hasDisplayName()) {
            return;
        }

        if(!event.getItem().getItemMeta().hasLore()) {
            return;
        }

        Book book = Book.identify(event.getItem());

        if (book == null) {
            return;
        }

        if(event.getItem().getAmount() > 1) {
            event.getPlayer().sendMessage("§cVous ne pouvez utiliser les livres s'ils sont stackés");
            return;
        }

        book.launchSpell(event);
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        MagicBookPlayer.getPlayer(event.getEntity()).resetCooldowns();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        MagicBookPlayer.unloadPlayer(event.getPlayer());
    }
}
