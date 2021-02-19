package fr.cloud.magicbook.events;

import fr.cloud.magicbook.MagicBook;
import fr.cloud.magicbook.books.Book;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {

    private MagicBook plugin;

    public EventListener(MagicBook plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBookUse(PlayerInteractEvent e) {

        if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if(e.getItem() == null) {
            return;
        }

        if(e.getItem().getItemMeta() == null) {
            return;
        }

        if(e.getItem().getItemMeta().getDisplayName() == null ) {
            return;
        }

        Book book = Book.identify(e.getItem());

        if (book == null) {
            return;
        }

        if(e.getItem().getAmount() > 1) {
            e.getPlayer().sendMessage("§5Vous ne pouvez utiliser les livres s'ils sont stackés");
            return;
        }

        book.launchSpell(e);

        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Book.unloadPlayer(e.getPlayer());
    }
}
