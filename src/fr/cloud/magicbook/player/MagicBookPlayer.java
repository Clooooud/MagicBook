package fr.cloud.magicbook.player;

import fr.cloud.magicbook.books.Book;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@RequiredArgsConstructor
public class MagicBookPlayer {

    private static final ConcurrentHashMap<String, MagicBookPlayer> players = new ConcurrentHashMap<>();

    public static MagicBookPlayer getPlayer(Player player) {
        return players.get(player.getName());
    }

    public static void unloadPlayer(Player player) {
        if (player == null) {
            return;
        }

        players.remove(player.getName());
    }

    public static void loadPlayer(String username) {
        players.put(username, new MagicBookPlayer(username));
    }

    private final String username;

    @Getter(AccessLevel.NONE)
    private boolean canBeTargeted = true;
    private boolean isSilenced = false;
    private final HashMap<Book, Long> cooldowns = new HashMap<>();

    public Long getCooldown(Book book) {
        return cooldowns.getOrDefault(book, -1L);
    }

    public void resetCooldowns() {
        cooldowns.clear();
    }

    public void resetCooldown(Book book) {
        cooldowns.remove(book);
    }

    public boolean canBeTargeted() {
        return canBeTargeted;
    }

    public void setCanBeTargeted(boolean canBeTargeted) {
        this.canBeTargeted = canBeTargeted;
    }
}
