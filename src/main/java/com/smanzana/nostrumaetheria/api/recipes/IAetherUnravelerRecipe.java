package com.smanzana.nostrumaetheria.api.recipes;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * A "Recipe" for something that can be put in the unraveler and taken apart.
 * Used to specify how much aether the unravel operation takes and what to spit out
 * when it's done
 * @author Skyler
 *
 */
public interface IAetherUnravelerRecipe {
	
	/**
	 * Check whether this recipe matches the provided input.
	 * Note: This <b>should include</b> checks that the item is in the right state.
	 * @param stack
	 * @return
	 */
	public boolean matches(ItemStack stack);
	
	/**
	 * Return how much aether it costs to unravel the item.
	 * For reference, a scroll takes 2000 aether to unravel.
	 * Note each whole number corresponds to .01 aether as displayed in the game. (So 500 is shows as "5.00 Aether")
	 * @param stack
	 * @return
	 */
	public int getAetherCost(ItemStack stack);
	
	/**
	 * Return how many ticks this item spends in the unraveler (with constant aether consumption) before
	 * it is finally unraveled.
	 * Note: Aether cost per tick is total cost / getDuration();
	 * @param stack
	 * @return
	 */
	public int getDuration(ItemStack stack);
	
	/**
	 * Actually perform the recipe and 'unravel' the item.
	 * The origin idea is discarded and all items returned from this method are thrown out as results.
	 * @param stack
	 */
	public @Nonnull NonNullList<ItemStack> unravel(ItemStack stack);
	
}
