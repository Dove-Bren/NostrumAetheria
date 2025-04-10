package com.smanzana.nostrumaetheria.recipes;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.recipes.IAetherUnravelerRecipe;

import net.minecraft.world.item.ItemStack;

public final class UnravelerRecipeManager {
	
	private static final UnravelerRecipeManager instance = new UnravelerRecipeManager();
	
	public static final UnravelerRecipeManager instance() {
		return instance;
	}
	
	private final List<IAetherUnravelerRecipe> recipes;
	
	protected UnravelerRecipeManager() {
		recipes = new ArrayList<>();
	}
	
	public void clearRecipes() {
		this.recipes.clear();
	}
	
	public void addRecipe(IAetherUnravelerRecipe recipe) {
		recipes.add(recipe);
	}
	
	public List<IAetherUnravelerRecipe> getAllRecipes() {
		return recipes;
	}
	
	public @Nullable IAetherUnravelerRecipe findRecipe(@Nonnull ItemStack input) {
		if (!input.isEmpty()) {
			for (IAetherUnravelerRecipe recipe : recipes) {
				if (recipe.matches(input)) {
					return recipe;
				}
			}
		}
		
		return null;
	}
}
