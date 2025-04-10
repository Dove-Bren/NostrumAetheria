package com.smanzana.nostrumaetheria.recipes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonParseException;
import com.smanzana.nostrumaetheria.integration.curios.items.AetherCloakItem;
import com.smanzana.nostrumaetheria.integration.curios.items.AetheriaCurios;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;

public abstract class AetherCloakModificationRecipe extends ShapelessRecipe {
	
	protected static interface TransformFuncs {
		public boolean isAlreadySet(@Nonnull ItemStack cloak, NonNullList<ItemStack> extras);
		public @Nonnull ItemStack transform(@Nonnull ItemStack cloak, NonNullList<ItemStack> extras);
	}

	private final NonNullList<Ingredient> ingredients;
	private final TransformFuncs func;
	private @Nullable final String group;
	private final @Nonnull ItemStack displayStack;
	
	public AetherCloakModificationRecipe(ResourceLocation id, String group, @Nonnull ItemStack displayStack, NonNullList<Ingredient> ingredients, TransformFuncs func) {
		super(id, group, displayStack /* TODO this ok? ItemStack.EMPTY*/, ingredients);
		
		if (ingredients == null || ingredients.isEmpty()) {
			throw new JsonParseException("ingredients items must be provided and contain at least an Aether Cloak");
		}
		
		final ItemStack cloak = new ItemStack(AetheriaCurios.aetherCloak);
		boolean found = false;
		for (Ingredient ing : ingredients) {
			if (ing.test(cloak)) {
				found = true;
				break;
			}
		}
		
		if (!found) {
			throw new JsonParseException("At least one ingredient must allow a blank Aether Cloak");
		}
		
		if (displayStack == null || displayStack.isEmpty() || !(displayStack.getItem() instanceof AetherCloakItem)) {
			throw new JsonParseException("Display item must be an aether cloak");
		}
		
		this.ingredients = ingredients;
		this.func = func;
		this.group = group;
		this.displayStack = displayStack;
	}
	
	/**
	 * Will match one and only one cloak. If there are multiple, will return an .isEmpty itemstack.
	 * @param inv
	 * @return
	 */
	protected @Nonnull ItemStack findAetherCloak(CraftingContainer inv) {
		@Nonnull ItemStack found = ItemStack.EMPTY;
		for (int i = 0; i < inv.getContainerSize(); i++) {
			@Nonnull ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty() && stack.getItem() instanceof AetherCloakItem) {
				if (found.isEmpty()) {
					found = stack;
				} else {
					found = ItemStack.EMPTY; // Found a second! Fail out!
					break;
				}
			}
		}
		
		return found;
	}
	
	@Override
	public ItemStack assemble(CraftingContainer inv) {
		@Nonnull ItemStack result = ItemStack.EMPTY;
		@Nonnull ItemStack cloak = findAetherCloak(inv);
		
		if (!cloak.isEmpty()) {
			NonNullList<ItemStack> extras = NonNullList.create();
			
			for (int i = 0; i < inv.getContainerSize(); i++) {
				@Nonnull ItemStack stack = inv.getItem(i);
				if (!stack.isEmpty() && stack != cloak) {
					extras.add(stack);
				}
			}
			
			if (!func.isAlreadySet(cloak, extras)) {
				result = func.transform(cloak, extras);
			}
		}
		
		return result;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= ingredients.size();
	}

	@Override
	public ItemStack getResultItem() {
		return displayStack;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		return ingredients;
	}
	
	@Override
	public String getGroup() {
		return group == null ? "" : group.toString();
	}
	
	@Override
	public abstract RecipeSerializer<?> getSerializer();
	
}