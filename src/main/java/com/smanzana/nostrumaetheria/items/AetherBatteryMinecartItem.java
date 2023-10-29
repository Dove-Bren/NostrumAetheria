package com.smanzana.nostrumaetheria.items;

import javax.annotation.Nonnull;

import com.smanzana.nostrumaetheria.entities.EntityAetherBatteryMinecart;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Basic hand-held battery
 * @author Skyler
 *
 */
public class AetherBatteryMinecartItem extends Item {

	public static final String ID = "aether_battery_cart_item";
	
	
	public AetherBatteryMinecartItem() {
		super(AetheriaItems.PropBase().maxStackSize(16));
	}
	
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		final PlayerEntity player = context.getPlayer();
		final BlockPos pos = context.getPos();
		final Hand hand = context.getHand();
		final World world = context.getWorld();
		final BlockState blockstate = world.getBlockState(pos);
		
		final @Nonnull ItemStack stack = player.getHeldItem(hand);
		if(!blockstate.isIn(BlockTags.RAILS)) {
			return ActionResultType.FAIL;
		} else {
			if(!world.isRemote()) {
				MinecartEntity entityminecart = new EntityAetherBatteryMinecart(AetheriaEntities.aetherMinecart, world);
				entityminecart.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

				if(stack.hasDisplayName()) {
					entityminecart.setCustomName(stack.getDisplayName());
				}

				world.addEntity(entityminecart);
			}

			stack.shrink(1);
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}
    
}
