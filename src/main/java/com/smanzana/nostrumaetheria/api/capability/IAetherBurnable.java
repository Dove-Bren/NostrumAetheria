package com.smanzana.nostrumaetheria.api.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public interface IAetherBurnable {
	
	public static final Capability<IAetherBurnable> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
	
	/**
	 * Return how long this should burn in a regular base small aether furnace.
	 * Reagents, for example, burn for 5 seconds each (100 ticks).
	 * @param stack
	 * @return
	 */
	public int getBurnTicks();
	
	/**
	 * How much aether to produce for each of the items in this stack when burned in a regular base small aether furnace.
	 * Reagents, for example, produce 150 aether per reagent.
	 * @param stack
	 * @return
	 */
	public float getAetherYield();
}
