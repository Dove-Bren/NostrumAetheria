package com.smanzana.nostrumaetheria.api.proxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.blocks.AetherTileEntity;
import com.smanzana.nostrumaetheria.api.component.IAetherComponentListener;
import com.smanzana.nostrumaetheria.api.component.IAetherHandlerComponent;
import com.smanzana.nostrumaetheria.api.recipes.IAetherRepairerRecipe;
import com.smanzana.nostrumaetheria.api.recipes.IAetherUnravelerRecipe;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
	public static Block AetherPumpBlock = null;
	public static Item ActivePendantItem = null;
	public static Item PassivePendantItem = null;
	public static Item AetherGemItem = null;
	public static Item AetherBatteryMinecartItem = null;
	public static Class<? extends Entity> AetherBatteryMinecart = null;
	
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
	public static int drawFromInventory(@Nullable World world, @Nullable Entity entity, IInventory inventory, int amount, @Nonnull ItemStack ignore) {
		if (handler != null) {
			return handler.handleDrawFromInventory(world, entity, inventory, amount, ignore);
		}
		
		return 0;
	}
	
	/**
	 * Attempts to charge up any aether-holding items in an inventory.
	 * Returns the amount that the inventory was charged.
	 * @param inventory
	 * @param amount
	 * @return
	 */
	public static int pushToInventory(@Nullable World world, @Nullable Entity entity, IInventory inventory, int amount) {
		if (handler != null) {
			return handler.handlePushToInventory(world, entity, inventory, amount);
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
	
	/**
	 * Register a recipe with the Aether Repairer.
	 * This should be called in the init stage.
	 * Default recipes are added in post-init. Note that recipes are given priority in the order they are
	 * registered. This means default recipes can be overriden by registering another recipe that matches the
	 * same items during init.
	 * @param recipe
	 */
	public static void addRepairerRecipe(IAetherRepairerRecipe recipe) {
		if (handler != null) {
			handler.handleAddRepairerRecipe(recipe);
		}
	}
	
	/**
	 * Register a recipe with the Aether Unraveler.
	 * This should be called in the init stage.
	 * Default recipes are added in post-init. Note that recipes are given priority in the order they are
	 * registered. This means default recipes can be overriden by registering another recipe that matches the
	 * same items during init.
	 * @param recipe
	 */
	public static void addUnravelerRecipe(IAetherUnravelerRecipe recipe) {
		if (handler != null) {
			handler.handleAddUnravelerRecipe(recipe);
		}
	}
	
	public static EntityPlayer getClientPlayer() {
		if (handler != null) {
			return handler.handleGetClientPlayer();
		}
		return null;
	}
	
	protected abstract boolean handleIsEnabled();
	protected abstract IAetherHandlerComponent handleCreateHandlerComponent(IAetherComponentListener listener, int defaultAether, int defaultMaxAether);
	protected abstract void handleSyncTEAether(AetherTileEntity te);
	protected abstract boolean handleIsBlockLoaded(World world, BlockPos pos);
	protected abstract int handleDrawFromInventory(@Nullable World world, @Nullable Entity entity, IInventory inventory, int amount, @Nonnull ItemStack ignore);
	protected abstract int handlePushToInventory(@Nullable World world, @Nullable Entity entity, IInventory inventory, int amount);
	protected abstract void handleAddRepairerRecipe(IAetherRepairerRecipe recipe);
	protected abstract void handleAddUnravelerRecipe(IAetherUnravelerRecipe recipe);
	protected abstract EntityPlayer handleGetClientPlayer();
}
