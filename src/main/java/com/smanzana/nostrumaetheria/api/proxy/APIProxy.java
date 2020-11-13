package com.smanzana.nostrumaetheria.api.proxy;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.blocks.AetherTileEntity;
import com.smanzana.nostrumaetheria.api.component.IAetherComponentListener;
import com.smanzana.nostrumaetheria.api.component.IAetherHandlerComponent;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class APIProxy {
	
    public static CreativeTabs creativeTab;
	public static Block InfiniteAetherBlock = null;
	public static Block AetherBatterySmallBlock = null;
	public static Block AetherBatteryMediumBlock = null;
	public static Block AetherBatteryLargeBlock = null;
	public static Block AetherBatteryGiantBlock = null;
	public static Block AetherRelay = null;
	public static Block AetherFurnaceBlock = null;
	public static Block AetherBoilerBlock = null;
	public static Block AetherBathBlock = null;
	public static Block AetherChargerBlock = null;
	public static Block AetherRepairerBlock = null;
	public static Block AetherUnravelerBlock = null;
	public static Item ActivePendantItem = null;
	public static Item PassivePendantItem = null;
	public static Item AetherGemItem = null;
	
	// This is a NostrumResearchTab, but using that type would cause a circular dependency
	public static Object ResearchTab = null;
	
	public static APIProxy handler = null;
	
	/**
	 * Check whether Nostrum Aetheria is loaded, functioning, and enabled!
	 * Not safe to call before init.
	 * @return
	 */
	public static boolean isEnabled() {
		if (handler != null) {
			return handler.handleIsEnabled();
		}
		
		return false;
	}
	
	/**
	 * Sync aether between two aether tile entities.
	 * Specifically, refresh any clients who are tracking the provided tile entity with updated aether values.
	 * @param te
	 */
	public static void syncTEAether(AetherTileEntity te) {
		if (handler != null) {
			handler.handleSyncTEAether(te);
		}
	}
	
	/**
	 * Convenience func to see whether a block is loaded.
	 * @param world
	 * @param pos
	 * @return
	 */
	public static boolean isBlockLoaded(World world, BlockPos pos) {
		if (handler != null) {
			return handler.handleIsBlockLoaded(world, pos);
		}
		
		return true;
	}
	
	/**
	 * Attempts to draw some amount of aether from the provided inventory.
	 * Returns the amount actually drawn.
	 * ignore itemstack is an optional param that, when provided, provides the item to skip (presumably because
	 * it's the one that's trying to do the drawing lol)
	 * @param inventory
	 * @param amount
	 * @param ignore
	 * @return
	 */
	public static int drawFromInventory(@Nullable World world, @Nullable Entity entity, IInventory inventory, int amount, @Nullable ItemStack ignore) {
		if (handler != null) {
			return handler.handleDrawFromInventory(world, entity, inventory, amount, ignore);
		}
		
		return 0;
	}
	
	public static IAetherHandlerComponent createHandlerComponent(IAetherComponentListener listener, int defaultAether, int defaultMaxAether) {
		if (handler != null) {
			return handler.handleCreateHandlerComponent(listener, defaultAether, defaultMaxAether);
		}
		
		// Could consider returning a shell here? Would that be easier?
		return null;
	}
	
	protected abstract boolean handleIsEnabled();
	protected abstract IAetherHandlerComponent handleCreateHandlerComponent(IAetherComponentListener listener, int defaultAether, int defaultMaxAether);
	protected abstract void handleSyncTEAether(AetherTileEntity te);
	protected abstract boolean handleIsBlockLoaded(World world, BlockPos pos);
	protected abstract int handleDrawFromInventory(@Nullable World world, @Nullable Entity entity, IInventory inventory, int amount, @Nullable ItemStack ignore);
}
