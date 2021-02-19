package fr.cloud.magicbook.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemCreator {

    private ItemStack stack;
    private ItemMeta meta;

    public ItemCreator(Material material) {
        stack = new ItemStack(material);
        meta = stack.getItemMeta();
    }

    public ItemCreator name(String name) {
        meta.setDisplayName(name);
        return this;
    }

    public ItemCreator amount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    public ItemCreator lore(List<String> lore) {
        meta.setLore(lore);
        return this;
    }

    public ItemCreator blingBling() {
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        return this;
    }

    public ItemStack getStack() {
        stack.setItemMeta(meta);
        return stack;
    }
}
