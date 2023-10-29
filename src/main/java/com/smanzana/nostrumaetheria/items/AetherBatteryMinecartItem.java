package com.smanzana.nostrumaetheria.items;

import javax.annotation.Nonnull;

import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.entities.EntityAetherBatteryMinecart;

import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Basic hand-held battery
 * @author Skyler
 *
 */
public class AetherBatteryMinecartItem extends Item {

	public static final String ID = "aether_battery_cart_item";
	
	private static AetherBatteryMinecartItem instance = null;
	public static AetherBatteryMinecartItem instance() {
		if (instance == null)
			instance = new AetherBatteryMinecartItem();
		
		return instance;
	}
	
	public AetherBatteryMinecartItem() {
		super();
		this.setUnlocalizedName(ID);
		this.setRegistryName(ID);
		this.setMaxStackSize(16);
		this.setCreativeTab(APIProxy.creativeTab);
	}
	
	@Override
	public EnumActionResult onItemUse(PlayerEntity player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		final @Nonnull ItemStack stack = player.getHeldItem(hand);
		if(BlockRailBase.isRailBlock(world.getBlockState(pos))) {
			if(!world.isRemote) {
				EntityMinecart entityminecart = new EntityAetherBatteryMinecart(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

				if(stack.hasDisplayName()) {
					entityminecart.setCustomNameTag(stack.getDisplayName());
				}

				world.spawnEntity(entityminecart);
			}

			stack.shrink(1);
			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.PASS;
	}
    
}
