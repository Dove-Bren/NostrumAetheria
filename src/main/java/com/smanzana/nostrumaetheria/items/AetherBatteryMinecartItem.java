package com.smanzana.nostrumaetheria.items;

import javax.annotation.Nonnull;

import com.smanzana.nostrumaetheria.entity.AetheriaEntityTypes;
import com.smanzana.nostrumaetheria.entity.EntityAetherBatteryMinecart;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Basic hand-held battery
 * @author Skyler
 *
 */
public class AetherBatteryMinecartItem extends Item {

	
	
	public AetherBatteryMinecartItem() {
		super(AetheriaItems.PropBase().stacksTo(16));
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		final Player player = context.getPlayer();
		final BlockPos pos = context.getClickedPos();
		final InteractionHand hand = context.getHand();
		final Level world = context.getLevel();
		final BlockState blockstate = world.getBlockState(pos);
		
		final @Nonnull ItemStack stack = player.getItemInHand(hand);
		if(!blockstate.is(BlockTags.RAILS)) {
			return InteractionResult.FAIL;
		} else {
			if(!world.isClientSide()) {
				Minecart entityminecart = new EntityAetherBatteryMinecart(AetheriaEntityTypes.batteryCart, world);
				entityminecart.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

				if(stack.hasCustomHoverName()) {
					entityminecart.setCustomName(stack.getHoverName());
				}

				world.addFreshEntity(entityminecart);
			}

			stack.shrink(1);
			return InteractionResult.SUCCESS;
		}
	}
    
}
