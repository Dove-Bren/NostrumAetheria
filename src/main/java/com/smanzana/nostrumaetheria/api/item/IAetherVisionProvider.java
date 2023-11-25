package com.smanzana.nostrumaetheria.api.item;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public interface IAetherVisionProvider {

	public boolean shouldProvideAetherVision(ItemStack stack, PlayerEntity player, @Nullable EquipmentSlotType slot);
	
}
