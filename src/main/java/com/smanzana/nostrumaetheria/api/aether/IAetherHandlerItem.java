package com.smanzana.nostrumaetheria.api.aether;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

public interface IAetherHandlerItem {
	
	public IAetherHandler getAetherHandler(@Nonnull ItemStack stack);
	
}
