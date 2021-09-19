package com.smanzana.nostrumaetheria.recipes;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.recipes.IAetherRepairerRecipe;

import net.minecraft.item.ItemStack;

public final class RepairerRecipeManager {
	
	private static final RepairerRecipeManager instance = new RepairerRecipeManager();
	
	public static final RepairerRecipeManager instance() {
		return instance;
	}
	
	private final List<IAetherRepairerRecipe> recipes;
	
	protected RepairerRecipeManager() {
		recipes = new ArrayList<>();
	}
	
	public void clearRecipes() {
		this.recipes.clear();
	}
	
	public void addRecipe(IAetherRepairerRecipe recipe) {
		recipes.add(recipe);
	}
	
	public List<IAetherRepairerRecipe> getAllRecipes() {
		return recipes;
	}
	
	public @Nullable IAetherRepairerRecipe findRecipe(@Nonnull ItemStack input) {
		if (!input.isEmpty()) {
			for (IAetherRepairerRecipe recipe : recipes) {
				if (recipe.matches(input)) {
					return recipe;
				}
			}
		}
		
		return null;
	}
}
