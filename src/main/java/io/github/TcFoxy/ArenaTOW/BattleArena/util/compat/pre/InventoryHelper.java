package io.github.TcFoxy.ArenaTOW.BattleArena.util.compat.pre;

import java.awt.Color;
import java.util.List;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import io.github.TcFoxy.ArenaTOW.BattleArena.util.compat.IInventoryHelper;

public class InventoryHelper implements IInventoryHelper{
	@Override
	public void setColor(ItemStack itemStack, Color color) {/* do nothing */}

	@Override
	public void setLore(ItemStack itemStack, List<String> lore) {/* do nothing */}

	@Override
	public void setDisplayName(ItemStack itemStack, String displayName) {/* do nothing */}

	@Override
	public void setOwnerName(ItemStack itemStack, String ownerName) {/* do nothing */}

	@Override
	public Color getColor(ItemStack itemStack) {return null;}

	@Override
	public List<String> getLore(ItemStack itemStack) {return null;}

	@Override
	public String getDisplayName(ItemStack itemStack) {return null;}

	@Override
	public String getOwnerName(ItemStack itemStack) {return null;}

    @Override
    public String getCommonNameByEnchantment(Enchantment enchantment) {return enchantment.getName();}

    @Override
    public Enchantment getEnchantmentByCommonName(String itemName) {return null;}

    @Override
    public boolean isEnderChest(InventoryType type) {return false;}
}
