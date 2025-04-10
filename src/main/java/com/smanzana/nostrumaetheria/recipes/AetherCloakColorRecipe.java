package com.smanzana.nostrumaetheria.recipes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.integration.curios.items.AetherCloakItem.ColorUpgrades;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class AetherCloakColorRecipe extends AetherCloakModificationRecipe {
	
	private final ColorUpgrades upgradeType;
	
	public AetherCloakColorRecipe(ResourceLocation id, String group, @Nonnull ItemStack display, NonNullList<Ingredient> ingredients, ColorUpgrades upgradeType) {
		super(id, group, display, ingredients, makeTransformFunc(upgradeType));
		this.upgradeType = upgradeType;
	}
	
	public ColorUpgrades getUpgradeType() {
		return this.upgradeType;
	}
	
	protected static TransformFuncs makeTransformFunc(ColorUpgrades upgradeType) {
		return new TransformFuncs() {
			@Override
			public boolean isAlreadySet(ItemStack cloak, NonNullList<ItemStack> extras) {
				DyeColor color = findColor(extras);
				return color == null || upgradeType.getFunc().isSet(cloak, color);
			}

			@Override
			public ItemStack transform(ItemStack cloak, NonNullList<ItemStack> extras) {
				DyeColor color = findColor(extras);
				
				if (color == null) {
					NostrumAetheria.logger.error("Asked to change color on Aether Cloak with no consistent color ingredients!");
					color = DyeColor.RED;
				}
				
				cloak = cloak.copy();
				upgradeType.getFunc().set(cloak, color);
				return cloak;
			}
			
		};
	}
	
	/**
	 * Searches the passed in stacks for a consistent color.
	 * If multiple items with color are present, returns null.
	 * If no items with color are present, also returns null.
	 * @param items
	 * @return
	 */
	protected static @Nullable DyeColor findColor(NonNullList<ItemStack> items) {
		@Nullable DyeColor color = null;
		
		for (ItemStack item : items) {
			DyeColor indColor = findColor(item);
			if (indColor != null) {
				if (color == null) {
					color = indColor;
				} else if (color != indColor) {
					color = null;
					break;
				}
			}
		}
		
		return color;
	}
	
	protected static @Nullable DyeColor findColor(@Nonnull ItemStack stack) {
		@Nullable DyeColor color = null;
		
		if (!stack.isEmpty()) {
			if (stack.getItem() instanceof DyeItem) {
				color = ((DyeItem) stack.getItem()).getDyeColor();
			} // else if...
		}
		
		return color;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return AetheriaCrafting.aetherCloakColorSerializer;
	}
	
	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>>  implements RecipeSerializer<AetherCloakColorRecipe> {
		
		public static final String ID = "aether_cloak_color";
		
		@Override
		public AetherCloakColorRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			if (GsonHelper.isValidNode(json, "result")) {
				throw new JsonParseException("AetherCloak recipe cannot specify a result");
			}
			
			String group = GsonHelper.getAsString(json, "group", "");

			NonNullList<Ingredient> ingredients = NonNullList.create();
			for (JsonElement ele : GsonHelper.getAsJsonArray(json, "ingredients")) {
				ingredients.add(Ingredient.fromJson(ele));
			}

			if (ingredients.isEmpty()) {
				throw new JsonParseException("No ingredients for aether cloak recipe");
			}
			
			String toggleKey = GsonHelper.getAsString(json, "key", "");
			if (toggleKey.isEmpty()) {
				throw new JsonParseException("Must provide a 'key' field");
			}
			
			ColorUpgrades upgrade = null;
			try {
				upgrade = ColorUpgrades.valueOf(toggleKey.toUpperCase());
			} catch (Exception e) {
				throw new JsonParseException("Could not find Color upgrade with key [" + toggleKey + "]");
			}
			
			ItemStack displayStack = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "display"), true);
			if (displayStack == null || displayStack.isEmpty()) {
				throw new JsonParseException("\"display\" section is required and must be a valid itemstack (not ingredient)");
			}
			
			return new AetherCloakColorRecipe(recipeId, group, displayStack, ingredients, upgrade);
		}

		@Override
		public AetherCloakColorRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			// I think I can just piggy back off of shaped recipe serializer
			ResourceLocation fakeRecipeId = new ResourceLocation(recipeId.getNamespace(), recipeId.getPath() + ".fake");
			ShapelessRecipe base = RecipeSerializer.SHAPELESS_RECIPE.fromNetwork(fakeRecipeId, buffer);
			
			// Also read toggle func key
			String upgradeKey = buffer.readUtf(32767);
			ColorUpgrades upgrade = null;
			try {
				upgrade = ColorUpgrades.valueOf(upgradeKey.toUpperCase());
			} catch (Exception e) {
				throw new JsonParseException("Could not find Color upgrade with key [" + upgradeKey + "]");
			}
			
			return new AetherCloakColorRecipe(recipeId, base.getGroup(), base.getResultItem(), base.getIngredients(), upgrade);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, AetherCloakColorRecipe recipe) {
			RecipeSerializer.SHAPELESS_RECIPE.toNetwork(buffer, recipe);
			buffer.writeUtf(recipe.getUpgradeType().name());
		}
	}
}


