package com.smanzana.nostrumaetheria.api.proxy;

import com.smanzana.nostrumaetheria.api.blocks.AetherTileEntity;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class APIProxy {

	public static APIProxy handler = null;
	
	protected abstract void handleSyncTEAether(AetherTileEntity te);
	public static void syncTEAether(AetherTileEntity te) {
		if (handler != null) {
			handler.handleSyncTEAether(te);
		}
	}
	
	protected abstract boolean handleIsBlockLoaded(World world, BlockPos pos);
	public static boolean isBlockLoaded(World world, BlockPos pos) {
		if (handler != null) {
			return handler.handleIsBlockLoaded(world, pos);
		}
		
		return true;
	}
	
}
