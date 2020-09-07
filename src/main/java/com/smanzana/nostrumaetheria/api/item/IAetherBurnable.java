package com.smanzana.nostrumaetheria.api.item;

import net.minecraft.item.ItemStack;

public interface IAetherBurnable {

	/**
	 * Return how long 1 of the provided item stack should burn in a regular base small aether furnace.
	 * Reagents, for example, burn for 5 seconds each (100 ticks).
	 * @param stack
	 * @return
	 */
	public int getBurnTicks(ItemStack stack);
	
	/**
	 * How much aether to produce for each of the items in this stack when burned in a regular base small aether furnace.
	 * Reagents, for example, produce 100 aether per reagent.
	 * @param stack
	 * @return
	 */
	public float getAetherYield(ItemStack stack);
	
}
