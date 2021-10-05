package fr.cloud.magicbook.books;

import fr.cloud.magicbook.MagicBook;
import fr.cloud.magicbook.books.callables.*;
import fr.cloud.magicbook.config.Parameter;
import fr.cloud.magicbook.player.MagicBookPlayer;
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

    private static final String SEPARATOR = "#";

    @Getter private static final Set<Book> bookSet = new HashSet<>();

    public static void loadBooks() {
        bookSet.add(new Book("Geyser", "Fais apparaître un geyser sous le joueur ciblé", "geyser", 3, new GeyserCallable(), 15));
        bookSet.add(new Book("Heal", "Redonne 4 coeurs", "heal", 3, new HealCallable(), 30));
        bookSet.add(new Book("Silence", "Empêche le joueur ciblé de lancer des sorts", "silence", 3, new SilenceCallable(), 45));
        bookSet.add(new Book("Poison", "Empoisonne le joueur ciblé", "poison", 3, new PoisonCallable(), 15));
        bookSet.add(new Book("Drain", "Vole 1 coeur au joueur ciblé", "drain", 3, new DrainCallable(), 15));
        bookSet.add(new Book("Téléportation", "Téléporte au bloc visé", "teleport", 3, new TeleportCallable(), 15));
        bookSet.add(new Book("Saut", "Propulse vers l'avant", "jump", 3, new JumpCallable(), 25));
        bookSet.add(new Book("Retour accéléré", "Réduit le temps de rechargement des autres livres", "rewind", 1, new RewindCallable(), 60));
        bookSet.add(new Book("Épuisement", "Fatigue l'ennemi et l'empêche de courir", "hunger", 3, new HungerCallable(), 15));
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

        if (hiddenString.split(SEPARATOR).length != 2) {
            return null;
        }

        return bookSet.stream().filter(book -> book.getRegistryName().equals(hiddenString.split(SEPARATOR)[0])).findAny().orElse(null);
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
        final MagicBookPlayer bookPlayer = MagicBookPlayer.getPlayer(player);

        MagicBook plugin = (MagicBook) Bukkit.getPluginManager().getPlugin("MagicBook");
        if (plugin.getWorldGuardManager() != null) {
            if (!plugin.getWorldGuardManager().canPlayerCastSpell(player)) {
                player.sendMessage("§cTu ne peux pas utiliser de sort ici");
                return;
            }
        }

        if (bookPlayer.getCooldowns().containsKey(this)) {
            double cooldown = bookPlayer.getCooldown(this) - System.currentTimeMillis();
            if (cooldown > 0) {
                player.sendMessage(String.format("§cTu dois encore attendre %s secondes pour lancer ce sort.", (cooldown / 1000)));
                return;
            }

            bookPlayer.resetCooldown(this);
        }

        if (bookPlayer.isSilenced()) {
            player.sendMessage("§cTu es sous l'effet d'un silence ! Tu ne peux donc pas lancer de sorts !");
            return;
        }

        if (spell instanceof TargetCallable) {

            Player target = SpellUtils.getTarget(player, ((TargetCallable) spell).getRange());
            if (target == null || target.isDead()) {
                player.sendMessage("§cAucun joueur n'est dans la portée.");
                return;
            }

            if (plugin.getWorldGuardManager() != null) {
                if (!plugin.getWorldGuardManager().canPlayerCastSpell(target)) {
                    player.sendMessage("§cCe joueur est dans une zone protégée");
                    return;
                }
            }

            MagicBookPlayer bookTarget = MagicBookPlayer.getPlayer(target);
            if (!bookTarget.canBeTargeted()) {
                player.sendMessage("§cCe joueur ne peut pas être ciblé, il est sous l'effet d'un sort le protégeant !");
                return;
            }

            ((TargetCallable) spell).run(event, target);

        } else {
            if (!spell.run(event))
                return;
        }


        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        bookPlayer.getCooldowns().put(this, this.cooldown * 1000L + System.currentTimeMillis());
        player.setItemInHand(getStack(getAmount(event.getItem()) - 1));
    }

    public int getAmount(ItemStack stack) {
        if (identify(stack) == null) {
            return -1;
        }

        ItemMeta itemMeta = stack.getItemMeta();
        List<String> lore = itemMeta.getLore();

        String hiddenString = ChatColor.stripColor(lore.get(3));
        return Integer.parseInt(hiddenString.split(SEPARATOR)[1]);
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
        return "§0§k" + registryName + SEPARATOR + amount;
    }
}
