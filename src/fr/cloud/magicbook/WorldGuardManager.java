package fr.cloud.magicbook;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.entity.Player;

public class WorldGuardManager {

    public static StateFlag BOOK_FLAG;

    private final MagicBook plugin;

    public WorldGuardManager(MagicBook plugin) {
        this.plugin = plugin;
    }

    public void load() {

        FlagRegistry registry = WorldGuardPlugin.inst().getFlagRegistry();
        try {
            StateFlag flag = new StateFlag("use-magic-book", true);
            registry.register(flag);
            BOOK_FLAG = flag;
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get("my-custom-flag");
            if (existing instanceof StateFlag) {
                BOOK_FLAG = (StateFlag) existing;
            } else {
                StateFlag flag = new StateFlag("cloud:use-magic-book", true);
                registry.register(flag);
                BOOK_FLAG = flag;
            }
        }

        plugin.getLogger().fine(String.format("WorldGuard Flag (%s) has been loaded", BOOK_FLAG.getName()));
    }

    public boolean isPlayerAllowed(Player player) {
        RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(player.getLocation());
        return set.getRegions().stream().noneMatch(region -> region.getFlag(BOOK_FLAG) == StateFlag.State.DENY);
    }
}
