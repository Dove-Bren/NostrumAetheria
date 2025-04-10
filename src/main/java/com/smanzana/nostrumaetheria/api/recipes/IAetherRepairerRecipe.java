package com.smanzana.nostrumaetheria.api.recipes;

import javax.annotation.Nonnull;

import net.minecraft.world.item.ItemStack;

/**
 * A "Recipe" for something that can be added to the repairer.
 * Used to specify how much aether to take per 'repair' and what to actually do
 * each time repairs are done.
 * @author Skyler
 *
 */
public interface IAetherRepairerRecipe {
	
	/**
	 * Check whether this recipe matches the provided input.
	 * Note: This <b>should include</b> checks that the item is in the right state.
	 * For example, if you are going to repair an item, make sure to check it's the right item <b>and</b>
	 * that it's damaged.
	 * @param stack
	 * @return
	 */
	public boolean matches(ItemStack stack);
	
	/**
	 * Return how much aether it costs per repair call.
	 * For reference, armor takes ~12 to ~60 depending on slot and material to repair 1 point of damage.
	 * Note each whole number corresponds to .01 aether as displayed in the game. (So 500 is shows as "5.00 Aether")
	 * @param stack
	 * @return
	 */
	public int getAetherCost(ItemStack stack);
	
	/**
	 * Actually perform the recipe and 'repair' the item.
	 * This method is free to modify the item or create a new one.
	 * The returned item will be placed into the repairer block.
	 * Note: Recipes can use
	 * @param stack
	 */
	public @Nonnull ItemStack repair(ItemStack stack);
	
}
