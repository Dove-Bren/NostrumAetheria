package com.smanzana.nostrumaetheria.items;

import com.smanzana.nostrumaetheria.api.item.IAetherVisionProvider;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;

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
	public InteractionResult useOn(UseOnContext context) {
		return super.useOn(context);
	}

	@Override
	public boolean shouldProvideAetherVision(ItemStack stack, Player player, EquipmentSlot slot) {
		return true;
	}
    
}
