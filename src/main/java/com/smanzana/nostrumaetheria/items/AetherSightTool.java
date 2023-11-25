package com.smanzana.nostrumaetheria.items;

import com.smanzana.nostrumaetheria.api.item.IAetherVisionProvider;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

/**
 * Item that, when held, gives aether vision
 * @author Skyler
 *
 */
public class AetherSightTool extends Item implements IAetherVisionProvider {

	public AetherSightTool() {
		super(AetheriaItems.PropUnstackable());
	}
	
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		return super.onItemUse(context);
	}

	@Override
	public boolean shouldProvideAetherVision(ItemStack stack, PlayerEntity player, EquipmentSlotType slot) {
		return true;
	}
    
}
