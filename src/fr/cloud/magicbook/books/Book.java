package fr.cloud.magicbook.books;

import fr.cloud.magicbook.MagicBook;
import fr.cloud.magicbook.books.callables.*;
import fr.cloud.magicbook.config.Parameter;
import fr.cloud.magicbook.utils.ItemCreator;
import fr.cloud.magicbook.utils.SpellUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Book {

    @Getter private static final Set<Book> bookSet = new HashSet<>();

    private static final Map<Book, Map<Player, Long>> cooldowns = new HashMap<>();

    public static void loadBooks() {

        bookSet.add(new Book("Geyser", "Fais apparaître un geyser sous le joueur ciblé", "geyser", 3, new GeyserCallable(), 15));
        bookSet.add(new Book("Heal", "Redonne 4 coeurs", "heal", 3, new HealCallable(), 30));
        bookSet.add(new Book("Silence", "Empêche le joueur ciblé de lancer des sorts", "silence", 3, new SilenceCallable(), 45));
        bookSet.add(new Book("Poison", "Empoisonne le joueur ciblé", "poison", 3, new PoisonCallable(), 15));
        bookSet.add(new Book("Drain", "Vole 1 coeur au joueur ciblé", "drain", 3, new DrainCallable(), 15));
        bookSet.add(new Book("Téléportation", "Téléporte au bloc visé", "teleport", 3, new TeleportCallable(), 15));
        bookSet.add(new Book("Saut", "Propulse vers l'avant", "jump", 3, new JumpCallable(), 25));

        bookSet.forEach(book -> cooldowns.put(book, new HashMap<>()));
    }

    public static void unloadPlayer(Player player) {
        if (player == null) {
            return;
        }
        bookSet.forEach(book -> cooldowns.get(book).remove(player));
    }

    public static Book getBook(String registryName) {
        return bookSet.stream().filter(book -> book.getRegistryName().equalsIgnoreCase(registryName)).findFirst().orElse(null);
    }

    public static Book identify(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        List<String> lore = meta.getLore();

        if (lore.size() != 4)
            return null;

        final String hiddenString = ChatColor.stripColor(lore.get(3));

        if (hiddenString.split("&").length != 2) {
            return null;
        }

        return bookSet.stream().filter(book -> book.getRegistryName().equals(hiddenString.split("&")[0])).findAny().orElse(null);
    }

    @Parameter @Getter private final String name, desc;
    @Getter private final String registryName;
    @Parameter @Getter private final int baseAmount, cooldown;
    @Getter private final BookCallable spell;

    public Book(String name, String desc, String registryName, int baseAmount, BookCallable spell, int cooldown) {
        this.name = name;
        this.desc = desc;
        this.registryName = registryName.toLowerCase();
        this.baseAmount = baseAmount;
        this.spell = spell;
        this.cooldown = cooldown;
    }

    public void launchSpell(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        MagicBook plugin = (MagicBook) Bukkit.getPluginManager().getPlugin("MagicBook");
        if (plugin.getWorldGuardManager() != null) {
            if (!plugin.getWorldGuardManager().isPlayerAllowed(player)) {
                player.sendMessage("§cTu ne peux pas utiliser de sort ici");
                return;
            }
        }

        if (cooldowns.get(this).containsKey(player)) {
            double cooldown = cooldowns.get(this).get(player) - System.currentTimeMillis();
            if (cooldown > 0) {
                player.sendMessage(String.format("§5Tu dois encore attendre %s secondes pour lancer ce sort.", (cooldown / 1000)));
                return;
            }

            cooldowns.get(this).remove(player);
        }

        if (player.hasMetadata("silence")) {
            player.sendMessage("§5Tu es sous l'effet d'un silence ! Tu ne peux donc pas lancer de sorts !");
            return;
        }

        if (spell instanceof TargetCallable) {

            Player target = SpellUtils.getTarget(player, ((TargetCallable) spell).getRange());
            if (target == null || target.isDead()) {
                player.sendMessage("§5Aucun joueur n'est dans la portée.");
                return;
            }

            if (plugin.getWorldGuardManager() != null) {
                if (!plugin.getWorldGuardManager().isPlayerAllowed(target)) {
                    player.sendMessage("§cCe joueur est dans une zone protégée");
                    return;
                }
            }

            ((TargetCallable) spell).run(event, target);

        } else {
            if (!spell.run(event))
                return;
        }


        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        cooldowns.get(this).put(player, this.cooldown * 1000 + System.currentTimeMillis());
        player.setItemInHand(getStack(getAmount(event.getItem()) - 1));
    }

    public int getAmount(ItemStack stack) {
        if (identify(stack) == null) {
            return -1;
        }

        ItemMeta itemMeta = stack.getItemMeta();
        List<String> lore = itemMeta.getLore();

        String hiddenString = ChatColor.stripColor(lore.get(3));
        return Integer.parseInt(hiddenString.split("&")[1]);
    }

    public ItemStack getStack() {
        return getStack(this.baseAmount);
    }

    public ItemStack getStack(int amount) {
        if (amount == 0)
            return new ItemStack(Material.AIR);
        return new ItemCreator(Material.BOOK).name("§6" + name + " §7(" + amount + " utilisations)").lore(Arrays.asList(desc, " ", "§aCe livre a encore §6" + amount + " §autilisation(s)", getHiddenString(amount))).getStack();
    }

    private String getHiddenString(int amount) {
        return "§0§k" + registryName + "&" + amount;
    }
}
