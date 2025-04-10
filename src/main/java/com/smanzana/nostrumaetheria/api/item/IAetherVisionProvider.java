package com.smanzana.nostrumaetheria.api.item;

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public interface IAetherVisionProvider {

	public boolean shouldProvideAetherVision(ItemStack stack, Player player, @Nullable EquipmentSlot slot);
	
}
