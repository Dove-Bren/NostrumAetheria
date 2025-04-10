package com.smanzana.nostrumaetheria.recipes;

import javax.annotation.Nonnull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.smanzana.nostrumaetheria.integration.curios.items.AetherCloakItem.ToggleUpgrades;

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

public final class AetherCloakToggleRecipe extends AetherCloakModificationRecipe {
	
	private final ToggleUpgrades upgradeType;

	public AetherCloakToggleRecipe(ResourceLocation id, String group, @Nonnull ItemStack display, NonNullList<Ingredient> ingredients, ToggleUpgrades upgradeType) {
		super(id, group, display, ingredients, makeTransformFunc(upgradeType));
		this.upgradeType = upgradeType;
	}
	
	protected static TransformFuncs makeTransformFunc(ToggleUpgrades upgrade) {
		return new TransformFuncs() {
			@Override
			public boolean isAlreadySet(ItemStack cloak, NonNullList<ItemStack> extras) {
				return upgrade.getFunc().isSet(cloak);
			}

			@Override
			public ItemStack transform(ItemStack cloak, NonNullList<ItemStack> extras) {
				// Don't have to care about extras and can just toggle.
				cloak = cloak.copy();
				upgrade.getFunc().toggle(cloak);
				return cloak;
			}
		};
	}
	
	public ToggleUpgrades getUpgradeType() {
		return this.upgradeType;
	}
	
	@Override
	public RecipeSerializer<AetherCloakToggleRecipe> getSerializer() {
		return AetheriaCrafting.aetherCloakToggleSerializer;
	}
	
	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>>  implements RecipeSerializer<AetherCloakToggleRecipe> {
		
		public static final String ID = "aether_cloak_toggle";
		
		@Override
		public AetherCloakToggleRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
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
			
			ToggleUpgrades upgrade = null;
			try {
				upgrade = ToggleUpgrades.valueOf(toggleKey.toUpperCase());
			} catch (Exception e) {
				throw new JsonParseException("Could not find Toggleable upgrade with key [" + toggleKey + "]");
			}
			
			ItemStack displayStack = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "display"), true);
			if (displayStack == null || displayStack.isEmpty()) {
				throw new JsonParseException("\"display\" section is required and must be a valid itemstack (not ingredient)");
			}
			
			return new AetherCloakToggleRecipe(recipeId, group, displayStack, ingredients, upgrade);
		}

		@Override
		public AetherCloakToggleRecipe fromNetwork(ResourceLocation recipeId,
				FriendlyByteBuf buffer) {
			// I think I can just piggy back off of shaped recipe serializer
			ResourceLocation fakeRecipeId = new ResourceLocation(recipeId.getNamespace(), recipeId.getPath() + ".fake");
			ShapelessRecipe base = RecipeSerializer.SHAPELESS_RECIPE.fromNetwork(fakeRecipeId, buffer);
			
			// Also read toggle func key
			String toggleKey = buffer.readUtf(32767);
			ToggleUpgrades upgrade = null;
			try {
				upgrade = ToggleUpgrades.valueOf(toggleKey.toUpperCase());
			} catch (Exception e) {
				throw new JsonParseException("Could not find Toggleable upgrade with key [" + toggleKey + "]");
			}
			
			return new AetherCloakToggleRecipe(recipeId, base.getGroup(), base.getResultItem(), base.getIngredients(), upgrade);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, AetherCloakToggleRecipe recipe) {
			RecipeSerializer.SHAPELESS_RECIPE.toNetwork(buffer, recipe);
			buffer.writeUtf(recipe.getUpgradeType().name());
		}
	}
}
	
