package com.smanzana.nostrumaetheria.loot;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

public class AddItemMod extends LootModifier {
	
	private final Item addItem;
	private final float chance;
	private final int min;
	private final int max;
	
	private final float chancePerLoot;
	private final int countPerLoot;
	
	public AddItemMod(ILootCondition[] conditionsIn, Item addItem, float chance, int min, int max, float chancePerLoot,
			int countPerLoot) {
		super(conditionsIn);
		this.addItem = addItem;
		this.chance = chance;
		this.min = min;
		this.max = max;
		this.chancePerLoot = chancePerLoot;
		this.countPerLoot = countPerLoot;
	}
	
	@Override
	@Nonnull
	public List<ItemStack> doApply(List<ItemStack> loot, LootContext context) {
		final float rollChance = this.chance + Math.max(0f, context.getLootingModifier() * this.chancePerLoot);
		if (context.getRandom().nextFloat() < rollChance) {
			final int count = (max-min > 0 ? context.getRandom().nextInt(max-min) : 0)
					+ min
					+ Math.max(0, context.getLootingModifier() * this.countPerLoot);
			if (count > 0) {
				loot.add(new ItemStack(this.addItem, count));
			}
		}
		return loot;
	}
	
	public static class Serializer extends GlobalLootModifierSerializer<AddItemMod> {
		
		public static final String ID = "add_item";

		@Override
		public AddItemMod read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition) {
            Item addItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation((JSONUtils.getString(object, "addItem"))));
            float chance = JSONUtils.getFloat(object, "chance");
        	int min = JSONUtils.getInt(object, "min");
        	int max = JSONUtils.getInt(object, "max");
        	float chancePerLoot = JSONUtils.getFloat(object, "chancePerLoot");
        	int countPerLoot = JSONUtils.getInt(object, "countPerLoot");
        	return new AddItemMod(ailootcondition, addItem, chance, min, max, chancePerLoot, countPerLoot);
		}

		@Override
		public JsonObject write(AddItemMod instance) {
			return this.makeConditions(instance.conditions);
		}
		
	}
	
}
